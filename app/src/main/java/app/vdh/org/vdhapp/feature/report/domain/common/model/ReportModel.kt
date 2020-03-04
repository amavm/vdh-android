package app.vdh.org.vdhapp.feature.report.domain.common.model

import android.content.Context
import android.graphics.Bitmap
import android.os.Parcelable
import android.util.Base64
import app.vdh.org.vdhapp.feature.report.data.common.local.entity.ReportEntity
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ImageAssetDto
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.ObservationDto
import com.bumptech.glide.Glide
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.parcel.Parcelize
import java.io.ByteArrayOutputStream

@Parcelize
data class ReportModel(
    val deviceId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val id: String = deviceId + timestamp,
    val sentToSever: Boolean = false,
    val name: String? = null,
    val position: LatLng = EMPTY_LAT_LNG,
    val photoPath: String? = null,
    var comment: String? = null,
    val status: Status? = null,
    val serverId: String? = null,
    val moderationStatus: String? = null
) : Parcelable {

    companion object {
        private val EMPTY_LAT_LNG = LatLng(0.0, 0.0)
    }

    fun isPositionDefined() = position != EMPTY_LAT_LNG

    fun toObservationDto(context: Context): ObservationDto {

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

    fun toReportEntity(): ReportEntity {
        return ReportEntity(
                deviceId = deviceId,
                id = id,
                status = status,
                comment = comment,
                name = name,
                photoPath = photoPath,
                position = position,
                sentToSever = sentToSever,
                serverId = serverId,
                timestamp = timestamp
        )
    }
}