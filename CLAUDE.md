# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

- `./gradlew build` - Build the entire project (both app and charge library)
- `./gradlew :charge:build` - Build only the charge library module
- `./gradlew :app:build` - Build only the app module
- `./gradlew test` - Run unit tests for all modules
- `./gradlew :charge:test` - Run unit tests for charge module only
- `./gradlew connectedAndroidTest` - Run instrumented tests
- `./gradlew publishToMavenLocal` - Publish the charge library to local Maven repository

## Git Hooks

The project includes a pre-commit hook that runs lint and Detekt checks before allowing commits. This helps catch issues early and prevents polluting the remote repository with code that doesn't meet quality standards.

### Installation
To install the git hooks, run:
```bash
./gradlew installGitHooks
```

Or set up the entire project with:
```bash
./gradlew setupProject
```

The pre-commit hook will be automatically checked when you sync the project. If it's not installed, you'll see a reminder message.

### What the Hook Does
The pre-commit hook:
- ✅ Runs Detekt static analysis on changed Kotlin files
- ✅ Runs Android Lint on the charge module (always)
- ✅ Runs Android Lint on the app module (if app files changed)
- ✅ Provides clear error messages with instructions
- ✅ Skips checks if no Kotlin/Gradle files are changed
- ⚡ Optimized to run quickly by checking only changed files

### Bypassing the Hook
In emergencies, you can bypass the hook with:
```bash
git commit --no-verify
```

**Note:** Only use `--no-verify` when absolutely necessary. The hook exists to catch issues before they reach CI.

## Project Architecture

This is an Android SDK project with two main modules:

### Modules Structure
- **`charge/`** - The main SDK library module that gets published
- **`app/`** - Demo application that demonstrates SDK usage

### SDK Architecture (charge module)

The SDK follows a clean architecture pattern with dependency injection using Koin:

**Features Structure:**
- `features/adhoc_charging/` - Core charging session functionality
- `features/payments/` - Payment processing with Stripe integration
- `features/sites/` - EV charging site discovery and management

Each feature follows this structure:
- `data/` - Remote API calls, local storage, repositories, mappers
- `domain/` - Business logic, use cases, models, interfaces
- `ui/` - Compose screens, view models, UI components
- `di/` - Dependency injection modules

**Platform Layer:**
- `platform/config/` - Configuration and environment management
- `platform/network/` - Retrofit/OkHttp setup with API key interceptors
- `platform/simulator/` - Test simulation modes for development
- `platform/ui/` - Reusable UI components and theme system
  - `platform/ui/components/graph/line/` - Energy pricing graph components with state management
  - `platform/ui/components/graph/line/state/` - State classes for graph interaction and data management

**Key Components:**
- `Elvah.kt` - Main SDK initialization entry point
- `entrypoints/` - Public SDK interfaces (ChargeBanner, ChargePointList)
- `public_api/pricinggraph/` - PricingGraph public API with visibility controls
- Uses Arrow for functional programming patterns
- Protocol Buffers for local data storage
- MVI pattern for state management

### Entry Points
The SDK provides three main entry points:
- `ChargeBanner` - Widget for displaying nearby charging deals
- `ChargePointList` - Component for listing charging points
- `PricingGraph` - Component for displaying energy pricing charts with dynamic visibility controls

### Configuration
SDK is configured via `Config` class with:
- API key (required)
- Environment (Int/Production/Simulator modes)
- Optional dark theme setting

### Testing
- Unit tests use JUnit, MockK, and Turbine for Flow testing
- Simulator module provides fake implementations for testing scenarios
- Test files organized to mirror the source structure

## API Docs
### Discovery services
Related to `features/sites/`. It returns the information for sites, charging points and their pricing.

Swagger docs: https://discovery.backend.int.elvah.de/v3/api-docs/Public%20API

## Coding Standards

### Visibility Modifiers
The SDK uses **explicit API mode** which requires all functions and classes to have explicit visibility modifiers. When implementing new code:

- **ALWAYS** declare visibility modifiers explicitly (`public`, `internal`, `private`, `protected`)
- Use `internal` for SDK implementation details that should not be exposed to consumers
- Use `public` only for APIs that should be accessible to SDK users
- Use `private` for implementation details within a single class/file
- Never omit visibility modifiers when declaring classes or functions if the class is public

Example:
```kotlin
// Correct - explicit visibility
internal class MyRepository {
    fun getData(): String = "data"
    private fun helper(): Unit = Unit
}

public class MyPublicRepository {
    public fun getData(): String = "data"
    private fun helper(): Unit = Unit
}

// Incorrect - will fail to compile
class MyRepository {
    fun getData(): String = "data"
}
```

### Graph Components
The SDK includes energy pricing graph components with enhanced visibility controls:

**PricingGraph API:**
- `shouldShowSiteDetails` - Controls visibility of site information header
- `shouldShowChart` - Controls visibility of the actual graph visualization  
- `graphDisplayBehavior` - Controls when the graph should be displayed based on dynamic pricing availability

**State Management:**
New state classes in `platform/ui/components/graph/line/state/`:
- `ChartState` - Manages overall chart state including data, selection, and pricing
- `SelectionState` - Handles slot selection state and pricing calculations
- `SlotClickResult` - Result data from slot interaction processing
- `PriceRange` - Price range calculations for chart scaling
- `PlugType` - Type definitions for charging plug configurations

These components support conditional visibility, allowing the graph to be hidden when dynamic pricing is not available while maintaining the pricing information display.
