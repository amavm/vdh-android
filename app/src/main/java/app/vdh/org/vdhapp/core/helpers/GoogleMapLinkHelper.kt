package app.vdh.org.vdhapp.core.helpers

import com.mapbox.mapboxsdk.geometry.LatLng

object GoogleMapLinkHelper {

    fun getMapUrl(latLng: LatLng?, zoom: Int): String =
            "https://www.google.com/maps/@${latLng?.latitude},${latLng?.longitude},${zoom}z"
}