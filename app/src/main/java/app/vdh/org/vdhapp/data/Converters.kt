package app.vdh.org.vdhapp.data

import android.arch.persistence.room.TypeConverter
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

}