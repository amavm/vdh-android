package app.vdh.org.vdhapp.data

import androidx.room.TypeConverter
import app.vdh.org.vdhapp.data.models.Status
import com.google.android.gms.maps.model.LatLng

class Converters {

    @TypeConverter
    fun positionToString(latLng: LatLng) : String {
        return "${latLng.latitude}|${latLng.longitude}"
    }

    @TypeConverter
    fun positionFromString(position: String) : LatLng {
        val positions = position.split("|")
        return LatLng(positions[0].toDouble(),positions[1].toDouble())
    }

    @TypeConverter
    fun statusToString(status: Status?) : String? {
        status?.let {
            return status.key
        }
        return null
    }

    @TypeConverter
    fun stringToStatus(statusKey: String?) : Status? {
        return Companion.stringToStatus(statusKey)
    }

    companion object {
        fun stringToStatus(statusKey: String?) : Status? {
            return when (statusKey) {
                Status.BIG_SNOW.key -> Status.BIG_SNOW
                Status.SMALL_SNOW.key -> Status.SMALL_SNOW
                Status.ICE.key -> Status.ICE
                Status.OK.key -> Status.OK
                else -> null
            }
        }
    }
}