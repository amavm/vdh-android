package app.vdh.org.vdhapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import app.vdh.org.vdhapp.data.models.Status
import com.google.android.gms.maps.model.LatLng
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
    val serverId: String? = null
) : Parcelable {

    companion object {
        private val EMPTY_LAT_LNG = LatLng(0.0, 0.0)
    }

    fun isPositionDefined() = position != EMPTY_LAT_LNG
}