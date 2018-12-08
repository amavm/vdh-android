package app.vdh.org.vdhapp.data.models

import com.google.android.gms.maps.model.LatLng

class LatLngQueryParameter(private val latLng: LatLng) {

    override fun toString(): String =
            "${latLng.latitude},${latLng.longitude}"
}