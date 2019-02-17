package app.vdh.org.vdhapp.extenstions

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
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
            val hsv = FloatArray(3)
            Color.colorToHSV(ContextCompat.getColor(context, it.colorRes), hsv)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hsv[0]))
        }

        this.addMarker(markerOptions)
                .tag = report
    }
}