package com.NqXkLmR.vJpTzF.presentation.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.ui.PressFeedback
import com.NqXkLmR.vJpTzF.databinding.DialogPauseBinding

class PauseDialog : DialogFragment() {

    private var binding: DialogPauseBinding? = null
    private var resultSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_ChikoRoadPulse_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = DialogPauseBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val views = binding ?: return
        PressFeedback.attach(views.pauseDialogResume)
        PressFeedback.attach(views.pauseDialogMenu)
        views.pauseDialogResume.setOnClickListener { finishWith(ACTION_RESUME) }
        views.pauseDialogMenu.setOnClickListener { finishWith(ACTION_MENU) }
    }

    private fun finishWith(action: String) {
        sendResult(action)
        dismissAllowingStateLoss()
    }

    private fun sendResult(action: String) {
        if (resultSent) {
            return
        }
        resultSent = true
        try {
            setFragmentResult(RESULT_KEY, Bundle().apply { putString(RESULT_ACTION, action) })
        } catch (e: Exception) {
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        sendResult(ACTION_RESUME)
        super.onCancel(dialog)
    }

    override fun onDestroyView() {
        sendResult(ACTION_RESUME)
        val views = binding
        if (views != null) {
            PressFeedback.detach(views.pauseDialogResume)
            PressFeedback.detach(views.pauseDialogMenu)
        }
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "pause"
        const val RESULT_KEY = "pause_result"
        const val RESULT_ACTION = "pause_action"
        const val ACTION_RESUME = "resume"
        const val ACTION_MENU = "menu"
    }
}
