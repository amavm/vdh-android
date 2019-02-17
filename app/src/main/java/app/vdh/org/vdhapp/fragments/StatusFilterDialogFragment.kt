package app.vdh.org.vdhapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.data.events.StatusFilterEvent
import app.vdh.org.vdhapp.data.models.Status
import app.vdh.org.vdhapp.databinding.FragmentStatusFilterBinding
import app.vdh.org.vdhapp.viewmodels.StatusFilterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.viewModel

class StatusFilterDialogFragment : BottomSheetDialogFragment() {

    companion object {

        fun newInstance(status: Status?) : StatusFilterDialogFragment {
            val fragment = StatusFilterDialogFragment()
            if (status != null) {
                val args = Bundle()
                args.putSerializable(Status.STATUS_SORT_PREFS_KEY, status.name)
                fragment.arguments = args
            }
            return fragment
        }
    }

    private val viewModel : StatusFilterViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : FragmentStatusFilterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_status_filter, container,false)

        arguments?.getString(Status.STATUS_SORT_PREFS_KEY)?.let {
            val status = Status.valueOf(it)
            viewModel.currentStatus.value = status
        }

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.statusFilterEvent.observe(this, Observer { statusFilterEvent ->
            when(statusFilterEvent) {
                is StatusFilterEvent.PickStatusFilter -> {
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