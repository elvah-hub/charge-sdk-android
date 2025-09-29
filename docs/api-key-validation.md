# API Key Validation

This document describes how the Elvah Charge SDK validates API keys and determines the appropriate environment.

## Overview

The SDK supports automatic environment detection based on API key prefixes, while also allowing explicit environment specification. API keys are validated to ensure they match the expected format for their target environment.

## API Key Formats

The SDK recognizes two types of API keys:

- **Test keys**: Start with `evpk_test`
- **Production keys**: Start with `evpk_prod`

## Environment Auto-Detection

When no environment is explicitly specified, the SDK automatically detects the environment based on the API key prefix:

```kotlin
// Auto-detects Int environment
val config = Config(apiKey = "evpk_test_your_key_here")

// Auto-detects Production environment  
val config = Config(apiKey = "evpk_prod_your_key_here")
```

### Detection Logic

1. If the API key starts with `evpk_test` → `Environment.Int`
2. If the API key starts with `evpk_prod` → `Environment.Production`
3. For any other prefix → defaults to `Environment.Int`

## Explicit Environment Specification

You can still explicitly specify an environment, which overrides auto-detection:

```kotlin
// Explicitly set Production environment
val config = Config(
    apiKey = "evpk_prod_your_key_here",
    environment = Environment.Production
)

// Use Simulator environment with any API key
val config = Config(
    apiKey = "any_key_for_testing",
    environment = Environment.Simulator(SimulatorFlow.Default)
)
```

## Validation Rules

After environment determination (auto-detected or explicit), the SDK validates the API key:

### For Int Environment
- API key **must** start with `evpk_test`
- If key starts with `evpk_prod`: throws "API Key error: You are using a production API key"
- If key has invalid format: throws "API key must start with evpk_test"

### For Production Environment
- API key **must** start with `evpk_prod`
- If key starts with `evpk_test`: throws "API Key error: You are using a test API key"
- If key has invalid format: throws "API key must start with evpk_prod"

### For Simulator Environment
- **No validation** - any API key is accepted
- Allows testing without real API keys

## Error Handling

The SDK throws `IllegalArgumentException` with descriptive messages for validation failures:

```kotlin
try {
    val config = Config(apiKey = "invalid_key")
} catch (e: IllegalArgumentException) {
    // e.message: "API key must start with evpk_test"
}

try {
    val config = Config(
        apiKey = "evpk_test_key", 
        environment = Environment.Production
    )
} catch (e: IllegalArgumentException) {
    // e.message: "API Key error: You are using a test API key"
}
```

## Usage Examples

### Recommended: Auto-Detection
```kotlin
// Simple - just provide the API key
val config = Config(apiKey = "evpk_test_abc123")
// Environment.Int is auto-detected

val config = Config(apiKey = "evpk_prod_xyz789")  
// Environment.Production is auto-detected
```

### Explicit Environment Control
```kotlin
// Override auto-detection for special cases
val config = Config(
    apiKey = "evpk_test_key",
    environment = Environment.Simulator(SimulatorFlow.PaymentFailure)
)

// Explicit production setup
val config = Config(
    apiKey = "evpk_prod_live_key",
    environment = Environment.Production,
    darkTheme = true
)
```

## Implementation Details

The validation occurs in the `Config` class constructor:

1. **Environment Determination**: `detectEnvironmentFromApiKey()` is called if no environment is provided
2. **Validation**: `validateApiKey()` ensures the API key matches the determined environment
3. **Error Handling**: Descriptive exceptions are thrown for mismatches

The auto-detection logic is implemented as a private companion function to maintain encapsulation while being testable through the public API.