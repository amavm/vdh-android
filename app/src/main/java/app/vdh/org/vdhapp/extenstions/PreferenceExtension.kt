package app.vdh.org.vdhapp.extenstions

import android.content.Context
import app.vdh.org.vdhapp.consts.PrefConst
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
