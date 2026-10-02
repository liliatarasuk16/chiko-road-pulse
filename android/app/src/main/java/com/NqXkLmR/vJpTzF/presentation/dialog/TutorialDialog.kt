package com.NqXkLmR.vJpTzF.presentation.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.di.ServiceLocator
import com.NqXkLmR.vJpTzF.core.ui.PressFeedback
import com.NqXkLmR.vJpTzF.databinding.DialogTutorialBinding
import com.NqXkLmR.vJpTzF.domain.model.ParcelColor

class TutorialDialog : DialogFragment() {

    private var binding: DialogTutorialBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_ChikoRoadPulse_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = DialogTutorialBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val views = binding ?: return
        views.tutorialDialogList.layoutManager = LinearLayoutManager(requireContext())
        views.tutorialDialogList.adapter =
            TutorialStepAdapter(ServiceLocator.tutorialRepository.steps())
        views.tutorialDialogStrip.render(DEMO_SEQUENCE, 0)
        PressFeedback.attach(views.tutorialDialogConfirm)
        views.tutorialDialogConfirm.setOnClickListener { confirm() }
    }

    private fun confirm() {
        try {
            setFragmentResult(RESULT_KEY, Bundle())
        } catch (e: Exception) {
        }
        dismissAllowingStateLoss()
    }

    override fun onDestroyView() {
        val views = binding
        if (views != null) {
            PressFeedback.detach(views.tutorialDialogConfirm)
            views.tutorialDialogStrip.cancelAnimations()
            views.tutorialDialogList.adapter = null
        }
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "tutorial"
        const val RESULT_KEY = "tutorial_result"

        private val DEMO_SEQUENCE = listOf(
            ParcelColor.AMBER,
            ParcelColor.CYAN,
            ParcelColor.LIME,
            ParcelColor.CORAL,
            ParcelColor.AMBER,
            ParcelColor.LIME,
            ParcelColor.CYAN
        )
    }
}
