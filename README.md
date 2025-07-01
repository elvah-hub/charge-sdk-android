# elvah Charge SDK

The elvah Charge SDK is a lightweight toolkit that enables apps to discover nearby EV charging deals
and initiate charge sessions through a fully native and seamless interface.

With just a few lines of code, you can add a `CampaignBanner` view to your app that intelligently
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
implementation("com.github.elvah-hub:charge-sdk-android:0.1.2")
```

Alternatively, you can download the source code and place it in your project. Everything yoy need is
in charge directory.

## Getting Started

To set up the SDK, call ``Elvah.initialize()`` as early as possible in your app's lifecycle. A good
place could be the `Application` class or as soon as possible in the Activities

The configuration allows you to pass the following values:

- `apiKey`: The API key that allows the SDK to connect with elvah's backend.

### Campaign Banner

The SDK's primary entry point is the `CampaignBanner` view. You can add it anywhere you want to
offer users a deal to charge their electric car nearby.

The minimal setup to integrate a `CampaignBanner` into your view hierarchy is this:

```kotlin

@Composable
fun ExampleScreen() {
    Box {
        CampaignBanner()
    }
}

```

### Campaign Source

The campaign banner relies on the coordinates where the user wants to search the available
campaigns. You can set the coordinates by using `CampaignSource` class and calling the method
`dealsAt`. For example:

```kotlin
// Injects the CampaignSource into the view hierarchy
private var campaignSource: CampaignSource = CampaignSource()

campaignSource.dealsAt(minLat = 5.0, minLng = 4.0, maxLat = 52.0, maxLng = 50.0)

//or

campaignSource.dealsAt(
    CampaignSource.Coordinates(
        minLat = 5.0,
        minLng = 4.0,
        maxLat = 52.0,
        maxLng = 50.0
    )
)
```

#### Display Behavior

By default, there will be visible loading and error states inside the `CampaignBanner` view,
whenever a source is set. To change this, specify a `DisplayBehavior` in the `display` property:

```kotlin 
CampaignBanner(display: DisplayBehavior. WHEN_CONTENT_AVAILABLE)
```

Setting the `DisplayBehavior` to `DisplayBehavior.WHEN_CONTENT_AVAILABLE` can be useful when you do
not want to introduce changes to your UI until it is certain there is an active campaign available.

#### Banner Variants

The `CampaignBanner` view comes in two variants: `default` and `compact`. You can specify the
variant through a `variant` parameter:

```kotlin
CampaignBanner(variant = Variant.COMPACT)
```

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
