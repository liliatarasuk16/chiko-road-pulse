package com.NqXkLmR.vJpTzF.presentation.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.di.AppViewModelFactory
import com.NqXkLmR.vJpTzF.core.navigation.Navigator
import com.NqXkLmR.vJpTzF.core.ui.PressFeedback
import com.NqXkLmR.vJpTzF.core.ui.ShiftLabels
import com.NqXkLmR.vJpTzF.databinding.FragmentMenuBinding
import com.NqXkLmR.vJpTzF.presentation.dialog.ShiftSelectDialog
import com.NqXkLmR.vJpTzF.presentation.dialog.TutorialDialog
import com.NqXkLmR.vJpTzF.presentation.game.GameFragment
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var binding: FragmentMenuBinding? = null
    private val viewModel: MenuViewModel by viewModels { AppViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentMenuBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return
        PressFeedback.attach(views.menuPlay)
        PressFeedback.attach(views.menuShift)
        PressFeedback.attach(views.menuTutorial)
        views.menuPlay.setOnClickListener { openGame() }
        views.menuShift.setOnClickListener { openShiftDialog() }
        views.menuTutorial.setOnClickListener { openTutorialDialog() }
        registerDialogResults()
        observeState()
        playEntrance()
        viewModel.refresh()
    }

    private fun registerDialogResults() {
        setFragmentResultListener(ShiftSelectDialog.RESULT_KEY) { _, bundle ->
            val shiftId = bundle.getString(ShiftSelectDialog.RESULT_SHIFT_ID)
            if (shiftId != null) {
                viewModel.selectShift(shiftId)
            }
        }
        setFragmentResultListener(TutorialDialog.RESULT_KEY) { _, _ ->
            viewModel.markTutorialSeen()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: MenuUiState) {
        val views = binding ?: return
        val context = context ?: return
        val shiftLabel = getString(R.string.menu_shift_label) +
            System.lineSeparator() +
            getString(ShiftLabels.shortRes(state.shiftKey))
        views.menuShift.text = shiftLabel

        val showBest = state.bestChain > 0
        val showAccuracy = state.bestAccuracy > 0
        views.menuHeaderBest.visibility = if (showBest) View.VISIBLE else View.GONE
        if (showBest) {
            views.menuHeaderBest.setValue(state.bestChain.toString())
            views.menuHeaderBest.setLabel(getString(R.string.stat_best))
            views.menuHeaderBest.setAccentColor(
                ContextCompat.getColor(context, R.color.accent_amber)
            )
        }

        val showRow = showBest && showAccuracy
        views.menuStatsRow.visibility = if (showRow) View.VISIBLE else View.GONE
        if (showRow) {
            views.menuStatBest.setValue(state.bestChain.toString())
            views.menuStatBest.setLabel(getString(R.string.stat_best))
            views.menuStatAccuracy.setValue(
                getString(R.string.result_accuracy_value, state.bestAccuracy)
            )
            views.menuStatAccuracy.setLabel(getString(R.string.stat_accuracy))
        }
    }

    private fun playEntrance() {
        val views = binding ?: return
        val targets = listOf(
            views.menuHeroTile,
            views.menuHint,
            views.menuPlayRow,
            views.menuStatsRow,
            views.menuTutorial
        )
        val offset = ENTRANCE_OFFSET_DP * resources.displayMetrics.density
        targets.forEachIndexed { index, target ->
            target.alpha = 0f
            target.translationY = offset
            target.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(index * ENTRANCE_STAGGER_MS)
                .setDuration(ENTRANCE_DURATION_MS)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
        views.menuPlay.scaleX = PLAY_START_SCALE
        views.menuPlay.scaleY = PLAY_START_SCALE
        views.menuPlay.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(PLAY_POP_DELAY_MS)
            .setDuration(PLAY_POP_MS)
            .setInterpolator(OvershootInterpolator(1.8f))
            .start()
    }

    private fun openGame() {
        if (!isAdded) {
            return
        }
        try {
            Navigator(parentFragmentManager, R.id.fragment_container)
                .push(GameFragment(), GameFragment.TAG)
        } catch (e: Exception) {
        }
    }

    private fun openShiftDialog() {
        if (!isAdded) {
            return
        }
        try {
            ShiftSelectDialog.newInstance(viewModel.state.value.shiftId)
                .show(parentFragmentManager, ShiftSelectDialog.TAG)
        } catch (e: Exception) {
        }
    }

    private fun openTutorialDialog() {
        if (!isAdded) {
            return
        }
        try {
            TutorialDialog().show(parentFragmentManager, TutorialDialog.TAG)
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        val views = binding
        if (views != null) {
            PressFeedback.detach(views.menuPlay)
            PressFeedback.detach(views.menuShift)
            PressFeedback.detach(views.menuTutorial)
            listOf(
                views.menuHeroTile,
                views.menuHint,
                views.menuPlayRow,
                views.menuStatsRow,
                views.menuTutorial
            ).forEach { it.animate().cancel() }
        }
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "menu"
        private const val ENTRANCE_OFFSET_DP = 20f
        private const val ENTRANCE_DURATION_MS = 260L
        private const val ENTRANCE_STAGGER_MS = 60L
        private const val PLAY_START_SCALE = 0.94f
        private const val PLAY_POP_MS = 320L
        private const val PLAY_POP_DELAY_MS = 180L
    }
}
