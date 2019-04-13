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

    @Query("SELECT * FROM reportentity WHERE syncTimestamp >= :fromTimeStamp")
    fun getAllReports(fromTimeStamp: Long): LiveData<List<ReportEntity>>

    @Query("SELECT * FROM reportentity WHERE status == :filterStatus AND syncTimestamp >= :fromTimeStamp")
    fun getReports(filterStatus: Status, fromTimeStamp: Long): LiveData<List<ReportEntity>>

    @Delete
    fun deleteReport(reportEntity: ReportEntity)
}