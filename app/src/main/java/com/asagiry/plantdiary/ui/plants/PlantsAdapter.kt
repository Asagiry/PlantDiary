package com.asagiry.plantdiary.ui.plants

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.entity.Plant
import com.asagiry.plantdiary.databinding.ItemPlantBinding
import com.asagiry.plantdiary.ui.common.DateFormats
import com.asagiry.plantdiary.ui.common.labelRes

class PlantsAdapter(
    private val onEdit: (Plant) -> Unit,
    private val onDelete: (Plant) -> Unit,
) : ListAdapter<Plant, PlantsAdapter.PlantViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = ItemPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding, onEdit, onDelete)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlantViewHolder(
        private val binding: ItemPlantBinding,
        private val onEdit: (Plant) -> Unit,
        private val onDelete: (Plant) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Plant) {
            val context = binding.root.context
            binding.plant = item
            binding.plantType.text = context.getString(item.type.labelRes())
            binding.wateringInterval.text =
                context.getString(R.string.watering_interval_value, item.wateringIntervalDays)
            binding.plantingDate.text =
                if (item.type == com.asagiry.plantdiary.data.local.entity.PlantType.GARDEN &&
                    item.plantingDate != null &&
                    item.plantingTime != null
                ) {
                    context.getString(
                        R.string.planting_value,
                        DateFormats.formatDate(item.plantingDate),
                        DateFormats.formatTime(item.plantingTime),
                    )
                } else {
                    context.getString(R.string.no_planting_needed)
                }
            binding.editButton.setOnClickListener { onEdit(item) }
            binding.deleteButton.setOnClickListener { onDelete(item) }
            binding.executePendingBindings()
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean = oldItem == newItem
    }
}

