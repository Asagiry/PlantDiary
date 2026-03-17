package com.asagiry.plantdiary.ui.plants

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
import com.asagiry.plantdiary.data.local.entity.PlantType
import com.asagiry.plantdiary.databinding.FragmentPlantFormBinding
import com.asagiry.plantdiary.ui.common.playEntranceMotion
import com.asagiry.plantdiary.ui.common.showDatePicker
import com.asagiry.plantdiary.ui.common.showTimePicker
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PlantFormFragment : Fragment() {
    private var _binding: FragmentPlantFormBinding? = null
    private val binding get() = _binding!!
    private var isSaving = false

    private val viewModel: PlantFormViewModel by viewModels { PlantFormViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlantFormBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editing = arguments?.getLong(PlantFormViewModel.ARG_PLANT_ID, 0L) != 0L
        requireActivity().findViewById<MaterialToolbar>(R.id.toolbar).title =
            getString(if (editing) R.string.edit_plant else R.string.add_plant)

        setupTypeSelector()

        binding.pickPlantingDateButton.setOnClickListener {
            showDatePicker(viewModel::setPlantingDate)
        }
        binding.pickPlantingTimeButton.setOnClickListener {
            showTimePicker(viewModel::setPlantingTime)
        }
        binding.savePlantButton.setOnClickListener {
            if (isSaving) {
                return@setOnClickListener
            }
            isSaving = true
            binding.savePlantButton.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                var shouldRestoreButton = true
                try {
                    val errorRes = viewModel.savePlant()
                    if (errorRes == null) {
                        shouldRestoreButton = false
                        findNavController().navigateUp()
                    } else {
                        Snackbar.make(binding.root, getString(errorRes), Snackbar.LENGTH_SHORT).show()
                    }
                } finally {
                    if (shouldRestoreButton) {
                        isSaving = false
                        binding.savePlantButton.isEnabled = true
                    }
                }
            }
        }
        binding.plantFormContent.playEntranceMotion()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTypeSelector() {
        val labels = listOf(getString(R.string.indoor), getString(R.string.garden))
        val types = listOf(PlantType.INDOOR, PlantType.GARDEN)
        val input: AutoCompleteTextView = binding.typeInput
        input.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels))
        input.setOnItemClickListener { _, _, position, _ ->
            viewModel.setType(types[position])
        }
        viewModel.type.observe(viewLifecycleOwner) { value ->
            val selectedType = PlantType.valueOf(value)
            input.setText(getString(if (selectedType == PlantType.INDOOR) R.string.indoor else R.string.garden), false)
        }
    }
}
