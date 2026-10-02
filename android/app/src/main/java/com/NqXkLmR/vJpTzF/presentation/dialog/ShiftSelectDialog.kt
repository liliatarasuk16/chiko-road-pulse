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
import com.NqXkLmR.vJpTzF.databinding.DialogShiftSelectBinding

class ShiftSelectDialog : DialogFragment() {

    private var binding: DialogShiftSelectBinding? = null
    private var adapter: ShiftPresetAdapter? = null
    private var selectedId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_ChikoRoadPulse_Dialog)
        selectedId = arguments?.getString(ARG_SELECTED).orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = DialogShiftSelectBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val views = binding ?: return
        val presets = ServiceLocator.shiftRepository.presets()
        if (selectedId.isEmpty()) {
            selectedId = presets.first().id
        }
        val listAdapter = ShiftPresetAdapter(presets, selectedId) { shiftId ->
            selectedId = shiftId
            adapter?.select(shiftId)
        }
        adapter = listAdapter
        views.shiftDialogList.layoutManager = LinearLayoutManager(requireContext())
        views.shiftDialogList.adapter = listAdapter
        PressFeedback.attach(views.shiftDialogConfirm)
        PressFeedback.attach(views.shiftDialogClose)
        views.shiftDialogConfirm.setOnClickListener { confirm() }
        views.shiftDialogClose.setOnClickListener { dismissAllowingStateLoss() }
    }

    private fun confirm() {
        try {
            setFragmentResult(RESULT_KEY, Bundle().apply { putString(RESULT_SHIFT_ID, selectedId) })
        } catch (e: Exception) {
        }
        dismissAllowingStateLoss()
    }

    override fun onDestroyView() {
        val views = binding
        if (views != null) {
            PressFeedback.detach(views.shiftDialogConfirm)
            PressFeedback.detach(views.shiftDialogClose)
            views.shiftDialogList.adapter = null
        }
        adapter = null
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "shift_select"
        const val RESULT_KEY = "shift_select_result"
        const val RESULT_SHIFT_ID = "shift_id"
        private const val ARG_SELECTED = "arg_selected"

        fun newInstance(selectedId: String): ShiftSelectDialog {
            val dialog = ShiftSelectDialog()
            dialog.arguments = Bundle().apply { putString(ARG_SELECTED, selectedId) }
            return dialog
        }
    }
}
