# elvah Charge SDK

The elvah Charge SDK is a lightweight toolkit that enables apps to discover nearby EV charging deals
and initiate charge sessions through a fully native and seamless interface.

With just a few lines of code, you can add a `ChargeBanner` view to your app that intelligently
finds and displays nearby charging deals. The SDK handles everything from deal discovery to payment
processing and charge session management, allowing your users to charge their cars without ever
leaving your app.

## Content

1. [Installation](#installation)
    - [Gradle](#gradle)
2. **[Getting Started](#getting-started)**
    - [Campaign Banner](#campaign-banner)
    - [Campaign Source](#campaign-source)
    - [Display Behavior](#display-behavior)
    - [Banner Variants](#banner-variants)
3. [Glossary](#glossary)
4. [Legal Notice](#legal-notice)

## Installation

The SDK supports integration into projects targeting Android 24 and above.

### Requirements

- Jetpack Compose

### Gradle

In order to resolve the sdk from GitHub, you need to add the following to your `settings.gradle`
file:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' } // this line
    }
}
```

Add the following line to the dependencies in your `build.gradle` file:

```kotlin
implementation("com.github.elvah-hub:charge-sdk-android:0.2.1")
```

Alternatively, you can download the source code and place it in your project. Everything yoy need is
in charge directory.

## Getting Started

To set up the SDK, call ``Elvah.initialize()`` as early as possible in your app's lifecycle. A good
place could be the `Application` class or as soon as possible in the Activities

The configuration allows you to pass the following values:

- `apiKey`: The API key that allows the SDK to connect with elvah's backend.
- `environment`: Sets if the sdk targets the backend or any of
  the [Simulated Flows](#simulated-flows)

### Campaign Banner

The SDK's primary entry point is the `ChargeBanner` view. You can add it anywhere you want to
offer users a deal to charge their electric car nearby.

The minimal setup to integrate a `ChargeBanner` into your view hierarchy is this:

```kotlin

@Composable
fun ExampleScreen() {
    Box {
        ChargeBanner()
    }
}

```

### Campaign Source

The charge banner relies on the coordinates where the user wants to search the available
campaigns. You can set the coordinates by using `CampaignSource` class and calling the method
`sitesAt`. For example:

```kotlin
// Injects the CampaignSource into the view hierarchy
private var campaignSource: CampaignSource = CampaignSource()

campaignSource.sitesAt(
    BoundingBox(
        minLat = -87.0,
        minLng = 14.0,
        maxLat = -86.0,
        maxLng = 15.0
    )
)

// or
campaignSource.sitesAt(
    latitude = 14.09499,
    longitude = -87.19039,
    radius = 10.0
)
```

Additionally, you can query the sites by passing a list of evse ids with the `evseIds` parameter:

```kotlin
campaignSource.sitesAt(
    evseIds = listOf(EvseId("HNTCI*E*00001"))
)
```

All sites can be filtered by the `offerType` parameter:

```kotlin
campaignSource.sitesAt(
    // other filters
    offerType = OfferType.CAMPAIGN
)
```


#### Display Behavior

By default, there will be visible loading and error states inside the `ChargeBanner` view,
whenever a source is set. To change this, specify a `DisplayBehavior` in the `display` property:

```kotlin 
ChargeBanner(display: DisplayBehavior.WHEN_CONTENT_AVAILABLE)
```

Setting the `DisplayBehavior` to `DisplayBehavior.WHEN_CONTENT_AVAILABLE` can be useful when you do
not want to introduce changes to your UI until it is certain there is an active campaign available.

#### Banner Variants

The `ChargeBanner` view comes in two variants: `default` and `compact`. You can specify the
variant through a `variant` parameter:

```kotlin
ChargeBanner(variant = Variant.COMPACT)
```

## Simulator

The SDK provides a simulation mode to test common scenarios without targeting the real backend.

### Simulated flows

- [Default] flow simulates a successful charging session with typical timing.
- [StartFails] flow simulates a failure to start the charging session.
- [StopFails] flow simulates a failure to stop the charging session.
- [InterruptedCharge] flow simulates a charging session that gets interrupted unexpectedly.
- [StopRejected] flow simulates a charging session where the stop request is rejected by the charge
  point.
- [StartRejected] flow simulates a charging session where the start request is rejected by the
  charge point.

## Glossary

- **Site**: A place with one or more charge points to charge an electric car at.
- **Charge Point**: A plug used to charge an electric car.
- **Deal**: A charge point with attached pricing information and a signed agreement to charge under
  those conditions.
- **Campaign**: A site with a list of deals attached to it.
- **Charge Session**: An instance of charging an electric car at a charge point.

## Legal Notice

Please note that the contents of this repository are **not** open source and **must not** be used,
modified, or distributed without prior written permission.  
See [LEGAL_NOTICE.md](./LEGAL_NOTICE.md) for full details.
