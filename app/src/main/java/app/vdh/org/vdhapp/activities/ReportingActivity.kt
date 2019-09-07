package app.vdh.org.vdhapp.activities

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.MenuItemCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import android.view.Menu
import android.view.MenuItem
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.actions.ReportingAction
import app.vdh.org.vdhapp.data.actions.ReportingViewAction
import app.vdh.org.vdhapp.data.models.Status
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

    private val viewModel: ReportingViewModel by viewModel()

    private val placePickerBuilder = PlacePicker.IntentBuilder()
    private val progressDialogFragment = ProgressDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityReportingBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_reporting)

        bindView(binding)

        getCurrentReport()?.let {
            viewModel.setCurrentReport(it)
        } ?: run {
            viewModel.initReport()
        }

        with(binding) {
            lifecycleOwner = this@ReportingActivity
            viewModel = this@ReportingActivity.viewModel
        }

        observeViewModelsAction()

        placePickerMapView.onCreate(savedInstanceState)
    }

    private fun bindView(binding: ActivityReportingBinding) {
        binding.placePickerIcon.setOnClickListener {
            viewModel.handleAction(ReportingAction.OpenPlacePicker)
        }

        binding.photoPickerTakeFab.setOnClickListener {
            viewModel.handleAction(ReportingAction.TakePicture)
        }

        binding.photoPickerChooseFab.setOnClickListener {
            viewModel.handleAction(ReportingAction.OpenPhotoGallery)
        }

        binding.statusBigSnowButton.setOnClickListener {
            viewModel.handleAction(ReportingAction.UpdateStatus(Status.CAUTION))
        }

        binding.statusSmallSnowButton.setOnClickListener {
            viewModel.handleAction(ReportingAction.UpdateStatus(Status.SNOW))
        }

        binding.statusIceButton.setOnClickListener {
            viewModel.handleAction(ReportingAction.UpdateStatus(Status.ICE))
        }

        binding.statusSunnyButton.setOnClickListener {
            viewModel.handleAction(ReportingAction.UpdateStatus(Status.CLEARED))
        }

        binding.deleteReportButton.setOnClickListener {
            viewModel.handleAction(ReportingAction.OpenDeleteDialog)
        }

        binding.buttonEditPlace.setOnClickListener {
            viewModel.handleAction(ReportingAction.EditPlace)
        }

        binding.buttonEditPicture.setOnClickListener {
            viewModel.handleAction(ReportingAction.EditPhoto)
        }

        binding.buttonEditStatus.setOnClickListener {
            viewModel.handleAction(ReportingAction.EditStatus)
        }
    }

    private fun observeViewModelsAction() {
        viewModel.reportingViewViewAction.observe(this, Observer { action ->

            when (action) {
                is ReportingViewAction.PickPhoto -> {
                    ImagePicker.create(this)
                            .single()
                            .showCamera(false)
                            .theme(R.style.ImagePickerTheme)
                            .returnMode(ReturnMode.ALL)
                            .start()
                }
                is ReportingViewAction.TakePhoto -> ImagePicker.cameraOnly().start(this)
                is ReportingViewAction.PickPlace -> {
                    val intent = placePickerBuilder.build(this)
                    startActivityForResult(intent, PLACE_PICKER_REQUEST)
                }
                is ReportingViewAction.OpenDeleteDialog -> {
                    // TODO : Show destructive dialog
                    progressDialogFragment.show(supportFragmentManager, "progress_dialog")
                    viewModel.handleAction(ReportingAction.DeleteReport)
                }
                is ReportingViewAction.DeleteReportSuccess -> {
                    progressDialogFragment.dismiss()
                    finish()
                }
                is ReportingViewAction.DeleteReportError -> {
                    progressDialogFragment.dismiss()
                    Snackbar.make(container, action.message, Snackbar.LENGTH_LONG).show()
                }
                is ReportingViewAction.SaveReportSuccess -> {
                    progressDialogFragment.dismiss()
                    if (action.sentToServer) {
                        finish()
                    } else {
                        Snackbar.make(container, action.message, Snackbar.LENGTH_LONG).show()
                    }
                }
                is ReportingViewAction.SaveReportError -> {
                    progressDialogFragment.dismiss()
                    Snackbar.make(container, action.message, Snackbar.LENGTH_LONG).show()
                }
            }
        })
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
                viewModel.handleAction(ReportingAction.UpdatePlace(name = place.name.toString(), location = place.latLng))
            }
        } else if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val image = ImagePicker.getFirstImageOrNull(data)
            viewModel.handleAction(ReportingAction.UpdatePicture(image.path))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val isReportSentToServer = viewModel.currentReport.value?.sentToSever ?: false
        menuInflater.inflate(R.menu.reporting_edit_menu, menu)
        val shareMenuItem = menu?.findItem(R.id.menu_share_declaraton)
        menu?.findItem(R.id.menu_send_declaraton)?.isVisible = !isReportSentToServer
        menu?.findItem(R.id.menu_save_declaraton)?.isVisible = !isReportSentToServer
        if (isReportSentToServer) {
            val shareActionProvider = MenuItemCompat.getActionProvider(shareMenuItem) as ShareActionProvider
            shareActionProvider.setShareIntent(viewModel.getShareIntent())
        } else {
            shareMenuItem?.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (item.itemId) {
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
        viewModel.handleAction(ReportingAction.SaveReport(sendToServer))
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

    private fun getCurrentReport(): ReportEntity? = intent.extras?.getParcelable(REPORT_ARGS_KEY)
}
