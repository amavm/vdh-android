package app.vdh.org.vdhapp.data.dtos

import com.google.gson.annotations.SerializedName
import java.util.*

data class ObservationDto(@SerializedName("timestamp") val timestamp: Long,
                          @SerializedName("position") val position: Array<Double>,
                          @SerializedName("attributes")val attributes: Array<String>,
                          @SerializedName("assets") val assets: List<ImageAssetDto>?,
                          @SerializedName("comment") val comment: String,
                          @SerializedName("deviceId") val deviceId: String,
                          @SerializedName("id") val id: String? = null) {

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
        return result
    }
}