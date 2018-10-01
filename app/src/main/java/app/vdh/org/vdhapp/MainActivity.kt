package app.vdh.org.vdhapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.model.Image
import com.google.android.gms.location.places.ui.PlacePicker
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        var PLACE_PICKER_REQUEST = 1
    }

    private val placePickerBuilder =  PlacePicker.IntentBuilder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        placePickerButton.setOnClickListener {
            val intent = placePickerBuilder.build(this)
            startActivityForResult(intent, PLACE_PICKER_REQUEST)

        }

        openMapsButton.setOnClickListener {
            val intent = Intent(applicationContext, MapsActivity::class.java)
            startActivity(intent)
        }

        openPhotoPickerButton.setOnClickListener {
            ImagePicker.create(this)
                    .single()
                    .returnMode(ReturnMode.ALL)
                    .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(applicationContext, data)
                placesInfoTextView.text = String.format("Place: %s", place.toString())
            }
        }

        else if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image = ImagePicker.getFirstImageOrNull(data)
            Glide.with(this).load(image.path).into(photoImageView)
        }

    }
}
