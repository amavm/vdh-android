package app.vdh.org.vdhapp.data.models

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.consts.PrefConst.STATUS_SORT_PREFS_KEY
import org.jetbrains.anko.defaultSharedPreferences

enum class Status(
    @StringRes val labelRes: Int,
    @ColorRes val colorRes: Int,
    @DrawableRes val iconRes: Int,
    @DrawableRes val markerRes: Int
) {
    CAUTION(R.string.status_caution, R.color.red, R.drawable.ic_caution_on, R.drawable.ic_caution_pin),
    SNOW(R.string.status_snow, R.color.orange, R.drawable.ic_snowy_on, R.drawable.ic_snowy_pin),
    ICE(R.string.status_ice, R.color.darkBlue, R.drawable.ic_icy_on, R.drawable.ic_icy_pin),
    CLEARED(R.string.status_clear, R.color.green, R.drawable.ic_cleared_on, R.drawable.ic_cleared_pin);

    companion object {

        fun readFromPreferences(context: Context): Status? {
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