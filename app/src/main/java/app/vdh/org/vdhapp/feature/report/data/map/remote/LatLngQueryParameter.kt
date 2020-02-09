package app.vdh.org.vdhapp.feature.report.data.map.remote

import com.google.android.gms.maps.model.LatLng

class LatLngQueryParameter(private val latLng: LatLng) {

    override fun toString(): String =
            "${latLng.latitude},${latLng.longitude}"
}