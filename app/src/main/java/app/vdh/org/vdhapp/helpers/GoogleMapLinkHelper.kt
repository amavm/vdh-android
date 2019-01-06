package app.vdh.org.vdhapp.helpers

import com.google.android.gms.maps.model.LatLng

object GoogleMapLinkHelper {

    fun getMapUrl(latLng: LatLng?, zoom: Int) : String =
            "https://www.google.com/maps/@${latLng?.latitude},${latLng?.longitude},${zoom}z"

}