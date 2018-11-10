package app.vdh.org.vdhapp.extenstions

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import app.vdh.org.vdhapp.data.dtos.ImageAssetDto
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
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
    val statusList = status?.let { arrayOf(it.key) }

    return ObservationDto(
            timestamp = System.currentTimeMillis() / 1000,
            comment = comment ?: "",
            attributes = statusList ?: arrayOf(),
            position = arrayOf(position.latitude, position.longitude),
            deviceId = context.uniqueId() ?: "",
            assets = listOf(ImageAssetDto(imageUrl = photoString)))
}

fun List<ObservationDto>.toReportEntities() : List<ReportEntity> {
    return map {
        val photoPath = if (it.assets.isNotEmpty()) {
            it.assets[0].imageUrl
        } else null

        val report = ReportEntity(
                position = LatLng(it.position[0], it.position[1]),
                deviceId =  it.deviceId,
                comment = it.comment,
                syncTimestamp = it.timestamp * 1000,
                photoPath = photoPath)
        report.id = it.deviceId + it.timestamp
        report
    }
}