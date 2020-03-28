package app.vdh.org.vdhapp.core.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.staticmap.v1.MapboxStaticMap
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import org.jetbrains.anko.windowManager

object MapBoxHelper {

    fun MapboxMap.addMarkerImages(context: Context) {
        addMarkerImage(context, Status.CAUTION)
        addMarkerImage(context, Status.CLEARED)
        addMarkerImage(context, Status.ICE)
        addMarkerImage(context, Status.SNOW)
    }

    private fun MapboxMap.addMarkerImage(context: Context, status: Status) {
        val markerDrawable = ContextCompat.getDrawable(context, status.markerRes)
        markerDrawable?.let {
            style?.addImage(status.name, it)
        }
    }

    fun addMarkers(mapboxMap: MapboxMap, mapView: MapView, reports: List<ReportModel>, onMarkerClick: (ReportModel) -> Unit) {
        mapboxMap.style?.let { style ->
            reports.forEach { report ->
                report.status?.let { status ->
                    val symbolManager = SymbolManager(mapView, mapboxMap, style)
                    symbolManager.iconAllowOverlap = true
                    symbolManager.iconIgnorePlacement = true
                    symbolManager.create(SymbolOptions()
                            .withLatLng(report.position)
                            .withIconImage(status.name)
                            .withIconSize(1.0f))
                    symbolManager.addClickListener {
                        onMarkerClick(report)
                    }
                }
            }
        }
    }

    fun MapboxMap.enableLocation(
        context: Context,
        permissionsListener: PermissionsListener,
        activity: Activity,
        locationEnabled: () -> Unit
    ): PermissionsManager? {
        return if (PermissionsManager.areLocationPermissionsGranted(context)) {
            style?.let {
                locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(context, it).build()
                )
                locationComponent.isLocationComponentEnabled = true
                locationComponent.cameraMode = CameraMode.TRACKING
                locationComponent?.addOnLocationStaleListener {
                }
                locationEnabled()
            }
            null
        } else {
            val permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(activity)
            permissionsManager
        }
    }

    fun MapboxMap.centerOnUser(mapZoom: Double) {
        val lastLocation = locationComponent.lastKnownLocation
        lastLocation?.let {
            val position = CameraPosition.Builder()
                    .zoom(mapZoom)
                    .target(LatLng(lastLocation.latitude, lastLocation.longitude)).build()
            animateCamera(CameraUpdateFactory.newCameraPosition(position), 500)
        }
    }

    fun getStaticImageUrl(context: Context, position: LatLng): String {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val positionPoint = Point.fromLngLat(position.longitude, position.latitude)
        val staticImage = MapboxStaticMap.builder()
                .accessToken(context.resources.getString(R.string.mapbox_key))
                .cameraPoint(positionPoint)
                .cameraZoom(16.0)
                .width(displayMetrics.widthPixels / 2)
                .height(320)
                .staticMarkerAnnotations(listOf(
                        StaticMarkerAnnotation.builder()
                        .lnglat(positionPoint)
                                .build()))
                .build()
        return staticImage.url().toString()
    }

    fun getPlacePickerIntent(activity: Activity, position: LatLng): Intent {
        return PlacePicker.IntentBuilder()
                .accessToken(activity.resources.getString(R.string.mapbox_key))
                .placeOptions(PlacePickerOptions.builder()
                        .statingCameraPosition(CameraPosition.Builder()
                                .target(position)
                                .zoom(16.0)
                                .build()
                        ).build()
                ).build(activity)
    }
}