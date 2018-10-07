package app.vdh.org.vdhapp.helpers

import com.google.android.gms.maps.model.LatLng

object GoogleStaticMapHelper {

    fun getStaticMapUrl(latLng: LatLng, apiKey: String) : String {
        return "https://maps.googleapis.com/maps/api/staticmap?center=${latLng.latitude},%20${latLng.longitude}&zoom=17&size=400x400&key=$apiKey"
    }
}