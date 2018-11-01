package app.vdh.org.vdhapp.extenstions

import android.content.Context
import app.vdh.org.vdhapp.consts.PrefConst
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

fun Context.uniqueId() : String? =
        if (defaultSharedPreferences.contains(PrefConst.UNIQUE_ID_PREF_KEY)) {
            defaultSharedPreferences.getString(PrefConst.UNIQUE_ID_PREF_KEY, null)
        } else  {
            val uniqueId = UUID.randomUUID().toString()
            defaultSharedPreferences.edit()
                    .putString(PrefConst.UNIQUE_ID_PREF_KEY, uniqueId)
                    .apply()
            uniqueId
        }

