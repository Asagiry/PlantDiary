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
import androidx.navigation.navOptions
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.databinding.FragmentCareFormBinding
import com.asagiry.plantdiary.ui.common.labelRes
import com.asagiry.plantdiary.ui.common.playEntranceMotion
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
    private var isSaving = false

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
        binding.careGuidanceAction.setOnClickListener {
            findNavController().navigate(
                R.id.plantFormFragment,
                null,
                navOptions {
                    anim {
                        enter = R.anim.nav_enter
                        exit = R.anim.nav_exit
                        popEnter = R.anim.nav_pop_enter
                        popExit = R.anim.nav_pop_exit
                    }
                },
            )
        }

        viewModel.plantChoices.observe(viewLifecycleOwner) { plants ->
            currentPlants = plants
            val labels = plants.map { plant ->
                getString(R.string.record_for, plant.name, getString(plant.type.labelRes()))
            }
            val selector: AutoCompleteTextView = binding.plantSelector
            selector.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels))
            updatePlantSelectorText()
            renderFormState()
        }

        viewModel.selectedPlantId.observe(viewLifecycleOwner) {
            updatePlantSelectorText()
            renderFormState()
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
            if (isSaving) {
                return@setOnClickListener
            }
            isSaving = true
            binding.saveCareRecordButton.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                var shouldRestoreButton = true
                try {
                    val errorRes = viewModel.saveCareRecord()
                    if (errorRes == null) {
                        shouldRestoreButton = false
                        findNavController().navigateUp()
                    } else {
                        Snackbar.make(binding.root, getString(errorRes), Snackbar.LENGTH_SHORT).show()
                    }
                } finally {
                    if (shouldRestoreButton) {
                        isSaving = false
                        renderFormState()
                    }
                }
            }
        }
        binding.careFormContent.playEntranceMotion()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updatePlantSelectorText() {
        val selected = currentPlants.firstOrNull { it.id == viewModel.selectedPlantId.value }
        val value =
            selected?.let { plant ->
                getString(R.string.record_for, plant.name, getString(plant.type.labelRes()))
            }.orEmpty()
        binding.plantSelector.setText(value, false)
    }

    private fun renderFormState() {
        val selectedPlant = currentPlants.firstOrNull { it.id == viewModel.selectedPlantId.value }
        val hasPlants = currentPlants.isNotEmpty()
        val hasSelectedPlant = selectedPlant != null
        val isGardenPlant = selectedPlant?.type == com.asagiry.plantdiary.data.local.entity.PlantType.GARDEN

        binding.plantSelectorLayout.isEnabled = hasPlants
        binding.plantSelector.isEnabled = hasPlants
        binding.pickNextWateringButton.isEnabled = hasSelectedPlant
        binding.pickCarePlantingDateButton.isEnabled = isGardenPlant
        binding.pickCarePlantingTimeButton.isEnabled = isGardenPlant
        binding.saveCareRecordButton.isEnabled = hasSelectedPlant && !isSaving

        when {
            !hasPlants -> {
                binding.careGuidanceCard.visibility = View.VISIBLE
                binding.careGuidanceTitle.text = getString(R.string.empty_care_no_plants_title)
                binding.careGuidanceMessage.text = getString(R.string.empty_care_no_plants_message)
                binding.careGuidanceAction.visibility = View.VISIBLE
            }

            !hasSelectedPlant -> {
                binding.careGuidanceCard.visibility = View.VISIBLE
                binding.careGuidanceTitle.text = getString(R.string.select_plant_first_title)
                binding.careGuidanceMessage.text = getString(R.string.select_plant_first_message)
                binding.careGuidanceAction.visibility = View.GONE
            }

            else -> {
                binding.careGuidanceCard.visibility = View.GONE
            }
        }
    }
}
