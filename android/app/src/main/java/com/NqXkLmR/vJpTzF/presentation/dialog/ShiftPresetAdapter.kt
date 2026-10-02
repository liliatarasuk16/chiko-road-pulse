package com.NqXkLmR.vJpTzF.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.ui.ShiftLabels
import com.NqXkLmR.vJpTzF.databinding.ItemShiftPresetBinding
import com.NqXkLmR.vJpTzF.domain.model.ShiftPreset
import com.NqXkLmR.vJpTzF.domain.usecase.FormatPointsUseCase

class ShiftPresetAdapter(
    private val presets: List<ShiftPreset>,
    private var selectedId: String,
    private val onSelect: (String) -> Unit
) : RecyclerView.Adapter<ShiftPresetAdapter.PresetHolder>() {

    private val formatPoints = FormatPointsUseCase()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresetHolder {
        val binding = ItemShiftPresetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PresetHolder(binding)
    }

    override fun onBindViewHolder(holder: PresetHolder, position: Int) {
        holder.bind(presets[position], presets[position].id == selectedId)
    }

    override fun getItemCount(): Int = presets.size

    fun select(shiftId: String) {
        selectedId = shiftId
        notifyDataSetChanged()
    }

    inner class PresetHolder(
        private val binding: ItemShiftPresetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(preset: ShiftPreset, selected: Boolean) {
            val context = binding.root.context
            binding.shiftItemMarker.text = preset.marker
            binding.shiftItemTitle.setText(ShiftLabels.titleRes(preset.titleKey))
            binding.shiftItemSpeed.text =
                context.getString(R.string.shift_speed_value, preset.speedDpPerSecond)
            binding.shiftItemChain.text =
                context.getString(R.string.shift_chain_value, preset.chainTarget)
            binding.shiftItemReward.text =
                context.getString(R.string.shift_reward_value, formatPoints(preset.reward))
            binding.root.setBackgroundResource(
                if (selected) R.drawable.item_block_selected else R.drawable.item_block_default
            )
            ViewCompat.setStateDescription(
                binding.root,
                context.getString(
                    if (selected) R.string.shift_state_selected else R.string.shift_state_available
                )
            )
            binding.root.alpha = if (selected) 1f else 0.82f
            binding.root.setOnClickListener { onSelect(preset.id) }
        }
    }
}
