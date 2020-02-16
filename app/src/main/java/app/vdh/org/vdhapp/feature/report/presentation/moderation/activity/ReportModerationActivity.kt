package app.vdh.org.vdhapp.feature.report.presentation.moderation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.core.consts.ApiConst
import app.vdh.org.vdhapp.feature.report.presentation.moderation.action.ReportModerationAction
import app.vdh.org.vdhapp.feature.report.presentation.moderation.action.ReportModerationViewAction
import app.vdh.org.vdhapp.feature.report.presentation.moderation.recyclerview.ReportModerationAdapter
import app.vdh.org.vdhapp.feature.report.presentation.moderation.viewmodel.ReportModerationViewModel
import com.esafirm.imagepicker.view.GridSpacingItemDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_report_moderation.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReportModerationActivity : AppCompatActivity() {

    private val viewModel: ReportModerationViewModel by viewModel()
    private val adapter = ReportModerationAdapter { viewAction ->
        viewModel.handleViewAction(viewAction)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_moderation)
        configureRecyclerView()

        viewModel.reports.observe(this, Observer { reportUiModels ->
            if (reportUiModels != null) {
                adapter.updateReports(reportUiModels)
            }
        })

        viewModel.action.observe(this, Observer {
            it?.let {
                handleAction(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.moderation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_pending_reports ->
                viewModel.handleViewAction(ReportModerationViewAction.ChangeModerationStatusFilter(ApiConst.MODERATION_STATUS_PENDING))
            R.id.menu_valid_reports ->
                viewModel.handleViewAction(ReportModerationViewAction.ChangeModerationStatusFilter(ApiConst.MODERATION_STATUS_VALID))
            R.id.menu_refused_reports ->
                viewModel.handleViewAction(ReportModerationViewAction.ChangeModerationStatusFilter(ApiConst.MODERATION_STATUS_REJECTED))
        }

        return super.onOptionsItemSelected(item)
    }

    private fun handleAction(reportModerationAction: ReportModerationAction) {
        when (reportModerationAction) {
            is ReportModerationAction.ReportStatusUpdated -> {
            }

            is ReportModerationAction.ReportStatusUpdateError -> {
                Snackbar.make(reportRecyclerView, R.string.update_report_error, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun configureRecyclerView() {
        reportRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        reportRecyclerView.setHasFixedSize(true)
        reportRecyclerView.addItemDecoration(GridSpacingItemDecoration(1, resources.getDimensionPixelSize(R.dimen.medium_spacing), true))
        reportRecyclerView.adapter = adapter
    }
}
