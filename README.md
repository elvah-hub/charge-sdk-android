[![JitPack](https://jitpack.io/v/com.github.elvah-hub/charge-sdk-android.svg)](https://jitpack.io/#/com.github.elvah-hub/charge-sdk-android)

# elvah Charge SDK

The elvah Charge SDK helps you integrate EV charging into your app. With just a few lines of code, you can add a `ChargeBanner` view to your app that intelligently finds and displays nearby charging deals. The SDK handles everything from deal discovery to payment processing and charge session management, allowing your users to charge their cars without ever leaving your app.

## Content

1. [Installation](#installation)
    - [Gradle](#gradle)
2. **[Getting Started](#getting-started)**
    - [Charge Banner](#charge-banner)
    - [Data Sources](#data-sources)
    - [Customization](#customization)
3. [Simulator](#simulator)
4. [Glossary](#glossary)
5. [FAQ](#faq)
6. [API Reference](#api-reference)
7. [Legal Notice](#legal-notice)

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
implementation("com.github.elvah-hub:charge-sdk-android:0.3.4")
```

Alternatively, you can download the source code and place it in your project. Everything you need is
in charge directory.

## Getting Started

To get started with the elvah Charge SDK, you need to initialize it by calling the `Elvah.initialize()` method as early as possible in your app's lifecycle. A good place for this is your `Application` class.

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Elvah.initialize(
            apiKey = "YOUR_API_KEY",
            environment = Environment.Int
        )
    }
}
```

The configuration allows you to pass the following values:

- `apiKey`: The API key that allows the SDK to connect with elvah's backend.
- `environment`: Sets if the sdk targets the backend or any of the [Simulated Flows](#simulated-flows)

### Charge Banner

The SDK's primary entry point is the `ChargeBanner` view. You can add it anywhere you want to offer users a deal to charge their electric car nearby.

```kotlin
@Composable
fun ExampleScreen() {
    val chargeBannerSource = ChargeBannerSource()

    LaunchedEffect(Unit) {
        chargeBannerSource.sitesAt(
            latitude = 52.520008,
            longitude = 13.404954,
            radius = 10.0
        )
    }

    ChargeBanner(
        display = DisplayBehavior.WHEN_CONTENT_AVAILABLE
    )
}
```

### Data Sources

The elvah Charge SDK provides two ways to get information about charging sites:

*   **`ChargeBannerSource`**: This data source is used to find charging sites based on the user's location or a bounding box. It is used by the `ChargeBanner` to display nearby charging deals.
*   **`GetSites`**: This use case allows you to get a list of sites based on a list of EVSE IDs. This is useful if you want to build your own custom UI for displaying charging sites.

#### ChargeBannerSource

The `ChargeBannerSource` can be used to find charging sites within a certain radius of the user's location or within a bounding box.

```kotlin
// Find sites within a 10km radius of the user's location
chargeBannerSource.sitesAt(
    latitude = 52.520008,
    longitude = 13.404954,
    radius = 10.0
)

// Find sites within a bounding box
chargeBannerSource.sitesAt(
    BoundingBox(
        minLat = 52.5,
        minLng = 13.4,
        maxLat = 52.6,
        maxLng = 13.5
    )
)
```

Additionally, you can query the sites by passing a list of evse ids with the `evseIds` parameter:

```kotlin
chargeBannerSource.sitesAt(
    evseIds = listOf(EvseId("HNTCI*E*00001"))
)
```

All sites can be filtered by the `offerType` parameter:

```kotlin
chargeBannerSource.sitesAt(
    // other filters
    offerType = OfferType.CAMPAIGN
)
```

#### GetSites

The `GetSites` use case allows you to get a list of sites based on a list of EVSE IDs.

```kotlin
val getSites = GetSites()

val sites = getSites(
    GetSites.Params(
        evseIds = listOf(EvseId("HNTCI*E*00001"))
    )
)
```

Once you have the list of sites, you can use the `SitesManager` to open a specific site.

```kotlin
SitesManager.openSite(context, sites.first().id)
```

### Customization

The `ChargeBanner` can be customized to fit the look and feel of your app. You can customize the display behavior and the banner variant.

#### Display Behavior

By default, the `ChargeBanner` will show a loading indicator while it is searching for charging sites and an error message if it fails to find any. You can change this behavior by using the `display` property.

```kotlin
// Show the banner only when there is content available
ChargeBanner(display = DisplayBehavior.WHEN_CONTENT_AVAILABLE)
```

#### Banner Variants

The `ChargeBanner` view comes in two variants: `default` and `compact`. You can specify the variant through a `variant` parameter:

```kotlin
ChargeBanner(variant = BannerVariant.COMPACT)
```

## Simulator

The SDK provides a simulation mode to test common scenarios without targeting the real backend. You can enable the simulation mode by setting the `environment` to `Environment.Simulator` when you initialize the SDK.

### Simulated Flows

The simulator provides a number of simulated flows that you can use to test your app in different scenarios.

-   **`DEFAULT`**: This flow simulates a successful charging session with typical timing.
-   **`START_FAILS`**: This flow simulates a failure to start the charging session.
-   **`STOP_FAILS`**: This flow simulates a failure to stop the charging session.
-   **`INTERRUPTED_CHARGE`**: This flow simulates a charging session that gets interrupted unexpectedly.
-   **`STOP_REJECTED`**: This flow simulates a charging session where the stop request is rejected by the charge point.
-   **`START_REJECTED`**: This flow simulates a charging session where the start request is rejected by the charge point.

## Glossary

- **Site**: A place with one or more charge points to charge an electric car at.
- **Charge Point**: A plug used to charge an electric car.
- **Deal**: A charge point with attached pricing information and a signed agreement to charge under
  those conditions.
- **Campaign**: A site with a list of deals attached to it.
- **Charge Session**: An instance of charging an electric car at a charge point.

## FAQ

## Legal Notice

Please note that the contents of this repository are **not** open source and **must not** be used,
modified, or distributed without prior written permission.  
See [LEGAL_NOTICE.md](./LEGAL_NOTICE.md) for full details.
