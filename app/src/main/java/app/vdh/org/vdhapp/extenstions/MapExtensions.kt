package app.vdh.org.vdhapp.extenstions

import android.content.Context
import app.vdh.org.vdhapp.data.BitmapMarkerCache
import app.vdh.org.vdhapp.data.entities.ReportEntity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions

fun GoogleMap.addReportMarkers(context: Context, reports: List<ReportEntity>?) {
    reports?.forEach { report ->
        val markerOptions = MarkerOptions()
                .position(report.position)
                .title(report.name)

        report.status?.let {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapMarkerCache.getBitmapMarker(context, it.markerRes)))
        }

        this.addMarker(markerOptions)
                .tag = report
    }
}