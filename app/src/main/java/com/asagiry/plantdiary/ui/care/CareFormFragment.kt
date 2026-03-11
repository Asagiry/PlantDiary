package com.asagiry.plantdiary.ui.care

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.databinding.FragmentCareFormBinding
import com.asagiry.plantdiary.ui.common.labelRes
import com.asagiry.plantdiary.ui.common.showDatePicker
import com.asagiry.plantdiary.ui.common.showTimePicker
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CareFormFragment : Fragment() {
    private var _binding: FragmentCareFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CareFormViewModel by viewModels { CareFormViewModel.Factory }
    private var currentPlants: List<Plant> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCareFormBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editing = arguments?.getLong(CareFormViewModel.ARG_CARE_RECORD_ID, 0L) != 0L
        requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).title =
            getString(if (editing) R.string.edit_care_record else R.string.add_care_record)

        viewModel.plantChoices.observe(viewLifecycleOwner) { plants ->
            currentPlants = plants
            val labels = plants.map { plant ->
                getString(R.string.record_for, plant.name, getString(plant.type.labelRes()))
            }
            val selector: AutoCompleteTextView = binding.plantSelector
            selector.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels))

            val selected = plants.firstOrNull { it.id == viewModel.selectedPlantId.value }
            if (selected != null) {
                selector.setText(getString(R.string.record_for, selected.name, getString(selected.type.labelRes())), false)
            }
        }

        binding.plantSelector.setOnItemClickListener { _, _, position, _ ->
            currentPlants.getOrNull(position)?.let { plant ->
                viewModel.onPlantSelected(plant.id)
            }
        }

        binding.pickNextWateringButton.setOnClickListener {
            showDatePicker(viewModel::setNextWateringDate)
        }
        binding.pickCarePlantingDateButton.setOnClickListener {
            showDatePicker(viewModel::setPlantingDate)
        }
        binding.pickCarePlantingTimeButton.setOnClickListener {
            showTimePicker(viewModel::setPlantingTime)
        }
        binding.saveCareRecordButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val errorRes = viewModel.saveCareRecord()
                if (errorRes == null) {
                    findNavController().navigateUp()
                } else {
                    Snackbar.make(binding.root, getString(errorRes), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
