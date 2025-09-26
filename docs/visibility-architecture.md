# SDK Visibility Architecture

This document explains the visibility architecture of the Elvah Charge SDK, which follows a strict encapsulation pattern to ensure a clean and stable public API.

## Overview

The Elvah Charge SDK is designed with clear boundaries between internal implementation details and the public API surface. **Only components in the `public_api/` directory are intended for external use**.

## Public API Surface

### Entry Points Directory: `charge/src/main/java/de/elvah/charge/public_api/`

The `public_api/` directory contains the complete public API of the SDK. External developers should **only** import and use classes from this package.

#### Core Entry Points

- **`Elvah.kt`** (root package) - Main SDK initialization
- **`public_api/DisplayBehavior.kt`** - Common display behavior enumeration

#### Main Components

1. **ChargeBanner** (`public_api/banner/`)
   - `ChargeBanner.kt` - Main charging banner composable
   - `BannerVariant.kt` - Banner display variants
   - `ChargeBannerSource.kt` - Banner data source configuration

2. **ChargePointList** (`public_api/chargepoints/`)
   - `ChargePointList.kt` - Charge point list composable

3. **PricingGraph** (`public_api/pricinggraph/`)
   - `PricingGraph.kt` - Pricing visualization component

4. **Sites Management** (`public_api/sites/`)
   - `SitesManager.kt` - Sites management interface
   - `GetSites.kt` - Site retrieval functionality

### SDK Initialization

```kotlin
// Public API - Initialize the SDK
Elvah.initialize(context, config)
```

### Usage Examples

```kotlin
// Public API - Use the ChargeBanner component
@Composable
fun MyScreen() {
    ChargeBanner(
        modifier = Modifier.fillMaxWidth(),
        display = DisplayBehavior.WHEN_CONTENT_AVAILABLE,
        variant = BannerVariant.COMPACT
    )
}

// Public API - Use the ChargePointList component
@Composable
fun ChargingStationsScreen() {
    ChargePointList(
        modifier = Modifier.fillMaxSize(),
        display = DisplayBehavior.WHEN_SOURCE_SET
    )
}
```

## Internal Architecture (Not Public API)

The following packages are **internal implementation details** and should **not** be accessed directly:

### Internal Packages

- `features/` - Feature modules (adhoc_charging, payments, sites)
- `platform/` - Platform-specific implementations (config, network, UI)
- `di/` - Dependency injection modules

### Why This Separation Matters

1. **API Stability** - Internal changes don't break external integrations
2. **Backward Compatibility** - Public API remains stable across SDK versions
3. **Clean Encapsulation** - Complex internal logic is hidden from consumers
4. **Testability** - Clear boundaries enable better testing strategies

## Visibility Modifiers

The SDK uses Kotlin visibility modifiers to enforce encapsulation:

- **`public`** - Used for all classes/functions in `public_api/`
- **`internal`** - Used for implementation details within the SDK
- **`private`** - Used for class-internal implementations

## Guidelines for External Developers

### ✅ DO
- Import only from `de.elvah.charge.public_api.*` packages
- Use the provided `Elvah.initialize()` method
- Utilize the public composables (`ChargeBanner`, `ChargePointList`, etc.)
- Configure using the public `Config` class and `DisplayBehavior` enum

### ❌ DON'T
- Import from `de.elvah.charge.features.*`
- Import from `de.elvah.charge.platform.*`
- Access internal classes or functions
- Bypass the SDK initialization process

## Version Compatibility

The public API in the `public_api/` directory follows semantic versioning:

- **Major versions** - Breaking changes to public API
- **Minor versions** - New features added to public API
- **Patch versions** - Bug fixes, no API changes

Internal packages may change significantly between any version without notice.

## Support and Documentation

For questions about the public API or integration help:

1. Check the public methods and classes in `public_api/`
2. Review the demo app implementation for usage examples
3. Consult SDK configuration documentation for `Config` options

Remember: If you find yourself importing anything outside of `public_api/`, you're likely using internal APIs that may change without notice.
