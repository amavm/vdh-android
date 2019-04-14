package app.vdh.org.vdhapp.extenstions

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import app.vdh.org.vdhapp.data.Converters
import app.vdh.org.vdhapp.data.dtos.ImageAssetDto
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayOutputStream

fun ReportEntity.toObservationDto(context: Context): ObservationDto {

    val assets = if (photoPath != null) {
        val bitmap = Glide.with(context)
                .asBitmap()
                .load(photoPath)
                .submit()
                .get()
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos)
        val photoString = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
        arrayListOf(ImageAssetDto(data = photoString))
    } else {
        arrayListOf()
    }

    val statusList = status?.let { arrayOf(it.name) }

    return ObservationDto(
            timestamp = timestamp / 1000,
            comment = comment ?: "",
            attributes = statusList ?: arrayOf(),
            position = arrayOf(position.latitude, position.longitude),
            deviceId = deviceId,
            assets = assets)
}

fun List<ObservationDto>.toReportEntities(): List<ReportEntity> {
    return filter { it.position.size == 2 }
            .map {
        val photoPath = if (it.assets?.isNotEmpty() == true) {
            it.assets[0].imageUrl
        } else null
        val status = if (it.attributes.isNotEmpty()) {
            Converters.stringToStatus(it.attributes[0].toUpperCase())
        } else null

        val report = ReportEntity(
                id = it.deviceId + it.timestamp,
                position = LatLng(it.position[0], it.position[1]),
                deviceId = it.deviceId,
                comment = it.comment,
                status = status,
                timestamp = it.timestamp * 1000,
                photoPath = photoPath,
                serverId = it.id,
                sentToSever = true)
        report
    }
}