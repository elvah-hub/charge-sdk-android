package de.elvah.charge.development

import android.content.Intent
import com.chuckerteam.chucker.api.Chucker
import org.koin.core.context.GlobalContext

// Elvah SDK must be initialized before calling development tools
public object DevelopmentTools {

    public fun getHttpInspectorIntent(): Intent {
        return Chucker.getLaunchIntent(GlobalContext.get().get())
    }
}
