package app.vdh.org.vdhapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.models.Status

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReport(reportEntity: ReportEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReportList(reportEntityList: List<ReportEntity>) : List<Long>

    @Query("SELECT * FROM reportentity WHERE syncTimestamp BETWEEN :fromTimeStamp AND :toTimeStamp")
    fun getAllReports(fromTimeStamp: Long, toTimeStamp: Long): LiveData<List<ReportEntity>>

    @Query("SELECT * FROM reportentity WHERE status == :filterStatus AND syncTimestamp BETWEEN :fromTimeStamp AND :toTimeStamp")
    fun getReports(filterStatus: Status, fromTimeStamp: Long, toTimeStamp: Long): LiveData<List<ReportEntity>>

    @Delete
    fun deleteReport(reportEntity: ReportEntity)
}