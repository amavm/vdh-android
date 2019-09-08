package app.vdh.org.vdhapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.adapters.ReportAdapter
import app.vdh.org.vdhapp.data.actions.AdminAction
import app.vdh.org.vdhapp.data.actions.AdminViewAction
import app.vdh.org.vdhapp.helpers.ReportItemTouchHelperCallback
import app.vdh.org.vdhapp.viewmodels.AdminViewModel
import kotlinx.android.synthetic.main.activity_admin.*
import org.koin.android.viewmodel.ext.android.viewModel

class AdminActivity : AppCompatActivity() {

    private val viewModel: AdminViewModel by viewModel()
    private val reportAdapter = ReportAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        setupRecycler()
        observerViewModel()
    }

    private fun setupRecycler() {
        admin_recyclerView.setHasFixedSize(true)
        admin_recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        admin_recyclerView.adapter = reportAdapter
        val spanHelper = PagerSnapHelper()
        spanHelper.attachToRecyclerView(admin_recyclerView)
        val reportItemTouchHelper = ItemTouchHelper(ReportItemTouchHelperCallback(reportAdapter))
        reportItemTouchHelper.attachToRecyclerView(admin_recyclerView)
    }

    private fun observerViewModel() {

        viewModel.reports.observe(this, Observer { reports ->
            reportAdapter.updateReports(reports)
        })

        viewModel.adminViewAction.observe(this, Observer { viewAction ->
            when (viewAction) {
            }
        })
    }
}
