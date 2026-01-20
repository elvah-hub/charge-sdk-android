# Plan: DataStore Migration to Koin-Managed Instance

## Objetivo
Refactorizar la implementación de DataStore para que sea gestionada explícitamente por Koin en lugar de usar una property extension en Context. Esto mejorará la testabilidad y proporcionará un control más explícito sobre el ciclo de vida del DataStore.

## Contexto Actual

### Implementación Existente
El SDK actualmente usa AndroidX DataStore con Protocol Buffers para almacenamiento local:

**Archivo:** `charge/src/main/java/de/elvah/charge/features/adhoc_charging/data/local/DefaultChargingStore.kt`

```kotlin
private val Context.settingsDataStore: DataStore<ChargingSessionPrefs> by dataStore(
    fileName = "settings.pb",
    serializer = ChargingSessionPrefsSerializer,
)

internal class DefaultChargingStore(
    private val context: Context
) : ChargingStore {
    // Usa context.settingsDataStore para acceder al DataStore
}
```

### Problemas de la Implementación Actual

1. **Property Extension en Context**: El DataStore se crea como una property extension, lo que lo hace implícito y difícil de controlar
2. **Dificultad para Testing**: Es complicado proporcionar un DataStore mock o fake en tests
3. **Acoplamiento con Context**: La implementación depende directamente de la extensión del Context
4. **Falta de Control del Ciclo de Vida**: No hay un punto claro para limpiar o resetear el DataStore

## Beneficios de la Migración

1. **Mejor Testabilidad**: Inyectar DataStore permite usar fakes/mocks en tests
2. **Control Explícito**: El ciclo de vida del DataStore está gestionado por Koin
3. **Flexibilidad**: Facilita cambiar la implementación (e.g., para testing o diferentes entornos)
4. **Consistencia**: Todas las dependencias gestionadas de la misma manera
5. **Aislamiento**: El DataStore del SDK no interfiere con DataStores de la app host

## Tareas

### 1. Crear Factory para DataStore

**Archivo nuevo:** `charge/src/main/java/de/elvah/charge/features/adhoc_charging/data/local/DataStoreFactory.kt`

```kotlin
internal object DataStoreFactory {
    fun createChargingSessionDataStore(
        context: Context,
        fileName: String = "elvah_charging_session.pb"
    ): DataStore<ChargingSessionPrefs> {
        return DataStoreFactory.create(
            serializer = ChargingSessionPrefsSerializer,
            produceFile = { context.dataStoreFile(fileName) }
        )
    }
}
```

**Propósito:** Encapsular la creación del DataStore con un nombre de archivo específico del SDK.

---

### 2. Crear Módulo de Koin para DataStore

**Archivo nuevo:** `charge/src/main/java/de/elvah/charge/features/adhoc_charging/di/DataStoreModule.kt`

```kotlin
internal val dataStoreModule = module {
    single<DataStore<ChargingSessionPrefs>> {
        DataStoreFactory.createChargingSessionDataStore(
            context = androidContext(),
            fileName = "elvah_charging_session.pb"
        )
    }
}
```

**Propósito:** Definir el DataStore como una dependencia inyectable.

**Nota importante:** Usar prefijo "elvah_" en el nombre del archivo para evitar colisiones con DataStores de la app host.

---

### 3. Actualizar DefaultChargingStore

**Archivo:** `charge/src/main/java/de/elvah/charge/features/adhoc_charging/data/local/DefaultChargingStore.kt`

**Cambios:**

```kotlin
// ANTES
internal class DefaultChargingStore(
    private val context: Context
) : ChargingStore {
    override suspend fun setToken(token: String) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toBuilder().setToken(token).build()
        }
    }
}

private val Context.settingsDataStore: DataStore<ChargingSessionPrefs> by dataStore(
    fileName = "settings.pb",
    serializer = ChargingSessionPrefsSerializer,
)

// DESPUÉS
internal class DefaultChargingStore(
    private val dataStore: DataStore<ChargingSessionPrefs>
) : ChargingStore {
    override suspend fun setToken(token: String) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setToken(token).build()
        }
    }
}

// Eliminar la property extension
```

---

### 4. Actualizar el Módulo de Repositorios

**Archivo:** `charge/src/main/java/de/elvah/charge/Elvah.kt`

**Cambios:**

```kotlin
// Actualizar repositoriesModule para inyectar DataStore
private val repositoriesModule = module {
    singleOf(::DefaultChargingRepository) { bind<ChargingRepository>() }
    singleOf(::DefaultPaymentsRepository) { bind<PaymentsRepository>() }
    singleOf(::DefaultChargingStore) { bind<ChargingStore>() } // Ahora recibe DataStore del contexto
}

// Agregar dataStoreModule a la lista de módulos en initialize()
```

---

### 5. Crear Test Utilities para DataStore

**Archivo nuevo:** `charge/src/test/java/de/elvah/charge/testing/TestDataStoreFactory.kt`

```kotlin
internal object TestDataStoreFactory {
    fun createInMemoryDataStore(): DataStore<ChargingSessionPrefs> {
        return DataStoreFactory.create(
            serializer = ChargingSessionPrefsSerializer,
            produceFile = {
                File.createTempFile("test_charging_session", ".pb").also {
                    it.deleteOnExit()
                }
            }
        )
    }
}
```

**Propósito:** Facilitar tests con DataStores temporales en memoria.

---

### 6. Actualizar Tests Existentes

**Acción:** Buscar todos los tests que usan `DefaultChargingStore`.

**Cambios necesarios:**

```kotlin
// ANTES (si existía)
private val store = DefaultChargingStore(context)

// DESPUÉS
private val testDataStore = TestDataStoreFactory.createInMemoryDataStore()
private val store = DefaultChargingStore(testDataStore)
```

---

### 7. Considerar Migración de Datos (Opcional)

Si cambiamos el nombre del archivo de "settings.pb" a "elvah_charging_session.pb", necesitamos migrar datos existentes.

**Opción A: Mantener nombre original** (Recomendado para evitar romper sesiones activas)
- Pros: Sin migración necesaria, compatibilidad completa
- Contras: Posible colisión de nombres (poco probable en la práctica)

**Opción B: Migrar datos**
- Crear lógica de migración que copie datos del archivo antiguo al nuevo
- Ejecutar en la primera inicialización después de la actualización
- Requiere versioning del SDK para detectar actualizaciones

**Recomendación:** Usar Opción A inicialmente. Solo implementar Opción B si se reportan conflictos reales.

---

### 8. Documentar el Cambio

**Archivo:** `CHANGELOG.md` o release notes

```markdown
## [Versión X.Y.Z]

### Internal Changes
- Refactored DataStore to be managed by Koin dependency injection
- Improved testability of local storage layer
- No breaking changes for SDK consumers
```

---

## Orden de Ejecución Recomendado

**Prerequisito:** La migración de Koin a instancia aislada debe estar completada y funcionando.

1. Ejecutar tarea 1 (crear DataStoreFactory)
2. Ejecutar tarea 2 (crear módulo Koin)
3. Ejecutar tarea 3 (actualizar DefaultChargingStore)
4. Ejecutar tarea 4 (actualizar módulo de repositorios)
5. Ejecutar tarea 5 (crear test utilities)
6. Ejecutar tarea 6 (actualizar tests existentes)
7. Ejecutar tarea 7 (decidir sobre migración de datos)
8. Testing completo
9. Ejecutar tarea 8 (documentación)

## Consideraciones Importantes

### Thread Safety
- DataStore es thread-safe por diseño
- La inyección como singleton garantiza una única instancia

### Nombre del Archivo
- Usar prefijo "elvah_" para evitar colisiones con la app host
- Alternativamente, mantener "settings.pb" por compatibilidad

### Cleanup
- DataStore no requiere cierre explícito
- Los archivos persisten entre sesiones (comportamiento deseado)

### Testing
- Usar archivos temporales en tests
- Limpiar después de cada test (deleteOnExit)

### Compatibilidad
- No romper sesiones de charging activas
- Considerar migración de datos si se cambia el nombre del archivo

## Riesgos y Mitigaciones

| Riesgo | Impacto | Mitigación |
|--------|---------|------------|
| Pérdida de datos durante migración | Alto | Mantener nombre de archivo original o implementar migración completa |
| Tests fallan con nueva implementación | Medio | Crear TestDataStoreFactory robusto antes de migrar |
| Conflictos con DataStore de app host | Bajo | Usar nombre de archivo con prefijo único |
| Problemas de inicialización | Medio | Validar que DataStore esté disponible antes de usarlo |

## Resultado Esperado

Después de completar este plan:

- DataStore gestionado explícitamente por Koin
- Mejor testabilidad con DataStore fake/mock
- Control explícito del ciclo de vida
- Sin cambios en el comportamiento externo del SDK
- Tests más simples y confiables
- Arquitectura más limpia y mantenible

## Notas Adicionales

- Esta migración es **puramente interna** al SDK
- No afecta la API pública
- No requiere cambios en el código del consumidor del SDK
- Se puede realizar de forma incremental
- Es compatible con la migración de Koin aislado
