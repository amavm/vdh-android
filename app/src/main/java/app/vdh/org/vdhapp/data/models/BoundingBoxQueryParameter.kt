package app.vdh.org.vdhapp.data.models

import com.google.android.gms.maps.model.LatLng

class BoundingBoxQueryParameter(val topLeft: LatLng,
                                val topRight: LatLng,
                                val bottomRight: LatLng,
                                val bottomLeft: LatLng) {

    override fun toString(): String {
        return "${topLeft.latitude},${topLeft.longitude}," +
                "${topRight.latitude},${topRight.longitude}," +
                "${bottomLeft.latitude},${bottomLeft.longitude}," +
                "${bottomRight.latitude},${bottomRight.longitude}"
    }
}