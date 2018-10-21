package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.data.states.ReportingMapActionState

class ReportMapViewModel(app: Application, private val repository: ReportRepository) : AndroidViewModel(app) {

    val mapReportingEvent: SingleLiveEvent<ReportingMapActionState> = SingleLiveEvent()


    fun getReports() : LiveData<List<ReportEntity>> = repository.getReports()

    fun onReportButtonClicked() {
        mapReportingEvent.value = ReportingMapActionState.AddReport
    }

}