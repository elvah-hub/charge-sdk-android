package de.elvah.charge.sdk

import android.content.Context
import androidx.compose.ui.graphics.Color
import de.elvah.charge.Elvah
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.ui.theme.colors.CustomColorScheme

internal fun Context.initializeSdk() {
    Elvah.initialize(
        context = this,
        config = Config(
            apiKey = "evpk_test_Syx9tZW1LGcIA7Js0BADiFg7HVDpUGk2CDYPd6zLKOqrSfEI0GIVzInH7W4WfoATHcPnEW7O3uP0GrsGL0IpeUjBf72BuYwanBJ4EUZTv",
            // Example of custom color schemes - these override the default Material theme colors
            customLightColorScheme = CustomColorScheme(
                primary = Color(0xFF6200EE),
                secondary = Color(0xFF03DAC6),
                background = Color(0xFFF5F5F5),
                surface = Color.White,
                // Extended colors
                brand = Color(0xFF6200EE),
                onBrand = Color.White,
                success = Color(0xFF4CAF50),
                onSuccess = Color.White,
            ),
            customDarkColorScheme = CustomColorScheme(
                primary = Color(0xFFBB86FC),
                secondary = Color(0xFF03DAC6),
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                // Extended colors
                brand = Color(0xFFBB86FC),
                onBrand = Color.Black,
                success = Color(0xFF4CAF50),
                onSuccess = Color.Black,
            ),
        ),
    )
}
