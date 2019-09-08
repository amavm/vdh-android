package app.vdh.org.vdhapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.vdh.org.vdhapp.data.ReportRepository
import app.vdh.org.vdhapp.data.SingleLiveEvent
import app.vdh.org.vdhapp.data.actions.AdminAction
import app.vdh.org.vdhapp.data.actions.AdminViewAction

class AdminViewModel(app: Application, reportRepository: ReportRepository): AndroidViewModel(app) {

    val adminViewAction: SingleLiveEvent<AdminViewAction> = SingleLiveEvent()

    val reports = reportRepository.getReports(status = null, coroutineContext = viewModelScope.coroutineContext)

    fun handleAction(action: AdminAction) {
        when (action) {
        }
    }
}