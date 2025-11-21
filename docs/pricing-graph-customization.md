# PricingGraph Customization

The `PricingGraph` component supports full customization through color parameters and display behavior options, allowing you to match the graph appearance to your app's design system and control when the chart is displayed.

## Basic Usage

```kotlin
// Using default colors and display behavior
PricingGraph(
    siteId = "site-123"
)
```

## Display Behavior Control

The `PricingGraph` supports different display behaviors through the `graphDisplayBehavior` parameter:

```kotlin
// Always show the chart (default behavior)
PricingGraph(
    siteId = "site-123",
    graphDisplayBehavior = GraphDisplayBehavior.ALWAYS
)

// Only show the chart when dynamic pricing is available
PricingGraph(
    siteId = "site-123",
    graphDisplayBehavior = GraphDisplayBehavior.WHEN_DYNAMIC_PRICING_AVAILABLE
)
```

### GraphDisplayBehavior Options

- **`ALWAYS`** - Always show the energy price line chart regardless of dynamic pricing availability. This is the default behavior to maintain backward compatibility.
- **`WHEN_DYNAMIC_PRICING_AVAILABLE`** - Only show the energy price line chart when dynamic pricing is available for the site. If dynamic pricing is not available, the chart will be hidden and only the "Charge Now" button will be displayed.

## Custom Colors

```kotlin
// Using custom colors with display behavior
PricingGraph(
    siteId = "site-123",
    graphDisplayBehavior = GraphDisplayBehavior.WHEN_DYNAMIC_PRICING_AVAILABLE,
    colors = GraphColorDefaults.colors(
        offerSelectedLine = Color.Blue,
        offerSelectedArea = Color.Blue.copy(alpha = 0.3f),
        offerUnselectedLine = Color.Blue.copy(alpha = 0.6f),
        offerUnselectedArea = Color.Blue.copy(alpha = 0.2f),
        regularSelectedLine = Color.Gray,
        regularSelectedArea = Color.Gray.copy(alpha = 0.4f),
        regularUnselectedLine = Color.Gray.copy(alpha = 0.3f),
        regularUnselectedArea = Color.Gray.copy(alpha = 0.2f),
        verticalLine = Color.Gray.copy(alpha = 0.5f),
        currentTimeMarker = Color.Red
    )
)
```

## Color Properties

The `GraphColors` data class provides fine-grained control over all visual elements:

### Offer Colors (Special Pricing)
- **`offerSelectedLine`** - Line color for selected offer time slots
- **`offerSelectedArea`** - Fill color for selected offer time slots
- **`offerUnselectedLine`** - Line color for unselected offer time slots  
- **`offerUnselectedArea`** - Fill color for unselected offer time slots

### Regular Colors (Standard Pricing)
- **`regularSelectedLine`** - Line color for selected regular pricing slots
- **`regularSelectedArea`** - Fill color for selected regular pricing slots
- **`regularUnselectedLine`** - Line color for unselected regular pricing slots
- **`regularUnselectedArea`** - Fill color for unselected regular pricing slots

### Additional Elements
- **`verticalLine`** - Color for vertical transition lines between pricing slots
- **`currentTimeMarker`** - Color for the current time indicator (shown on today's data)

## Theme Integration

For consistent theming, you can create your own color defaults based on your app's theme:

```kotlin
@Composable
fun MyAppGraphColors(): GraphColors {
    return GraphColorDefaults.colors(
        offerSelectedLine = MyAppTheme.colors.primary,
        offerSelectedArea = MyAppTheme.colors.primary.copy(alpha = 0.3f),
        // ... other customizations
    )
}

// Usage
PricingGraph(
    siteId = "site-123",
    colors = MyAppGraphColors(),
    graphDisplayBehavior = GraphDisplayBehavior.WHEN_DYNAMIC_PRICING_AVAILABLE
)
```

## Alpha Values and Transparency

The graph uses different alpha values to create visual hierarchy:

- **Selected states**: Higher opacity (0.6f - 1.0f) for emphasis
- **Unselected states**: Lower opacity (0.2f - 0.6f) for de-emphasis  
- **Area fills**: Generally lower opacity than line colors for readability

## Default Color Scheme

By default, the graph uses:
- **Offer colors**: Brand color from `MaterialTheme.colorSchemeExtended.brand`
- **Regular colors**: Gray variants
- **Current time marker**: `MaterialTheme.colorScheme.primary`

This ensures good contrast and follows Material Design principles while highlighting special offers with your brand color.

## Best Practices

1. **Contrast**: Ensure sufficient contrast between selected and unselected states
2. **Accessibility**: Test colors for color-blind accessibility
3. **Brand Consistency**: Use your app's primary colors for offer highlighting
4. **Alpha Layering**: Keep area fills lighter than line colors for better readability
5. **Current Time**: Use a distinct color for the current time marker to help users orient themselves

## Example: Dark Theme Support

```kotlin
@Composable
fun AdaptiveGraphColors(): GraphColors {
    val isDark = isSystemInDarkTheme()
    
    return if (isDark) {
        GraphColorDefaults.colors(
            offerSelectedLine = Color(0xFF64B5F6), // Lighter blue for dark theme
            regularSelectedLine = Color(0xFFBDBDBD), // Lighter gray for dark theme
            // ... other dark theme colors
        )
    } else {
        GraphColorDefaults.colors() // Use defaults for light theme
    }
}
```