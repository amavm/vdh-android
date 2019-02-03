package app.vdh.org.vdhapp.activities

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.states.ReportingMapActionState
import app.vdh.org.vdhapp.databinding.ActivityReportMapBinding
import app.vdh.org.vdhapp.extenstions.addReportMarkers
import app.vdh.org.vdhapp.extenstions.navigateTo
import app.vdh.org.vdhapp.viewmodels.ReportMapViewModel
import com.crashlytics.android.Crashlytics
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonLayer
import io.fabric.sdk.android.Fabric
import org.koin.android.viewmodel.ext.android.viewModel

class ReportMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_CODE = 123
        private const val DEFAULT_MAP_ZOOM = 16.0f
        private const val BIKE_PATH_ON_MAP_ZOOM = 13.0f
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var map: GoogleMap? = null
    private val viewModel : ReportMapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityReportMapBinding = DataBindingUtil.setContentView(
                this, R.layout.activity_report_map)

        with(binding) {
            setLifecycleOwner(this@ReportMapActivity)
            viewModel = this@ReportMapActivity.viewModel
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        } else {
            initMap()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initMap() {

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
                false
            }

            viewModel.mapReportingEvent.observe(this, Observer { action ->
                when(action) {
                    ReportingMapActionState.AddReport -> {
                        this.navigateTo(ReportingActivity::class.java)
                    }
                }
            })

            map.setOnCameraIdleListener {
                if (map.cameraPosition.zoom >= BIKE_PATH_ON_MAP_ZOOM) {
                    addBicyclePath(map)
                }
            }
        }
    }

    private fun addReports() {
        viewModel.getReports().observe(this, Observer { reports ->
            map?.clear()
            map?.addReportMarkers(reports)
        })
    }

    private fun addBicyclePath(map: GoogleMap) {
        val visibleRegion = map.projection.visibleRegion
        var layer: GeoJsonLayer? = null
        viewModel.getBicyclePath(
                boundingBoxQueryParameter = BoundingBoxQueryParameter(
                        topRight = visibleRegion.latLngBounds.northeast,
                        bottomLeft = visibleRegion.latLngBounds.southwest
                ),
                onSuccess = { geoJson ->
                    map.let { map ->
                        layer?.removeLayerFromMap()
                        layer = GeoJsonLayer(map, geoJson)
                        layer?.addLayerToMap()
                    }
                },
                onError = {

                })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap()
            }
        }
    }
}
