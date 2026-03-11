package com.asagiry.plantdiary.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.databinding.FragmentScheduleBinding
import com.asagiry.plantdiary.ui.common.DateFormats
import com.asagiry.plantdiary.ui.common.showDatePicker
import kotlinx.coroutines.launch

class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by viewModels { ScheduleViewModel.Factory }
    private val wateringAdapter = ScheduleAdapter(ScheduleAdapter.Mode.WATERING)
    private val plantingAdapter = ScheduleAdapter(ScheduleAdapter.Mode.PLANTING)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.wateringList.layoutManager = LinearLayoutManager(requireContext())
        binding.wateringList.adapter = wateringAdapter
        binding.plantingList.layoutManager = LinearLayoutManager(requireContext())
        binding.plantingList.adapter = plantingAdapter

        binding.todayButton.setOnClickListener { viewModel.selectToday() }
        binding.tomorrowButton.setOnClickListener { viewModel.selectTomorrow() }
        binding.customDateButton.setOnClickListener {
            showDatePicker(viewModel::selectDate)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.date.collect { date ->
                        binding.selectedDayLabel.text =
                            getString(R.string.selected_day, DateFormats.formatDate(date))
                    }
                }
                launch {
                    viewModel.wateringRecords.collect { items ->
                        wateringAdapter.submitList(items)
                        binding.emptyWatering.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.plantingRecords.collect { items ->
                        plantingAdapter.submitList(items)
                        binding.emptyPlanting.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

