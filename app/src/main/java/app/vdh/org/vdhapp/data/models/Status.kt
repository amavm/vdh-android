package app.vdh.org.vdhapp.data.models

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.vdh.org.vdhapp.R
import org.jetbrains.anko.defaultSharedPreferences

enum class Status(@StringRes val  labelRes: Int,
                  @ColorRes val colorRes: Int,
                  @DrawableRes val imgRes: Int) {
    CAUTION(R.string.status_caution, R.color.colorPrimaryDark, R.drawable.ic_caution),
    SNOW(R.string.status_snow, R.color.colorPrimary, R.drawable.ic_snow),
    ICE(R.string.status_ice, R.color.lightBlue, R.drawable.ic_ice),
    CLEARED( R.string.status_clear, R.color.lightYellow, R.drawable.ic_cleared);

    companion object {
        private const val STATUS_SORT_PREFS_KEY = "status_sort_prefs_key"

        fun readFromPreferences(context: Context) : Status? {
            val statusKey = context.defaultSharedPreferences.getString(STATUS_SORT_PREFS_KEY, null)
            return if (statusKey != null) Status.valueOf(statusKey) else null
        }

        fun writeInPreferences(context: Context, status: Status?) {
            if (status != null) {
                context.defaultSharedPreferences.edit()
                        .putString(STATUS_SORT_PREFS_KEY, status.name)
                        .apply()
            } else {
                context.defaultSharedPreferences.edit()
                        .remove(STATUS_SORT_PREFS_KEY)
                        .apply()
            }
        }
    }
}