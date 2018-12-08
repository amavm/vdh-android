package app.vdh.org.vdhapp.data.models

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import app.vdh.org.vdhapp.R

enum class Status(val key: String,
                  @StringRes val  labelRes: Int,
                  @ColorRes val colorRes: Int,
                  @DrawableRes val imgRes: Int) {
    BIG_SNOW("snow", R.string.status_big_snow, R.color.colorPrimaryDark, R.drawable.ic_big_snow),
    SMALL_SNOW("small_snow", R.string.status_small_snow, R.color.colorPrimary, R.drawable.ic_small_snow),
    ICE("ice", R.string.status_ice, R.color.lightBlue, R.drawable.ic_ice),
    OK("ok", R.string.status_ok, R.color.lightYellow, R.drawable.ic_sun);
}