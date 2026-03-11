package com.asagiry.plantdiary.ui.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asagiry.plantdiary.R
import com.asagiry.plantdiary.data.local.model.CareRecordWithPlant
import com.asagiry.plantdiary.databinding.ItemScheduleRecordBinding
import com.asagiry.plantdiary.ui.common.DateFormats
import com.asagiry.plantdiary.ui.common.labelRes
import java.time.LocalDate

class ScheduleAdapter(
    private val mode: Mode,
) : ListAdapter<CareRecordWithPlant, ScheduleAdapter.ScheduleViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding, mode)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ScheduleViewHolder(
        private val binding: ItemScheduleRecordBinding,
        private val mode: Mode,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CareRecordWithPlant) {
            val context = binding.root.context
            binding.item = item
            binding.typePill.text = context.getString(item.plant.type.labelRes())
            binding.subtitle.text =
                when (mode) {
                    Mode.WATERING -> {
                        if (item.careRecord.nextWateringDate == LocalDate.now()) {
                            context.getString(R.string.needs_watering_today)
                        } else {
                            context.getString(
                                R.string.due_date_value,
                                DateFormats.formatDate(item.careRecord.nextWateringDate),
                            )
                        }
                    }
                    Mode.PLANTING -> {
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
                    }
                }
            binding.executePendingBindings()
        }
    }

    enum class Mode {
        WATERING,
        PLANTING,
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
