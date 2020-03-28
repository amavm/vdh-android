package app.vdh.org.vdhapp.feature.report.presentation.map.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.core.MapLocationManager
import app.vdh.org.vdhapp.core.consts.PrefConst
import app.vdh.org.vdhapp.core.consts.PrefConst.HOURS_SORT_PREFS_KEY
import app.vdh.org.vdhapp.core.consts.PrefConst.STATUS_SORT_PREFS_KEY
import app.vdh.org.vdhapp.core.extenstion.navigateTo
import app.vdh.org.vdhapp.core.extenstion.openBottomDialogFragment
import app.vdh.org.vdhapp.core.helpers.MapBoxHelper
import app.vdh.org.vdhapp.core.helpers.MapBoxHelper.addMarkerImages
import app.vdh.org.vdhapp.core.helpers.MapBoxHelper.centerOnUser
import app.vdh.org.vdhapp.core.helpers.MapBoxHelper.enableLocation
import app.vdh.org.vdhapp.databinding.ActivityReportMapBinding
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import app.vdh.org.vdhapp.feature.report.domain.map.model.BikePathNetwork
import app.vdh.org.vdhapp.feature.report.domain.map.model.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.feature.report.presentation.map.action.ReportMapAction
import app.vdh.org.vdhapp.feature.report.presentation.map.action.ReportMapFilterViewAction
import app.vdh.org.vdhapp.feature.report.presentation.map.action.ReportMapViewAction
import app.vdh.org.vdhapp.feature.report.presentation.map.fragment.HourFilterDialogFragment
import app.vdh.org.vdhapp.feature.report.presentation.map.fragment.StatusFilterDialogFragment
import app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel.ReportMapViewModel
import app.vdh.org.vdhapp.feature.report.presentation.reporting.activity.ReportingActivity
import app.vdh.org.vdhapp.feature.report.presentation.settings.activity.SettingsActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_report_map.*
import org.jetbrains.anko.defaultSharedPreferences
import org.koin.android.viewmodel.ext.android.viewModel

class ReportMapActivity : AppCompatActivity(), PermissionsListener {

    companion object {
        private const val DEFAULT_MAP_ZOOM = 16.0
        private const val BIKE_PATH_ON_MAP_ZOOM = 13.0f
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private val viewModel: ReportMapViewModel by viewModel()

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            STATUS_SORT_PREFS_KEY -> setCurrentFilterFromSharedPrefs()
            HOURS_SORT_PREFS_KEY -> setCurrentFilterFromSharedPrefs()
        }
    }

    private var permissionsManager: PermissionsManager? = null
    private lateinit var locationManager: MapLocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, resources.getString(R.string.mapbox_key))

        val binding: ActivityReportMapBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_report_map)

        with(binding) {
            lifecycleOwner = this@ReportMapActivity
            viewModel = this@ReportMapActivity.viewModel
        }

        bindView(binding)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapView = map_view as MapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { map ->
            mapboxMap = map
            configureMap(map)
        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(prefsListener)
        locationManager = MapLocationManager(
                context = this,
                lifecycle = lifecycle
        ) { _, firstUpdate ->
            if (firstUpdate) {
                mapboxMap?.centerOnUser(DEFAULT_MAP_ZOOM)
                locationManager.stop()
            }
        }
    }

    private fun configureMap(mapboxMap: MapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            mapboxMap.addMarkerImages(this)
            permissionsManager = mapboxMap.enableLocation(this, this, this) {
                locationManager.enable()
                my_location_button.setOnClickListener {
                    mapboxMap.centerOnUser(DEFAULT_MAP_ZOOM)
                }
                setupMapLayers()
            }
        }
    }

    private fun setupMapLayers() {
        setCurrentFilterFromSharedPrefs()
        mapView?.let {
            observeViewModelActions(it)
        }

        viewModel.reports.observe(this, Observer { reports ->
            if (reports.isNotEmpty()) {
                mapboxMap?.let { map ->
                    mapView?.let { mapView ->
                        MapBoxHelper.addMarkers(
                                mapboxMap = map,
                                mapView = mapView,
                                reports = reports
                        ) { report ->
                            val bundle = Bundle()
                            bundle.putParcelable(ReportingActivity.REPORT_ARGS_KEY, report)
                            navigateTo(ReportingActivity::class.java, bundle)
                        }
                    }
                }
            }
        })
    }

    private fun observeViewModelActions(map: MapView) {

        viewModel.mapReportViewAction.observe(this, Observer { viewAction ->
            when (viewAction) {
                is ReportMapViewAction.OpenReportCreation -> {
                    val userLocation = mapboxMap?.locationComponent?.lastKnownLocation
                    val bundle = Bundle()
                    userLocation?.let {
                        bundle.putParcelable(ReportingActivity.USER_POS_ARGS_KEY, userLocation)
                    }
                    navigateTo(ReportingActivity::class.java, bundle)
                }
                is ReportMapViewAction.BicyclePathQuerySuccess -> {} // Display bicycle Path
                is ReportMapViewAction.BicyclePathQueryError -> {}
            }
        })

        viewModel.reportMapFilterViewAction.observe(this, Observer { viewAction ->
            when (viewAction) {
                is ReportMapFilterViewAction.OpenStatusFilter -> {
                    this.openBottomDialogFragment(StatusFilterDialogFragment.newInstance(viewAction.status), "status_filter_framgent")
                }
                is ReportMapFilterViewAction.OpenHourFilter -> {
                    this.openBottomDialogFragment(HourFilterDialogFragment.newInstance(viewAction.hoursAgo), "status_filter_framgent")
                }
                is ReportMapFilterViewAction.RefreshPathNetwork -> {
                    Snackbar.make(map_coordinator_layout, viewAction.bikePathNetwork.label, Snackbar.LENGTH_LONG).show()
                    mapboxMap?.let {
                        addBicyclePath(it)
                    }
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun bindView(binding: ActivityReportMapBinding) {
        binding.createReportButton.setOnClickListener {
            viewModel.handleAction(ReportMapAction.CreateReport)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_filter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item?.let {
            when (item.itemId) {
                R.id.menu_filter_hours -> {
                    viewModel.handleAction(ReportMapAction.FilterByHour)
                }

                R.id.menu_filter_status -> {
                    viewModel.handleAction(ReportMapAction.FilterByStatus)
                }

                R.id.menu_filter_path -> {
                    viewModel.handleAction(ReportMapAction.ChangeBikePath())
                }
                R.id.menu_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addBicyclePath(map: MapboxMap) {
        if (map.cameraPosition.zoom >= BIKE_PATH_ON_MAP_ZOOM) {
            val visibleRegion = map.projection.visibleRegion
            val queryParameter = BoundingBoxQueryParameter(
                    topRight = visibleRegion.latLngBounds.northEast,
                    bottomLeft = visibleRegion.latLngBounds.southWest
            )
            viewModel.handleAction(ReportMapAction.GetBicyclePath(queryParameter))
        }
    }

    private fun setCurrentFilterFromSharedPrefs() {
        val status = Status.readFromPreferences(this)
        val hourFilterStatus = defaultSharedPreferences.getInt(HOURS_SORT_PREFS_KEY, PrefConst.HOURS_SORT_DEFAULT_VALUE)
        viewModel.currentFilterViewActionReport.value = ReportMapFilterViewAction.ReportFilterPickedReport(status, hourFilterStatus)
        // TODO get this value from shared prefs
        viewModel.handleAction(ReportMapAction.ChangeBikePath(BikePathNetwork.FOUR_SEASONS))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap?.let {
                configureMap(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(prefsListener)
        mapView?.onDestroy()
    }
}
