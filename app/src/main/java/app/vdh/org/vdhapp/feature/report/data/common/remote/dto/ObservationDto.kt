package app.vdh.org.vdhapp.feature.report.data.common.remote.dto

import app.vdh.org.vdhapp.feature.report.data.common.local.Converters
import app.vdh.org.vdhapp.feature.report.data.common.local.entity.ReportEntity
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class ObservationDto(
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("position") val position: Array<Double>,
    @SerializedName("attributes")val attributes: Array<String>,
    @SerializedName("assets") val assets: List<ImageAssetDto>?,
    @SerializedName("comment") val comment: String,
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("id") val id: String? = null,
    @SerializedName("status") val moderationStatus: ModerationStatus? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObservationDto

        if (timestamp != other.timestamp) return false
        if (!position.contentEquals(other.position)) return false
        if (!attributes.contentEquals(other.attributes)) return false
        if (assets != other.assets) return false
        if (comment != other.comment) return false
        if (deviceId != other.deviceId) return false
        if (id != other.id) return false
        if (moderationStatus != other.moderationStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + position.contentHashCode()
        result = 31 * result + attributes.contentHashCode()
        result = 31 * result + assets.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + deviceId.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + moderationStatus.hashCode()
        return result
    }

    fun toReportEntity(): ReportEntity {
        val photoPath = if (assets?.isNotEmpty() == true) {
            assets[0].imageUrl
        } else null
        val status = if (attributes.isNotEmpty()) {
            Converters.stringToStatus(attributes[0].toUpperCase())
        } else null
        return ReportEntity(
                id = deviceId + timestamp,
                position = LatLng(position[0], position[1]),
                deviceId = deviceId,
                comment = comment,
                status = status,
                timestamp = timestamp * 1000,
                photoPath = photoPath,
                serverId = id,
                sentToSever = true,
                moderationStatus = moderationStatus?.adminStatus
        )
    }
}

fun List<ObservationDto>.toReportEntities(): List<ReportEntity> {
    return filter { it.position.size == 2 }
            .map { it.toReportEntity() }
}