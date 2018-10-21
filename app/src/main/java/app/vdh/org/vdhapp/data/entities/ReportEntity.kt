package app.vdh.org.vdhapp.data.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
class ReportEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    var name:String? = ""

    var position: LatLng? = null

    var photoPath: String? = ""

    var comment: String? = ""

    // time stamp
}