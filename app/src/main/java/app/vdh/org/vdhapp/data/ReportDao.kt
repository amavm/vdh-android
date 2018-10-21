package app.vdh.org.vdhapp.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import app.vdh.org.vdhapp.data.entities.ReportEntity

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDeclaration(reportEntity: ReportEntity) : Long

    @Query("SELECT * FROM reportentity")
    fun getAllDeclarations(): LiveData<List<ReportEntity>>
}