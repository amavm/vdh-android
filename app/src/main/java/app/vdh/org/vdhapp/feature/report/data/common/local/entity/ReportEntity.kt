package app.vdh.org.vdhapp.feature.report.data.common.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import app.vdh.org.vdhapp.feature.report.domain.common.model.ReportModel
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class ReportEntity(
    val deviceId: String,
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey
val id: String = deviceId + timestamp,
    val sentToSever: Boolean = false,
    val name: String? = null,
    val position: LatLng = EMPTY_LAT_LNG,
    val photoPath: String? = null,
    var comment: String? = null,
    val status: Status? = null,
    val serverId: String? = null,
    val moderationStatus: String? = null
) : Parcelable {

    companion object {
        private val EMPTY_LAT_LNG = LatLng(0.0, 0.0)
    }

    fun toReportModel(): ReportModel {
        return ReportModel(
                deviceId = deviceId,
                id = id,
                status = status,
                comment = comment,
                name = name,
                photoPath = photoPath,
                position = position,
                sentToSever = sentToSever,
                serverId = serverId,
                timestamp = timestamp,
                moderationStatus = moderationStatus
        )
    }
}