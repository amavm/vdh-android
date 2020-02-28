package app.vdh.org.vdhapp.feature.report.data.common.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.vdh.org.vdhapp.core.consts.ApiConst
import app.vdh.org.vdhapp.feature.report.data.common.local.entity.ReportEntity
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(reportEntity: ReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReportList(reportEntityList: List<ReportEntity>): List<Long>

    @Query("SELECT * FROM reportentity WHERE timestamp >= :fromTimeStamp AND (deviceId == :deviceId OR moderationStatus == :moderationStatus)")
    fun getAllValidOrDeviceOwnerReports(
        fromTimeStamp: Long,
        deviceId: String,
        moderationStatus: String = ApiConst.MODERATION_STATUS_VALID
    ): LiveData<List<ReportEntity>>

    @Query("SELECT * FROM reportentity WHERE (status == :filterStatus AND timestamp >= :fromTimeStamp) AND (deviceId == :deviceId OR moderationStatus == :moderationStatus)")
    fun getValidOrDeviceOwnerReports(
        filterStatus: Status,
        fromTimeStamp: Long,
        deviceId: String,
        moderationStatus: String = ApiConst.MODERATION_STATUS_VALID
    ): LiveData<List<ReportEntity>>

    @Query("SELECT * FROM reportentity WHERE timestamp >= :fromTimeStamp AND moderationStatus == :moderationStatus ORDER BY timestamp DESC")
    fun getReportsByModerationStatus(fromTimeStamp: Long, moderationStatus: String): LiveData<List<ReportEntity>>

    @Delete
    suspend fun deleteReport(reportEntity: ReportEntity)

    @Query("UPDATE ReportEntity SET moderationStatus = :reportStatus WHERE serverId = :reportId")
    suspend fun updateReport(reportId: String, reportStatus: String)
}