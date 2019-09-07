package app.vdh.org.vdhapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.consts.PrefConst
import app.vdh.org.vdhapp.consts.PrefConst.HOURS_SORT_PREFS_KEY
import app.vdh.org.vdhapp.consts.PrefConst.STATUS_SORT_PREFS_KEY
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.actions.ReportMapAction
import app.vdh.org.vdhapp.data.actions.ReportMapFilterViewAction
import app.vdh.org.vdhapp.data.actions.ReportMapViewAction
import app.vdh.org.vdhapp.data.models.BikePathNetwork
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.models.Status
import app.vdh.org.vdhapp.databinding.ActivityReportMapBinding
import app.vdh.org.vdhapp.extenstions.addReportMarkers
import app.vdh.org.vdhapp.extenstions.navigateTo
import app.vdh.org.vdhapp.extenstions.openBottomDialogFragment
import app.vdh.org.vdhapp.fragments.HourFilterDialogFragment
import app.vdh.org.vdhapp.fragments.StatusFilterDialogFragment
import app.vdh.org.vdhapp.viewmodels.ReportMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.android.synthetic.main.activity_report_map.*
import org.jetbrains.anko.defaultSharedPreferences
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel

class ReportMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_CODE = 123
        private const val DEFAULT_MAP_ZOOM = 16.0f
        private const val BIKE_PATH_ON_MAP_ZOOM = 13.0f
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var map: GoogleMap? = null
    private val viewModel: ReportMapViewModel by viewModel()

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            STATUS_SORT_PREFS_KEY -> setCurrentFilterFromSharedPrefs()
            HOURS_SORT_PREFS_KEY -> setCurrentFilterFromSharedPrefs()
        }
    }

    private var geoJsonLayer: GeoJsonLayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityReportMapBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_report_map)

        with(binding) {
            lifecycleOwner = this@ReportMapActivity
            viewModel = this@ReportMapActivity.viewModel
        }

        bindView(binding)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(prefsListener)
    }

    private fun bindView(binding: ActivityReportMapBinding) {
        binding.createReportButton.setOnClickListener {
            viewModel.handleAction(ReportMapAction.CreateReport)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        } else {
            setupMap()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupMap() {

        setCurrentFilterFromSharedPrefs()
        addReports()

        map?.let { map ->

            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), DEFAULT_MAP_ZOOM))
                }
            }
            map.setOnMarkerClickListener {
                val bundle = Bundle()
                bundle.putParcelable(ReportingActivity.REPORT_ARGS_KEY, it.tag as ReportEntity)
                this.navigateTo(ReportingActivity::class.java, bundle)
                true
            }

            map.setOnCameraIdleListener {
                addBicyclePath(map)
            }

            observeViewModelActions(map)
        }
    }

    private fun observeViewModelActions(map: GoogleMap) {

        viewModel.mapReportViewAction.observe(this, Observer { viewAction ->
            when (viewAction) {
                is ReportMapViewAction.OpenReportCreation -> this.navigateTo(ReportingActivity::class.java)
                is ReportMapViewAction.BicyclePathQuerySuccess -> displayBicyclePathOverlay(viewAction.data, viewAction.network)
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
                    addBicyclePath(map)
                }
            }
        })
    }

    private fun displayBicyclePathOverlay(data: JSONObject, network: BikePathNetwork) {
        map.let { map ->
            geoJsonLayer?.removeLayerFromMap()
            geoJsonLayer = GeoJsonLayer(map, data)
                    .apply {
                        defaultLineStringStyle.color = ContextCompat.getColor(this@ReportMapActivity, network.color)
                    }
            geoJsonLayer?.addLayerToMap()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_filter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
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

    private fun addReports() {
        viewModel.reports.observe(this, Observer { reports ->
            map?.clear()
            map?.addReportMarkers(this, reports)
        })
    }

    private fun addBicyclePath(map: GoogleMap) {
        if (map.cameraPosition.zoom >= BIKE_PATH_ON_MAP_ZOOM) {
            val visibleRegion = map.projection.visibleRegion
            val queryParameter = BoundingBoxQueryParameter(
                    topRight = visibleRegion.latLngBounds.northeast,
                    bottomLeft = visibleRegion.latLngBounds.southwest
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
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMap()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(prefsListener)
    }
}
