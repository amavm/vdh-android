package app.vdh.org.vdhapp.data

import android.arch.lifecycle.LiveData
import app.vdh.org.vdhapp.data.entities.ReportEntity
import org.jetbrains.anko.doAsync

class DeclarationRepositoryImpl(private val reportDao: ReportDao) : DeclarationRepository {

    override fun insertReport(reportEntity: ReportEntity, whenInserted: (Long) -> Unit) {
        doAsync {
            whenInserted(reportDao.insertDeclaration(reportEntity))
        }
    }

    override fun getReports(): LiveData<List<ReportEntity>> {
        return reportDao.getAllDeclarations()
    }
}
