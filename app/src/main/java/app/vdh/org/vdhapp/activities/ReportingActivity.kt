package app.vdh.org.vdhapp.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.states.ReportingActionState
import app.vdh.org.vdhapp.databinding.ActivityReportingBinding
import app.vdh.org.vdhapp.fragments.ProgressDialogFragment
import app.vdh.org.vdhapp.viewmodels.ReportingViewModel
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.google.android.gms.location.places.ui.PlacePicker
import kotlinx.android.synthetic.main.activity_reporting.*
import org.koin.android.viewmodel.ext.android.viewModel


class ReportingActivity : AppCompatActivity() {

    companion object {
        const val PLACE_PICKER_REQUEST = 1
        const val REPORT_ARGS_KEY = "report_args_key"
    }

    private val viewModel : ReportingViewModel by viewModel()

    private val placePickerBuilder =  PlacePicker.IntentBuilder()
    private val progressDialogFragment =  ProgressDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding : ActivityReportingBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_reporting)

        getCurrentReport()?.let {
            viewModel.setReportData(it)
        }

        with(binding) {
            setLifecycleOwner(this@ReportingActivity)
            viewModel = this@ReportingActivity.viewModel
        }

        viewModel.reportingEvent.observe(this, Observer { action ->

            when (action) {

                is ReportingActionState.PickPhoto ->
                    ImagePicker.create(this)
                            .single()
                            .showCamera(false)
                            .theme(R.style.ImagePickerTheme)
                            .returnMode(ReturnMode.ALL)
                            .start()

                is ReportingActionState.TakePhoto ->
                    ImagePicker.cameraOnly().start(this)

                is ReportingActionState.PickPlace -> {
                    val intent = placePickerBuilder.build(this)
                    startActivityForResult(intent, PLACE_PICKER_REQUEST)
                }
            }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.reporting_edit_menu, menu)
        return viewModel.syncDate.value == null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when(item.itemId) {
                R.id.menu_save_declaraton -> {
                    saveReport()
                }

                R.id.menu_send_declaraton -> {
                    saveReport(sendToServer = true)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveReport(sendToServer: Boolean = false) {
        progressDialogFragment.show(supportFragmentManager, "progress_dialog")
        viewModel.saveReport(declarationComment = commentTextInput.text.toString(), sendToServer = sendToServer,
                onSuccess = {
                    progressDialogFragment.dismiss()
                    if (sendToServer) {
                        finish()
                    } else {
                        Snackbar.make(container, it ,Snackbar.LENGTH_LONG).show()
                    }
                },
                onError = {
                    progressDialogFragment.dismiss()
                    Snackbar.make(container, it ,Snackbar.LENGTH_LONG).show()
                })
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

    private fun getCurrentReport() : ReportEntity? = intent.extras?.getParcelable(REPORT_ARGS_KEY)

}
