# Plan de Acción: Migrar SDK a Koin Aislado

## Objetivo
Migrar el SDK de Kotlin/Android para usar una instancia aislada de Koin en lugar del Koin global, evitando conflictos cuando la app host también usa Koin.

## Contexto
- SDK usa Koin para inyección de dependencias
- Actualmente usa `startKoin()` lo que causa conflictos con apps que también usan Koin
- Se usa `by inject()` principalmente en puntos de entrada
- El SDK necesita permitir que la app inyecte dependencias personalizadas

## Tareas

### 1. Crear el wrapper de Koin aislado
**Archivo:** `MySDKKoin.kt` (crear en el paquete internal del SDK)
````kotlin
internal object MySDKKoin {
    private lateinit var koinApp: KoinApplication
    private var isInitialized = false
    
    fun init(externalModules: List<Module> = emptyList()) {
        if (isInitialized) {
            koinApp.close()
        }
        
        koinApp = koinApplication {
            modules(getSDKModules() + externalModules)
        }
        isInitialized = true
    }
    
    fun getKoin(): Koin = koinApp.koin
    
    fun close() {
        if (isInitialized) {
            koinApp.close()
            isInitialized = false
        }
    }
    
    private fun getSDKModules(): List<Module> {
        // Aquí irán todos los módulos internos del SDK
        return listOf(
            // sdkCoreModule,
            // sdkNetworkModule,
            // sdkDataModule,
            // etc.
        )
    }
}
````

**Propósito:** Encapsular la instancia de Koin del SDK, separada del Koin global.

---

### 2. Identificar todos los módulos de Koin existentes
**Acción:** Buscar en el proyecto todos los archivos que definen módulos de Koin.

**Buscar:**
- Declaraciones de `val xxxModule = module { ... }`
- Archivos que contengan definiciones de Koin (`single`, `factory`, `viewModel`, etc.)

**Resultado esperado:** Lista completa de módulos del SDK que deben agregarse a `getSDKModules()`.

---

### 3. Actualizar la inicialización del SDK
**Archivo:** Clase principal de inicialización del SDK (típicamente `MySDK.kt` o `MySDKInitializer.kt`)

**Cambios:**
- Eliminar cualquier llamada a `startKoin()`
- Agregar llamada a `MySDKKoin.init()`
- Agregar parámetro opcional para módulos externos
````kotlin
class MySDK {
    companion object {
        @Volatile
        private var initialized = false
        
        fun initialize(
            context: Context,
            customModules: List<Module> = emptyList()
        ) {
            if (initialized) return
            
            // Inicializar Koin aislado del SDK
            MySDKKoin.init(customModules)
            
            // Resto de inicialización del SDK
            // ...
            
            initialized = true
        }
        
        fun shutdown() {
            MySDKKoin.close()
            initialized = false
        }
    }
}
````

---

### 4. Crear helper para inyección en puntos de entrada
**Archivo:** `SDKInjection.kt` (crear en el paquete internal del SDK)
````kotlin
internal inline fun <reified T : Any> sdkInject(): Lazy<T> {
    return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MySDKKoin.getKoin().get()
    }
}

internal inline fun <reified T : Any> sdkGet(): T {
    return MySDKKoin.getKoin().get()
}

internal inline fun <reified T : Any> sdkGet(qualifier: Qualifier): T {
    return MySDKKoin.getKoin().get(qualifier)
}
````

**Propósito:** Simplificar el acceso a dependencias del Koin aislado.

---

### 5. Actualizar puntos de entrada con inyección
**Acción:** Buscar todos los usos de `by inject()` en el código del SDK.

**Buscar:**
- `by inject()`
- `get()`
- Cualquier referencia directa a Koin global

**Reemplazar:**
````kotlin
// ANTES
private val repository: MyRepository by inject()

// DESPUÉS
private val repository: MyRepository by sdkInject()
````
````kotlin
// ANTES
private val useCase = get<MyUseCase>()

// DESPUÉS
private val useCase = sdkGet<MyUseCase>()
````

---

### 6. Verificar y actualizar tests
**Acción:** Revisar los tests unitarios que usan Koin.

**Cambios necesarios:**
````kotlin
// En cada test que use Koin
@Before
fun setup() {
    MySDKKoin.init(listOf(testModule))
}

@After
fun tearDown() {
    MySDKKoin.close()
}
````

**Alternativa para tests:**
````kotlin
internal fun testSDKKoin(modules: List<Module>, testBody: () -> Unit) {
    MySDKKoin.init(modules)
    try {
        testBody()
    } finally {
        MySDKKoin.close()
    }
}

// Uso en test
@Test
fun `test my feature`() = testSDKKoin(listOf(testModule)) {
    val service = sdkGet<MyService>()
    // ... test
}
````

---

### 7. Documentar la API pública para inyección externa
**Archivo:** `README.md` o documentación del SDK

**Agregar sección:**
````markdown
## Configuración Personalizada con Koin

Si tu app ya usa Koin y quieres inyectar dependencias personalizadas al SDK:

### Ejemplo: Inyectar un HttpClient personalizado
```kotlin
val customSDKModule = module {
    single<HttpClient> { 
        // Tu implementación personalizada
        MyCustomHttpClient() 
    }
}

MySDK.initialize(
    context = applicationContext,
    customModules = listOf(customSDKModule)
)
```

### Dependencias que puedes sobreescribir
- `HttpClient`: Cliente HTTP para networking
- `DatabaseProvider`: Proveedor de base de datos
- [Listar otras dependencias configurables]
````

---

### 8. Crear interfaces públicas para dependencias inyectables
**Acción:** Si no existen, crear interfaces públicas para las dependencias que la app puede personalizar.

**Ejemplo:**
````kotlin
// API pública del SDK
interface SDKHttpClient {
    suspend fun get(url: String): Result<String>
    suspend fun post(url: String, body: String): Result<String>
}

// Implementación interna por defecto
internal class DefaultSDKHttpClient : SDKHttpClient {
    // implementación
}

// En el módulo interno
internal val networkModule = module {
    single<SDKHttpClient> { DefaultSDKHttpClient() }
}
````

---

## Checklist de Verificación

- [ ] `MySDKKoin.kt` creado con `koinApplication` aislado
- [ ] Todos los módulos del SDK identificados y agregados a `getSDKModules()`
- [ ] `startKoin()` eliminado de la inicialización del SDK
- [ ] `MySDKKoin.init()` agregado a la inicialización
- [ ] Helper `sdkInject()` y `sdkGet()` creados
- [ ] Todos los `by inject()` reemplazados por `by sdkInject()`
- [ ] Todos los `get<>()` reemplazados por `sdkGet<>()`
- [ ] Tests actualizados para usar `MySDKKoin`
- [ ] Documentación creada para inyección de dependencias personalizadas
- [ ] Interfaces públicas creadas para dependencias configurables
- [ ] Probado en app que también usa Koin global

## Orden de Ejecución Recomendado

1. Ejecutar tareas 1, 2, 4 (crear infraestructura)
2. Ejecutar tarea 3 (actualizar inicialización)
3. Ejecutar tarea 5 (migrar código existente)
4. Ejecutar tarea 6 (actualizar tests)
5. Ejecutar tareas 7 y 8 (documentación y API pública)
6. Testing completo en app de prueba

## Notas Importantes

- **No romper compatibilidad:** Si el SDK ya está publicado, considera deprecar la inicialización antigua antes de eliminarla
- **Thread safety:** El código propuesto usa `lazy(LazyThreadSafetyMode.SYNCHRONIZED)` para seguridad en hilos
- **Shutdown:** Incluir método `shutdown()` permite limpiar recursos si es necesario
- **Validation:** Agregar validación `isInitialized` antes de acceder a Koin para dar mensajes de error claros

## Resultado Esperado

Después de completar este plan:
- El SDK tendrá su propia instancia aislada de Koin
- No habrá conflictos con apps que usen Koin globalmente
- La app podrá inyectar dependencias personalizadas si lo necesita
- El código del SDK seguirá usando Koin de forma transparente
- Los tests funcionarán correctamente con el Koin aislado
