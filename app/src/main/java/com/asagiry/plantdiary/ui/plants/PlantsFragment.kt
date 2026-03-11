package com.asagiry.plantdiary.ui.plants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.data.local.entity.PlantType
import com.asagiry.plantdiary.databinding.FragmentPlantsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class PlantsFragment : Fragment() {
    private var _binding: FragmentPlantsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlantsViewModel by viewModels { PlantsViewModel.Factory }
    private val adapter by lazy {
        PlantsAdapter(
            onEdit = { plant ->
                findNavController().navigate(
                    R.id.action_plantsFragment_to_plantFormFragment,
                    bundleOf(PlantFormViewModel.ARG_PLANT_ID to plant.id),
                )
            },
            onDelete = ::confirmDelete,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.plantsList.layoutManager = LinearLayoutManager(requireContext())
        binding.plantsList.adapter = adapter

        binding.searchInput.doAfterTextChanged { text ->
            viewModel.onQueryChanged(text?.toString().orEmpty())
        }

        binding.filterGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chip_indoor -> PlantType.INDOOR
                R.id.chip_garden -> PlantType.GARDEN
                else -> null
            }
            viewModel.onFilterChanged(filter)
        }

        binding.addPlantFab.setOnClickListener {
            findNavController().navigate(R.id.action_plantsFragment_to_plantFormFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.plants.collect { items ->
                    adapter.submitList(items)
                    binding.emptyPlants.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun confirmDelete(plant: Plant) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.confirm_delete_plant)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deletePlant(plant) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}

