package app.vdh.org.vdhapp.feature.report.presentation.map.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.core.consts.PrefConst.STATUS_SORT_PREFS_KEY
import app.vdh.org.vdhapp.feature.report.domain.common.model.Status
import app.vdh.org.vdhapp.databinding.FragmentStatusFilterBinding
import app.vdh.org.vdhapp.feature.report.presentation.map.action.ReportMapFilterViewAction
import app.vdh.org.vdhapp.feature.report.presentation.map.viewmodel.StatusFilterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel

class StatusFilterDialogFragment : BottomSheetDialogFragment() {

    companion object {

        fun newInstance(status: Status?): StatusFilterDialogFragment {
            val fragment = StatusFilterDialogFragment()
            if (status != null) {
                val args = Bundle()
                args.putSerializable(STATUS_SORT_PREFS_KEY, status.name)
                fragment.arguments = args
            }
            return fragment
        }
    }

    private val viewModel: StatusFilterViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentStatusFilterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_status_filter, container, false)

        arguments?.getString(STATUS_SORT_PREFS_KEY)?.let {
            val status = Status.valueOf(it)
            viewModel.currentStatus.value = status
        }

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.reportMapFilterViewAction.observe(viewLifecycleOwner, Observer { statusFilterEvent ->
            when (statusFilterEvent) {
                is ReportMapFilterViewAction.OpenStatusFilter -> {
                    context?.let { context ->
                        Status.writeInPreferences(context, statusFilterEvent.status)
                        dismiss()
                    }
                }
            }
        })

        return binding.root
    }
}