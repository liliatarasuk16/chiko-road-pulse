package com.NqXkLmR.vJpTzF.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.ui.TutorialLabels
import com.NqXkLmR.vJpTzF.databinding.ItemTutorialStepBinding
import com.NqXkLmR.vJpTzF.domain.model.TutorialStep

class TutorialStepAdapter(
    private val steps: List<TutorialStep>
) : RecyclerView.Adapter<TutorialStepAdapter.StepHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepHolder {
        val binding = ItemTutorialStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepHolder(binding)
    }

    override fun onBindViewHolder(holder: StepHolder, position: Int) {
        holder.bind(steps[position])
    }

    override fun getItemCount(): Int = steps.size

    class StepHolder(
        private val binding: ItemTutorialStepBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(step: TutorialStep) {
            val context = binding.root.context
            binding.tutorialItemNumber.text =
                context.getString(R.string.tutorial_step_number, step.number)
            binding.tutorialItemText.setText(TutorialLabels.textRes(step.textKey))
            binding.tutorialItemSprite.setImageResource(TutorialLabels.spriteRes(step.spriteKey))
        }
    }
}
