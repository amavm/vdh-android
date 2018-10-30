package app.vdh.org.vdhapp.data.dtos

import com.google.gson.annotations.SerializedName
import java.util.*

data class ObservationDto(@SerializedName("timestamp") val timeStamp: Long,
                          @SerializedName("position") val position: Array<Double>,
                          @SerializedName("attributes")val attributes: Array<String>,
                          @SerializedName("assets") val assets: List<ImageAssetDto>,
                          @SerializedName("comment") val comment: String,
                          @SerializedName("deviceId") val deviceId: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObservationDto

        if (timeStamp != other.timeStamp) return false
        if (!Arrays.equals(position, other.position)) return false
        if (!Arrays.equals(attributes, other.attributes)) return false
        if (assets != other.assets) return false
        if (comment != other.comment) return false
        if (deviceId != other.deviceId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeStamp.hashCode()
        result = 31 * result + Arrays.hashCode(position)
        result = 31 * result + Arrays.hashCode(attributes)
        result = 31 * result + assets.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + deviceId.hashCode()
        return result
    }
}