package de.elvah.charge.platform.bindings.features.adhoc

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesModule {

    fun providesSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("charge", Context.MODE_PRIVATE)
    }
}