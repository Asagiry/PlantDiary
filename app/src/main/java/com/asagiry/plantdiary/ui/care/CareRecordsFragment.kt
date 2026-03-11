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
import androidx.recyclerview.widget.LinearLayoutManager
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.model.CareRecordWithPlant
import com.asagiry.plantdiary.databinding.FragmentCareRecordsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class CareRecordsFragment : Fragment() {
    private var _binding: FragmentCareRecordsBinding? = null
    private val binding get() = _binding!!

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

        binding.addCareFab.setOnClickListener {
            findNavController().navigate(R.id.action_careRecordsFragment_to_careFormFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.careRecords.collect { items ->
                    adapter.submitList(items)
                    binding.emptyCareRecords.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
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
}

