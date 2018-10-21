package app.vdh.org.vdhapp.extenstions

import app.vdh.org.vdhapp.data.entities.ReportEntity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions

fun GoogleMap.addReportMarkers(reports: List<ReportEntity>?) {
    reports?.forEach { report ->
        this.addMarker(MarkerOptions()
                .position(report.position)
                .title(report.name))
                .tag = report
    }
}