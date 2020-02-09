package app.vdh.org.vdhapp.feature.report.domain.map.model

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