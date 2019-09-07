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
    val name: String? = "",
    val position: LatLng = LatLng(0.0, 0.0),
    val photoPath: String? = "",
    var comment: String? = "",
    val status: Status? = null,
    val serverId: String? = null
) : Parcelable