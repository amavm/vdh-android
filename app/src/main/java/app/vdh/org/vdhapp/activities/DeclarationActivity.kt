package app.vdh.org.vdhapp.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.databinding.ActivityDeclarationBinding
import app.vdh.org.vdhapp.viewmodels.DeclarationViewModel
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.google.android.gms.location.places.ui.PlacePicker
import kotlinx.android.synthetic.main.activity_declaration.*
import org.koin.android.viewmodel.ext.android.viewModel


class DeclarationActivity : AppCompatActivity() {

    companion object {
        var PLACE_PICKER_REQUEST = 1
    }

    private val viewModel : DeclarationViewModel by viewModel()

    private val placePickerBuilder =  PlacePicker.IntentBuilder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding : ActivityDeclarationBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_declaration)

        with(binding) {
            setLifecycleOwner(this@DeclarationActivity)
            viewModel = this@DeclarationActivity.viewModel
        }


        viewModel.openPlacePickerEvent.observe(this, Observer {
            val intent = placePickerBuilder.build(this)
            startActivityForResult(intent, PLACE_PICKER_REQUEST)
        })

        viewModel.openPhotoPickerEvent.observe(this, Observer {
            ImagePicker.create(this)
                    .single()
                    .returnMode(ReturnMode.ALL)
                    .start()
        })

        placePickerMapView.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        placePickerMapView.onStart()

    }

    override fun onResume() {
        super.onResume()
        placePickerMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        placePickerMapView.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(applicationContext, data)
                viewModel.setPlace(place)
            }
        }

        else if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image = ImagePicker.getFirstImageOrNull(data)
            viewModel.setPhoto(image.path)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        placePickerMapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        placePickerMapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        placePickerMapView.onLowMemory()
    }
}
