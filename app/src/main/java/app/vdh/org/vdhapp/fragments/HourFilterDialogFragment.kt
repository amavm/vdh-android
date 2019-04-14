package app.vdh.org.vdhapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.consts.PrefConst.HOURS_SORT_PREFS_KEY
import app.vdh.org.vdhapp.data.events.ReportFilterEvent
import app.vdh.org.vdhapp.databinding.FragmentHoursFilterBinding
import app.vdh.org.vdhapp.viewmodels.HoursFilterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_hours_filter.*
import org.jetbrains.anko.support.v4.defaultSharedPreferences
import org.koin.android.viewmodel.ext.android.viewModel

class HourFilterDialogFragment : BottomSheetDialogFragment() {

    companion object {

        fun newInstance(hoursAgo: Int): HourFilterDialogFragment {
            val fragment = HourFilterDialogFragment()
            val args = Bundle()
            args.putInt(HOURS_SORT_PREFS_KEY, hoursAgo)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: HoursFilterViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentHoursFilterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_hours_filter, container, false)

        arguments?.getInt(HOURS_SORT_PREFS_KEY)?.let {
            viewModel.currentHoursAgoFilter = it
        }

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.reportFilterEvent.observe(this, Observer { statusFilterEvent ->
            when (statusFilterEvent) {
                is ReportFilterEvent.PickHoursFilter -> {
                    defaultSharedPreferences.edit()
                            .putInt(HOURS_SORT_PREFS_KEY, statusFilterEvent.hoursAgo)
                            .apply()
                    dismiss()
                }
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        report_filter_number_picker.minValue = 1
        report_filter_number_picker.maxValue = 24
        report_filter_number_picker.value = viewModel.currentHoursAgoFilter
    }
}