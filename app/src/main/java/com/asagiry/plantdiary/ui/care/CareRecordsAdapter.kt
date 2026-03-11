package com.asagiry.plantdiary.ui.care

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.model.CareRecordWithPlant
import com.asagiry.plantdiary.databinding.ItemCareRecordBinding
import com.asagiry.plantdiary.ui.common.DateFormats
import com.asagiry.plantdiary.ui.common.labelRes
import java.time.LocalDate

class CareRecordsAdapter(
    private val onEdit: (CareRecordWithPlant) -> Unit,
    private val onDelete: (CareRecordWithPlant) -> Unit,
) : ListAdapter<CareRecordWithPlant, CareRecordsAdapter.CareRecordViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareRecordViewHolder {
        val binding = ItemCareRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CareRecordViewHolder(binding, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: CareRecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CareRecordViewHolder(
        private val binding: ItemCareRecordBinding,
        private val onEdit: (CareRecordWithPlant) -> Unit,
        private val onDelete: (CareRecordWithPlant) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CareRecordWithPlant) {
            val context = binding.root.context
            binding.item = item
            binding.careType.text = context.getString(item.plant.type.labelRes())
            binding.nextWatering.text =
                if (item.careRecord.nextWateringDate == LocalDate.now()) {
                    context.getString(R.string.needs_watering_today)
                } else {
                    context.getString(
                        R.string.due_date_value,
                        DateFormats.formatDate(item.careRecord.nextWateringDate),
                    )
                }
            binding.plantingDue.text =
                if (item.careRecord.plannedPlantingDate == null || item.careRecord.plannedPlantingTime == null) {
                    context.getString(R.string.no_planting_needed)
                } else if (item.careRecord.plannedPlantingDate == LocalDate.now()) {
                    context.getString(R.string.needs_planting_today)
                } else {
                    context.getString(
                        R.string.planting_value,
                        DateFormats.formatDate(item.careRecord.plannedPlantingDate),
                        DateFormats.formatTime(item.careRecord.plannedPlantingTime),
                    )
                }
            binding.editButton.setOnClickListener { onEdit(item) }
            binding.deleteButton.setOnClickListener { onDelete(item) }
            binding.executePendingBindings()
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CareRecordWithPlant>() {
        override fun areItemsTheSame(
            oldItem: CareRecordWithPlant,
            newItem: CareRecordWithPlant,
        ): Boolean = oldItem.careRecord.id == newItem.careRecord.id

        override fun areContentsTheSame(
            oldItem: CareRecordWithPlant,
            newItem: CareRecordWithPlant,
        ): Boolean = oldItem == newItem
    }
}

