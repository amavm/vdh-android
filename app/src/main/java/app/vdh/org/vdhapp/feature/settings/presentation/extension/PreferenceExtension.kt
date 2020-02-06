package app.vdh.org.vdhapp.feature.settings.presentation.extension

import android.content.Context
import app.vdh.org.vdhapp.core.consts.PrefConst
import org.jetbrains.anko.defaultSharedPreferences
import java.util.UUID

fun Context.uniqueId(): String =
        defaultSharedPreferences.getString(PrefConst.UNIQUE_ID_PREF_KEY, null)?.let {
            it
        } ?: run {
            val uniqueId = UUID.randomUUID().toString()
            defaultSharedPreferences.edit()
                    .putString(PrefConst.UNIQUE_ID_PREF_KEY, uniqueId)
                    .apply()
            uniqueId
        }
