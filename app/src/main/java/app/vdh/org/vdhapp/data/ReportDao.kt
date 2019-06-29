package app.vdh.org.vdhapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.models.Status

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(reportEntity: ReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReportList(reportEntityList: List<ReportEntity>): List<Long>

    @Query("SELECT * FROM reportentity WHERE timestamp >= :fromTimeStamp")
    fun getAllReports(fromTimeStamp: Long): LiveData<List<ReportEntity>>

    @Query("SELECT * FROM reportentity WHERE status == :filterStatus AND timestamp >= :fromTimeStamp")
    fun getReports(filterStatus: Status, fromTimeStamp: Long): LiveData<List<ReportEntity>>

    @Delete
    suspend fun deleteReport(reportEntity: ReportEntity)
}