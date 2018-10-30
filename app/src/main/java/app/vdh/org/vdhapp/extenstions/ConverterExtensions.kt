package app.vdh.org.vdhapp.extenstions

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import app.vdh.org.vdhapp.data.dtos.ImageAssetDto
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream

fun ReportEntity.toObservationDto(context: Context) : ObservationDto {

    val bitmap = Glide.with(context)
            .asBitmap()
            .load(photoPath)
            .submit()
            .get()
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos)
    val photoString = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
    bitmap.recycle()

    return ObservationDto(
            timeStamp = timestamp / 1000,
            comment = comment ?: "",
            attributes = arrayOf("ice"),
            position = arrayOf(position.latitude, position.longitude),
            deviceId = "123",
            assets = listOf(ImageAssetDto(data = photoString)))
}