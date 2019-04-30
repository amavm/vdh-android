package app.vdh.org.vdhapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.adapters.ReportAdapter
import app.vdh.org.vdhapp.data.entities.ReportEntity
import app.vdh.org.vdhapp.helpers.ReportItemTouchHelperCallback
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        admin_recyclerView.setHasFixedSize(true)
        admin_recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val reports = List(20) {
            ReportEntity(
                    deviceId = "deviceId_$it",
                    name = "report $it")
        }

        admin_recyclerView.adapter = ReportAdapter(applicationContext, reports)
        val spanHelper = PagerSnapHelper()
        spanHelper.attachToRecyclerView(admin_recyclerView)
        val reportItemTouchHelper = ItemTouchHelper(ReportItemTouchHelperCallback())
        reportItemTouchHelper.attachToRecyclerView(admin_recyclerView)
    }
}
