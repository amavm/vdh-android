package app.vdh.org.vdhapp.databinding

import android.content.res.ColorStateList
import androidx.databinding.BindingAdapter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.widget.CompoundButtonCompat
import app.vdh.org.vdhapp.data.models.Status
import app.vdh.org.vdhapp.helpers.ImageHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.textColor


class BindingAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter("srcUrl")
        fun setImage(imageView: ImageView, url: String?) {
            url?.let {
                if (url.isNotEmpty()) {
                    if (url.startsWith("http")) {
                        Glide.with(imageView).load(url).listener(ImageHelper.getSharedImageRequestListener(imageView.context)).into(imageView)
                    } else {
                        Glide.with(imageView).asBitmap().listener(ImageHelper.getSharedImageRequestListener(imageView.context)).load(url).into(imageView)
                    }
                }
            }
        }

        @JvmStatic
        @BindingAdapter("visible")
        fun setVisibility(view: View, isVisible: Boolean) {
            view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
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

        @JvmStatic
        @BindingAdapter("status")
        fun setTextStatus(textView: TextView, status: Status?) {
            status?.let {
                textView.text = textView.context.getString(status.labelRes)
                textView.textColor = ContextCompat.getColor(textView.context, status.colorRes)
            }
        }

        @JvmStatic
        @BindingAdapter("status")
        fun setImageStatus(imageView: ImageView, status: Status?) {
            status?.let {
                imageView.setImageResource(status.imgRes)
                imageView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(imageView.context, status.colorRes))
            }
        }

        @JvmStatic
        @BindingAdapter("backgroundTint")
        fun setBackgroundTintRes(materialButton: MaterialButton, @ColorRes colorRes: Int) {
            materialButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(materialButton.context, colorRes))
        }

        @JvmStatic
        @BindingAdapter("buttonTint")
        fun setButtonTintRes(radioButton: RadioButton, @ColorRes colorRes: Int) {
            radioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(radioButton.context, colorRes))
        }

        @JvmStatic
        @BindingAdapter("textColor")
        fun setButtonTextColorRes(radioButton: RadioButton, @ColorRes colorRes: Int) {
            radioButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(radioButton.context, colorRes)))
        }
    }

}