package app.vdh.org.vdhapp.databinding

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class BindingAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter("srcUrl")
        fun setImage(imageView: ImageView, url: String?) {
            url?.let {
                if (url.isNotEmpty()) {
                    if (url.startsWith("http")) {
                        Glide.with(imageView).load(url).into(imageView)
                    } else {
                        Glide.with(imageView).asBitmap().load(url).into(imageView)
                    }
                }
            }
        }

        @JvmStatic
        @BindingAdapter("visible")
        fun setVisibility(view: View, isVisible: Boolean) {
            view.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        @JvmStatic
        @BindingAdapter(value = ["placeName", "placeLocation"])
        fun setPlace(mapView: MapView, placeName: String?, placeLocation: LatLng?) {
            if (placeName != null && placeLocation != null) {
                mapView.getMapAsync { googleMap ->
                    googleMap.uiSettings.setAllGesturesEnabled(false)
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 18.0f))

                    val marker = MarkerOptions().position(placeLocation)
                    marker.title(placeName)
                    googleMap.addMarker(marker)
                }
            }

        }
    }

}