package app.vdh.org.vdhapp.data.models

import com.google.android.gms.maps.model.LatLng

class BoundingBoxQueryParameter(
    val bottomLeft: LatLng,
    val topRight: LatLng
) {

    override fun toString(): String {
        return "${bottomLeft.latitude},${bottomLeft.longitude}," +
                "${topRight.latitude},${topRight.longitude}"
    }
}