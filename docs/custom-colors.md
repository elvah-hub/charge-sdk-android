# Custom Color Schemes

The Elvah Charge SDK supports custom color schemes that allow you to override the default Material Design theme colors to match your app's branding and design system.

## Overview

Custom color schemes are optional parameters in the SDK configuration that let you:
- Override Material 3 color tokens (primary, secondary, background, etc.)
- Customize SDK-specific extended colors (brand, success, decorativeStroke, etc.)
- Provide separate color schemes for light and dark themes
- Maintain consistent branding across your app and the SDK components

## Configuration

Add custom color schemes when initializing the SDK:

```kotlin
import de.elvah.charge.Elvah
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.ui.theme.colors.CustomColorScheme
import androidx.compose.ui.graphics.Color

Elvah.initialize(
    context = context,
    config = Config(
        apiKey = "your_api_key_here",
        customLightColorScheme = CustomColorScheme(/* light theme colors */),
        customDarkColorScheme = CustomColorScheme(/* dark theme colors */)
    )
)
```

## Color Properties

### Material 3 Colors

Standard Material 3 color tokens that affect the overall theme:

```kotlin
CustomColorScheme(
    // Primary colors
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE7D8FF),
    onPrimaryContainer = Color(0xFF21005D),
    
    // Secondary colors
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFCCF8F3),
    onSecondaryContainer = Color(0xFF002E2A),
    
    // Background and surfaces
    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFEFBFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    
    // Other semantic colors
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    // Additional surface colors
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    surfaceDim = Color(0xFFDDD8E1),
    surfaceBright = Color(0xFFFEFBFF),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F2FA),
    surfaceContainer = Color(0xFFF1ECF4),
    surfaceContainerHigh = Color(0xFFEBE6EE),
    surfaceContainerHighest = Color(0xFFE6E1E9),
)
```

### Extended Colors

SDK-specific colors for specialized components:

```kotlin
CustomColorScheme(
    // Brand colors for SDK-specific branding
    brand = Color(0xFF6200EE),
    onBrand = Color.White,
    brandLight = Color(0xFFE7D8FF),
    
    // Success states
    success = Color(0xFF4CAF50),
    onSuccess = Color.White,
    
    // Decorative elements
    decorativeStroke = Color(0xFFE0E0E0),
)
```

## Example Configurations

### Minimal Configuration

Override only the essential colors:

```kotlin
Config(
    apiKey = "your_api_key",
    customLightColorScheme = CustomColorScheme(
        primary = Color(0xFF1976D2),
        secondary = Color(0xFF0D47A1),
        brand = Color(0xFF1976D2)
    ),
    customDarkColorScheme = CustomColorScheme(
        primary = Color(0xFF90CAF9),
        secondary = Color(0xFF42A5F5),
        brand = Color(0xFF90CAF9)
    )
)
```

### Complete Brand Theme

Full color customization for comprehensive branding:

```kotlin
Config(
    apiKey = "your_api_key",
    customLightColorScheme = CustomColorScheme(
        // Primary brand colors
        primary = Color(0xFF1976D2),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE3F2FD),
        onPrimaryContainer = Color(0xFF0D47A1),
        
        // Secondary accent colors  
        secondary = Color(0xFF0288D1),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE1F5FE),
        onSecondaryContainer = Color(0xFF01579B),
        
        // Background theme
        background = Color(0xFFFAFAFA),
        onBackground = Color(0xFF212121),
        surface = Color.White,
        onSurface = Color(0xFF212121),
        
        // Extended brand colors
        brand = Color(0xFF1976D2),
        onBrand = Color.White,
        brandLight = Color(0xFFE3F2FD),
        success = Color(0xFF4CAF50),
        onSuccess = Color.White,
        decorativeStroke = Color(0xFFE0E0E0),
    ),
    customDarkColorScheme = CustomColorScheme(
        // Primary brand colors (dark theme)
        primary = Color(0xFF90CAF9),
        onPrimary = Color(0xFF0D47A1),
        primaryContainer = Color(0xFF1565C0),
        onPrimaryContainer = Color(0xFFE3F2FD),
        
        // Secondary accent colors (dark theme)
        secondary = Color(0xFF81D4FA),
        onSecondary = Color(0xFF01579B),
        secondaryContainer = Color(0xFF0277BD),
        onSecondaryContainer = Color(0xFFE1F5FE),
        
        // Background theme (dark)
        background = Color(0xFF121212),
        onBackground = Color(0xFFE0E0E0),
        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFE0E0E0),
        
        // Extended brand colors (dark theme)
        brand = Color(0xFF90CAF9),
        onBrand = Color(0xFF0D47A1),
        brandLight = Color(0xFF1565C0),
        success = Color(0xFF66BB6A),
        onSuccess = Color(0xFF1B5E20),
        decorativeStroke = Color(0xFF424242),
    )
)
```

### Industry-Specific Themes

#### Financial App Theme
```kotlin
CustomColorScheme(
    primary = Color(0xFF2E7D32), // Green primary
    secondary = Color(0xFF1B5E20), // Darker green
    brand = Color(0xFF2E7D32),
    success = Color(0xFF4CAF50),
    error = Color(0xFFD32F2F), // Red for warnings
)
```

#### Healthcare App Theme  
```kotlin
CustomColorScheme(
    primary = Color(0xFF1976D2), // Medical blue
    secondary = Color(0xFF0288D1),
    brand = Color(0xFF1976D2),
    success = Color(0xFF388E3C),
    brandLight = Color(0xFFE3F2FD),
)
```

#### Gaming App Theme
```kotlin
CustomColorScheme(
    primary = Color(0xFF7B1FA2), // Purple primary
    secondary = Color(0xFFE91E63), // Pink secondary
    brand = Color(0xFF7B1FA2),
    brandLight = Color(0xFFF3E5F5),
    decorativeStroke = Color(0xFF9C27B0),
)
```

## Best Practices

### Color Accessibility
- Ensure sufficient contrast ratios between text and background colors
- Test your color schemes with accessibility tools
- Consider color blindness when choosing color combinations

### Theme Consistency
- Use the same base colors across light and dark themes where possible
- Adjust brightness and saturation appropriately for dark themes
- Maintain semantic meaning (e.g., red for errors, green for success)

### Brand Integration
- Align colors with your app's existing design system
- Use your brand's primary colors for the `primary` and `brand` properties
- Consider how SDK components will look alongside your app's UI

### Performance Considerations
- Only override colors that need customization
- Use `null` for colors you want to keep as default
- The SDK will fallback to default values for any unspecified colors

## Color Testing

Test your custom colors in different contexts:

1. **Light and Dark Themes**: Ensure both theme variants look good
2. **Different Screen Sizes**: Test on phones and tablets  
3. **Accessibility**: Use high contrast modes and screen readers
4. **Real Content**: Test with actual charging station data and pricing information

## Migration Guide

If you're updating from a version without custom color support:

1. Your existing configuration will continue to work unchanged
2. Add custom color schemes gradually, starting with primary brand colors
3. Test thoroughly before deploying to production
4. Consider creating color scheme presets for different app variants

## Troubleshooting

### Colors Not Applying
- Verify you're passing the custom color schemes to the correct Config parameters
- Check that colors are specified using `Color()` constructor with proper values
- Ensure you're not overriding the colors elsewhere in your app

### Poor Contrast
- Use online contrast checkers to validate your color combinations
- Test with real content and different lighting conditions
- Consider providing high contrast alternatives

### Theme Inconsistencies  
- Make sure dark theme colors complement each other
- Test the transition between light and dark themes
- Verify colors work well with your app's existing UI elements