package app.vdh.org.vdhapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import app.vdh.org.vdhapp.api.Result
import app.vdh.org.vdhapp.consts.PrefConst
import app.vdh.org.vdhapp.data.dtos.ObservationDto
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.models.BikePathNetwork
import app.vdh.org.vdhapp.data.models.BoundingBoxQueryParameter
import app.vdh.org.vdhapp.data.models.Status
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

interface ReportRepository {

    suspend fun saveReport(context: Context, reportEntity: ReportEntity, sendToServer: Boolean = false): Pair<Result<Long>, Result<ObservationDto>?>

    fun getReports(hoursAgo: Int = PrefConst.HOURS_SORT_DEFAULT_VALUE, status: Status?, coroutineContext: CoroutineContext): LiveData<List<ReportEntity>>

    suspend fun deleteReport(reportEntity: ReportEntity): Result<String>

    suspend fun getBicyclePathGeoJson(boundingBoxQueryParameter: BoundingBoxQueryParameter, network: BikePathNetwork): Result<JSONObject>
}