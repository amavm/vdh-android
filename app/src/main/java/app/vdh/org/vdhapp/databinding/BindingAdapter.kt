package app.vdh.org.vdhapp.databinding

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions


class BindingAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter("srcUrl")
        fun setImage(imageView: ImageView, url: String?) {
            url?.let {
                if (url.isNotEmpty()) {
                    Glide.with(imageView).asBitmap().load(url).into(imageView)
                }
            }
        }

        @JvmStatic
        @BindingAdapter("visible")
        fun setVisibility(view: View, isVisible: Boolean) {
            view.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        @JvmStatic
        @BindingAdapter("place")
        fun setPlace(mapView: MapView, place: Place?) {
            place?.let {
                mapView.getMapAsync { googleMap ->
                    googleMap.uiSettings.setAllGesturesEnabled(false)
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 18.0f))

                    val marker = MarkerOptions().position(place.latLng)
                    place.name?.let { name->
                        marker.title(name.toString())
                    }
                    googleMap.addMarker(marker)
                }
            }

        }
    }

}