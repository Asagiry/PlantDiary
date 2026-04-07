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
import com.asagiry.plantdiary.ui.common.playEntranceMotion
import com.asagiry.plantdiary.ui.common.shouldReduceMotion
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.combine
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
        binding.emptyPlantsAction.setOnClickListener {
            if (binding.searchFilterPanel.visibility == View.VISIBLE && binding.plantsList.visibility == View.GONE) {
                viewModel.resetFilters()
            } else {
                findNavController().navigate(R.id.action_plantsFragment_to_plantFormFragment)
            }
        }

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
        binding.plantsContent.playEntranceMotion(listOf(binding.addPlantFab))

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentQuery.collect { query ->
                        if (binding.searchInput.text?.toString() != query) {
                            binding.searchInput.setText(query)
                            binding.searchInput.setSelection(binding.searchInput.text?.length ?: 0)
                        }
                    }
                }
                launch {
                    viewModel.currentFilter.collect { filter ->
                        val selectedChipId = when (filter) {
                            PlantType.INDOOR -> R.id.chip_indoor
                            PlantType.GARDEN -> R.id.chip_garden
                            null -> R.id.chip_all
                        }
                        if (binding.filterGroup.checkedChipId != selectedChipId) {
                            binding.filterGroup.check(selectedChipId)
                        }
                    }
                }
                launch {
                    combine(viewModel.plants, viewModel.hasAnyPlants) { items, hasAnyPlants ->
                        items to hasAnyPlants
                    }.collect { (items, hasAnyPlants) ->
                        adapter.submitList(items) {
                            if (!requireContext().shouldReduceMotion()) {
                                binding.plantsList.scheduleLayoutAnimation()
                            }
                        }
                        renderPlantsState(items, hasAnyPlants)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun confirmDelete(plant: Plant) {
        viewLifecycleOwner.lifecycleScope.launch {
            val relatedCareRecords = viewModel.getRelatedCareRecordCount(plant.id)
            val message =
                if (relatedCareRecords == 0) {
                    getString(R.string.confirm_delete_plant)
                } else {
                    resources.getQuantityString(
                        R.plurals.confirm_delete_plant_with_records,
                        relatedCareRecords,
                        relatedCareRecords,
                    )
                }

            MaterialAlertDialogBuilder(requireContext())
                .setMessage(message)
                .setPositiveButton(R.string.delete) { _, _ -> viewModel.deletePlant(plant) }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun renderPlantsState(items: List<Plant>, hasAnyPlants: Boolean) {
        val hasVisibleItems = items.isNotEmpty()
        val showEmptySearch = hasAnyPlants && !hasVisibleItems
        val showEmptyDiary = !hasAnyPlants

        binding.plantsList.visibility = if (hasVisibleItems) View.VISIBLE else View.GONE
        binding.searchFilterPanel.visibility = if (showEmptyDiary) View.GONE else View.VISIBLE
        binding.emptyPlantsCard.visibility = if (showEmptySearch || showEmptyDiary) View.VISIBLE else View.GONE
        binding.addPlantFab.visibility = if (showEmptyDiary) View.GONE else View.VISIBLE

        if (showEmptySearch) {
            binding.emptyPlantsTitle.setText(R.string.empty_search_title)
            binding.emptyPlantsMessageView.setText(R.string.empty_search_message)
            binding.emptyPlantsAction.setText(R.string.reset_filters)
            return
        }

        binding.emptyPlantsTitle.setText(R.string.empty_plants_title)
        binding.emptyPlantsMessageView.setText(R.string.empty_plants_message)
        binding.emptyPlantsAction.setText(R.string.add_first_plant)
    }
}
