package app.vdh.org.vdhapp.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class ReportEntity(
        @PrimaryKey(autoGenerate = true)
        var id: Long? = null,
        var name:String? = "",
        var position: LatLng = LatLng(0.0,0.0),
        var photoPath: String? = "",
        var comment: String? = "",
        var deviceId: String? = "",
        var timestamp: Long = System.currentTimeMillis()) : Parcelable