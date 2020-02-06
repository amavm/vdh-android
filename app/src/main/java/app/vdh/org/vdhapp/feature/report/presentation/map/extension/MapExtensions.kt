package app.vdh.org.vdhapp.feature.report.presentation.map.extension

import android.content.Context
import app.vdh.org.vdhapp.core.helpers.BitmapMarkerCache
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions

fun GoogleMap.addReportMarkers(context: Context, reports: List<ReportModel>?) {
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