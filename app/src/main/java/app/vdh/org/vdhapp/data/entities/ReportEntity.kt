package app.vdh.org.vdhapp.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import app.vdh.org.vdhapp.data.models.Status
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class ReportEntity(
        var name:String? = "",
        var position: LatLng = LatLng(0.0,0.0),
        var photoPath: String? = "",
        var comment: String? = "",
        var deviceId: String? = "",
        var status: Status? = null,
        var syncTimestamp: Long? = null,
        var serverId: String? = null) : Parcelable {

        @PrimaryKey
        var id: String = deviceId + syncTimestamp

}