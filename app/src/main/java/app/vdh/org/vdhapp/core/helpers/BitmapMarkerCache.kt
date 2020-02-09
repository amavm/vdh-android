package app.vdh.org.vdhapp.core.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

object BitmapMarkerCache {

    private val cache: LruCache<Int, Bitmap> = LruCache(4)

    fun getBitmapMarker(context: Context, @DrawableRes markerRes: Int): Bitmap? {
        return cache.get(markerRes) ?: run {
            val bitmap = markerRes.toBitmap(context)
            bitmap?.let {
                cache.put(markerRes, bitmap)
            }
            return bitmap
        }
    }

    private fun Int.toBitmap(context: Context): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, this) ?: return null
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bm = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        drawable.draw(canvas)
        return bm
    }
}