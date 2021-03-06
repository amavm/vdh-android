package app.vdh.org.vdhapp.feature.report.data.common.local

import androidx.room.TypeConverter
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception

class Converters {

    @TypeConverter
    fun positionToString(latLng: LatLng): String {
        return "${latLng.latitude}|${latLng.longitude}"
    }

    @TypeConverter
    fun positionFromString(position: String): LatLng {
        val positions = position.split("|")
        return LatLng(positions[0].toDouble(), positions[1].toDouble())
    }

    @TypeConverter
    fun statusToString(status: Status?): String? {
        status?.let {
            return status.name
        }
        return null
    }

    @TypeConverter
    fun stringToStatus(statusKey: String?): Status? {
        return Companion.stringToStatus(statusKey)
    }

    companion object {
        fun stringToStatus(statusKey: String?): Status? {
            if (statusKey != null) {
                return try {
                    Status.valueOf(statusKey)
                } catch (e: Exception) {
                    null
                }
            }
            return null
        }
    }
}