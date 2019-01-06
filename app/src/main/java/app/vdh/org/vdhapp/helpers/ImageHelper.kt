package app.vdh.org.vdhapp.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v4.content.FileProvider
import app.vdh.org.vdhapp.BuildConfig
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.ByteArrayOutputStream
import java.io.File

object ImageHelper {

    private const val SHARE_IMAGE_PATH = "vdh_report_share_picture.jpeg"

    fun getSharedImageUri(context: Context) : Uri? {
            val picture = File(context.cacheDir, SHARE_IMAGE_PATH)
            return FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", picture)

    }

    fun <T> getSharedImageRequestListener(context: Context) : RequestListener<T>  {
        return object : RequestListener<T> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<T>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: T?, model: Any?, target: Target<T>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                val bitmapDrawable = resource as? BitmapDrawable
                val bitmap = bitmapDrawable?.bitmap ?: (resource as? Bitmap)

                bitmap?.let {
                    val bos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
                    val picture = File(context.cacheDir, SHARE_IMAGE_PATH)
                    picture.delete()
                    picture.writeBytes(bos.toByteArray())
                }
                return false
            }

        }
    }
}