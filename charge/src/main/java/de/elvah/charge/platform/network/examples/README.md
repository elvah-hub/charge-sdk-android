# Network Interceptor Features

This feature allows hosting applications to register custom interceptors for various purposes:

## 1. Network Request Storage Feature
Store all network requests for testing and debugging purposes.

## 2. Custom Network Interceptors  
Add custom logic to network requests without storage requirements.

## Usage

### Step 1: Create a custom storage interceptor

```kotlin
@NetworkRequestStorage(priority = 1)
class MyCustomNetworkStorageInterceptor : NetworkStorageInterceptor {
    
    private val storedRequests = mutableListOf<StoredNetworkRequest>()
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Store the request and response for testing
        val storedRequest = StoredNetworkRequest(
            url = request.url.toString(),
            method = request.method,
            headers = request.headers.toMap(),
            body = request.body?.toString(),
            timestamp = System.currentTimeMillis(),
            responseCode = response.code,
            responseBody = response.body?.string()
        )
        
        storedRequests.add(storedRequest)
        return response
    }
    
    override fun clear() {
        storedRequests.clear()
    }
    
    override fun getStoredRequests(): List<StoredNetworkRequest> {
        return storedRequests.toList()
    }
}
```

### Step 2: Register the interceptor before initializing the SDK

```kotlin
// Register your custom storage interceptor
val myStorageInterceptor = MyCustomNetworkStorageInterceptor()
NetworkRequestStorageManager.registerStorageInterceptor(myStorageInterceptor)

// Initialize the Elvah SDK
Elvah.initialize(
    context = this,
    config = Config(
        apiKey = "your-api-key",
        environment = Environment.Int
    )
)
```

### Step 3: Access stored requests for testing

```kotlin
// Get all stored network requests
val allRequests = NetworkRequestStorageManager.getAllStoredRequests()

// Clear all stored requests
NetworkRequestStorageManager.clearAllStoredRequests()

// Get count of registered interceptors
val interceptorCount = NetworkRequestStorageManager.getStorageInterceptorsCount()
```

## Features

- **Annotation-based**: Use `@NetworkRequestStorage` to mark interceptors
- **Priority support**: Control the order of interceptors with priority parameter
- **Thread-safe**: Built with concurrent collections for multi-threaded environments
- **Flexible storage**: Implement your own storage mechanism (memory, file, database)
- **Easy integration**: Automatically integrates with the SDK's network stack

## Built-in Interceptor

The SDK provides a built-in `InMemoryNetworkStorageInterceptor` for simple use cases:

```kotlin
val inMemoryInterceptor = InMemoryNetworkStorageInterceptor()
NetworkRequestStorageManager.registerStorageInterceptor(inMemoryInterceptor)

// Access stored requests
val requests = inMemoryInterceptor.getStoredRequests()
```

## Custom Network Interceptors (without storage)

For interceptors that don't need to store requests, use the `@NetworkInterceptor` annotation:

### Step 1: Create a custom interceptor

```kotlin
@NetworkInterceptor(priority = 1)
class CustomLoggingInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        
        Log.d("CustomLogger", "→ ${request.method} ${request.url}")
        
        val response = chain.proceed(request)
        val endTime = System.currentTimeMillis()
        
        Log.d("CustomLogger", "← ${response.code} ${request.url} (${endTime - startTime}ms)")
        
        return response
    }
}
```

### Step 2: Register the interceptor

```kotlin
// Register your custom interceptor
val customLoggingInterceptor = CustomLoggingInterceptor()
CustomNetworkInterceptorManager.registerInterceptor(customLoggingInterceptor)

// Initialize the Elvah SDK
Elvah.initialize(context = this, config = Config(apiKey = "your-api-key"))
```

### Step 3: Manage custom interceptors

```kotlin
// Get count of registered custom interceptors
val interceptorCount = CustomNetworkInterceptorManager.getCustomInterceptorsCount()

// Clear all custom interceptors
CustomNetworkInterceptorManager.clearAllInterceptors()
```

## Priority System

Both storage and custom interceptors support priority ordering. Interceptors with lower priority values are executed first:

```kotlin
@NetworkInterceptor(priority = 0) // Executed first
class HighPriorityInterceptor : Interceptor { ... }

@NetworkRequestStorage(priority = 5) // Executed after custom interceptors
class StorageInterceptor : NetworkStorageInterceptor { ... }

@NetworkInterceptor(priority = 10) // Executed later
class LowPriorityInterceptor : Interceptor { ... }
```

## Execution Order

The interceptor chain executes in this order:
1. **Custom Interceptors** (`@NetworkInterceptor`) - sorted by priority
2. **Storage Interceptors** (`@NetworkRequestStorage`) - sorted by priority  
3. **SDK Internal Interceptors** (API key, distinct ID, logging)