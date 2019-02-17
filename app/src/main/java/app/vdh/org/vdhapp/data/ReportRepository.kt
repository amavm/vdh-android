package app.vdh.org.vdhapp.data

import androidx.lifecycle.LiveData
import android.content.Context
import app.vdh.org.vdhapp.api.Result
import app.vdh.org.vdhapp.consts.PrefConst
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.models.Status
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

interface ReportRepository {

    suspend fun saveReport(context: Context, reportEntity: ReportEntity, sendToServer: Boolean = false) : Pair<Result<Long>, Result<ObservationDto>?>

    fun getReports(hoursAgo: Int = PrefConst.HOURS_SORT_DEFAULT_VALUE, status: Status?) : LiveData<List<ReportEntity>>

    suspend fun deleteReport(reportEntity: ReportEntity) : Result<String>

    suspend fun getBicyclePathGeoJson(boundingBoxQueryParameter: BoundingBoxQueryParameter) : Result<JSONObject>

    suspend fun getBicyclePathGeoJson(centerCoordinates: LatLng) : Result<JSONObject>

    suspend fun getBicyclePathGeoJson() : Result<JSONObject>
}