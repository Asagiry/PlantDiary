package com.asagiry.plantdiary.ui.care

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.model.CareRecordWithPlant
import com.asagiry.plantdiary.databinding.FragmentCareRecordsBinding
import com.asagiry.plantdiary.ui.common.playEntranceMotion
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class CareRecordsFragment : Fragment() {
    private var _binding: FragmentCareRecordsBinding? = null
    private val binding get() = _binding!!
    private var hasPlants = false
    private var currentRecords: List<CareRecordWithPlant> = emptyList()
    private var emptyStateAction: (() -> Unit)? = null

    private val viewModel: CareRecordsViewModel by viewModels { CareRecordsViewModel.Factory }
    private val adapter by lazy {
        CareRecordsAdapter(
            onEdit = { item ->
                findNavController().navigate(
                    R.id.action_careRecordsFragment_to_careFormFragment,
                    bundleOf(CareFormViewModel.ARG_CARE_RECORD_ID to item.careRecord.id),
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
        _binding = FragmentCareRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.careRecordsList.layoutManager = LinearLayoutManager(requireContext())
        binding.careRecordsList.adapter = adapter
        binding.emptyStateAction.setOnClickListener { emptyStateAction?.invoke() }

        binding.addCareFab.setOnClickListener {
            openCareAction()
        }
        binding.careRecordsContent.playEntranceMotion(listOf(binding.addCareFab))

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.hasPlants.collect { available ->
                        hasPlants = available
                        renderState()
                    }
                }
                launch {
                    viewModel.careRecords.collect { items ->
                        currentRecords = items
                        adapter.submitList(items) {
                            binding.careRecordsList.scheduleLayoutAnimation()
                        }
                        renderState()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun confirmDelete(item: CareRecordWithPlant) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.confirm_delete_care_record)
            .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteRecord(item) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun openCareAction() {
        if (hasPlants) {
            findNavController().navigate(R.id.action_careRecordsFragment_to_careFormFragment)
        } else {
            openPlantForm()
        }
    }

    private fun openPlantForm() {
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

    private fun renderState() {
        when {
            !hasPlants -> {
                binding.careRecordsList.visibility = View.GONE
                binding.emptyStateCard.visibility = View.VISIBLE
                binding.addCareFab.visibility = View.GONE
                binding.emptyStateTitle.text = getString(R.string.empty_care_no_plants_title)
                binding.emptyStateMessage.text = getString(R.string.empty_care_no_plants_message)
                binding.emptyStateAction.text = getString(R.string.add_first_plant)
                emptyStateAction = ::openPlantForm
            }

            currentRecords.isEmpty() -> {
                binding.careRecordsList.visibility = View.GONE
                binding.emptyStateCard.visibility = View.VISIBLE
                binding.addCareFab.visibility = View.VISIBLE
                binding.emptyStateTitle.text = getString(R.string.empty_care_title)
                binding.emptyStateMessage.text = getString(R.string.empty_care_message)
                binding.emptyStateAction.text = getString(R.string.create_first_care_record)
                emptyStateAction = { findNavController().navigate(R.id.action_careRecordsFragment_to_careFormFragment) }
            }

            else -> {
                binding.careRecordsList.visibility = View.VISIBLE
                binding.emptyStateCard.visibility = View.GONE
                binding.addCareFab.visibility = View.VISIBLE
                emptyStateAction = null
            }
        }
    }
}
