package com.NqXkLmR.vJpTzF.presentation.gameover

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.di.AppViewModelFactory
import com.NqXkLmR.vJpTzF.core.navigation.Navigator
import com.NqXkLmR.vJpTzF.core.ui.PressFeedback
import com.NqXkLmR.vJpTzF.databinding.FragmentGameoverBinding
import com.NqXkLmR.vJpTzF.domain.model.FinishReason
import com.NqXkLmR.vJpTzF.domain.model.RunResult
import com.NqXkLmR.vJpTzF.presentation.game.GameFragment
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var binding: FragmentGameoverBinding? = null
    private val viewModel: GameOverViewModel by viewModels { AppViewModelFactory() }
    private var pulseAnimator: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentGameoverBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return
        PressFeedback.attach(views.resultPlayAgain)
        PressFeedback.attach(views.resultMenu)
        views.resultPlayAgain.setOnClickListener { playAgain() }
        views.resultMenu.setOnClickListener { goToMenu() }
        observeState()
        viewModel.submit(readResult())
        playEntrance()
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

    private fun render(state: GameOverUiState) {
        val views = binding ?: return
        views.resultScrim.setBackgroundResource(
            if (state.isWin) {
                R.drawable.gradient_result_scrim_win
            } else {
                R.drawable.gradient_result_scrim_lose
            }
        )
        views.resultTitle.setText(
            if (state.isWin) R.string.result_title_win else R.string.result_title_lose
        )
        views.resultReason.setText(reasonRes(state.reason))
        views.resultPoints.text = getString(R.string.result_points_earned, state.points)

        val showChain = state.chain > 0
        val showAccuracy = state.accuracy > 0
        val showBest = state.bestChain > 0
        val visibleCount = listOf(showChain, showAccuracy, showBest).count { it }
        views.resultStatsRow.visibility = if (visibleCount > 1) View.VISIBLE else View.GONE
        views.resultStatChain.visibility = if (showChain) View.VISIBLE else View.INVISIBLE
        views.resultStatAccuracy.visibility = if (showAccuracy) View.VISIBLE else View.INVISIBLE
        views.resultStatBest.visibility = if (showBest) View.VISIBLE else View.INVISIBLE
        if (showChain) {
            views.resultStatChain.setValue(
                getString(R.string.result_chain_value, state.chain, state.chainTarget)
            )
            views.resultStatChain.setLabel(getString(R.string.stat_chain))
        }
        if (showAccuracy) {
            views.resultStatAccuracy.setValue(
                getString(R.string.result_accuracy_value, state.accuracy)
            )
            views.resultStatAccuracy.setLabel(getString(R.string.stat_accuracy))
        }
        if (showBest) {
            views.resultStatBest.setValue(state.bestChain.toString())
            views.resultStatBest.setLabel(getString(R.string.stat_best))
        }

        if (state.isNewBest) {
            showNewBest()
        } else {
            views.resultNewBest.visibility = View.GONE
        }
    }

    private fun reasonRes(reason: FinishReason): Int = when (reason) {
        FinishReason.CHAIN_DONE -> R.string.result_reason_chain
        FinishReason.TOO_MANY_CRASHES -> R.string.result_reason_crashes
        FinishReason.TOO_MANY_WRONG_PICKS -> R.string.result_reason_wrong
        FinishReason.TIME_UP -> R.string.result_reason_time
    }

    private fun showNewBest() {
        val views = binding ?: return
        if (views.resultNewBest.visibility == View.VISIBLE) {
            return
        }
        val badge = views.resultNewBest
        badge.visibility = View.VISIBLE
        badge.scaleX = NEW_BEST_START_SCALE
        badge.scaleY = NEW_BEST_START_SCALE
        badge.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(NEW_BEST_POP_MS)
            .setInterpolator(OvershootInterpolator(2.5f))
            .withEndAction {
                try {
                    if (!isAdded) {
                        return@withEndAction
                    }
                    val current = binding ?: return@withEndAction
                    startBadgePulse(current.resultNewBest)
                } catch (e: Exception) {
                }
            }
            .start()
    }

    private fun startBadgePulse(badge: View) {
        pulseAnimator?.cancel()
        val animator = ObjectAnimator.ofFloat(badge, View.SCALE_X, 1f, NEW_BEST_PULSE_SCALE)
        animator.duration = NEW_BEST_PULSE_MS
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
        pulseAnimator = animator
    }

    private fun playEntrance() {
        val views = binding ?: return
        val offset = ENTRANCE_OFFSET_DP * resources.displayMetrics.density
        val targets = listOf(
            views.resultVerdict,
            views.resultStatsRow,
            views.resultPoints,
            views.resultPlayAgain,
            views.resultMenu
        )
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
    }

    private fun readResult(): RunResult {
        val args = arguments
        val reasonName = args?.getString(ARG_REASON) ?: FinishReason.TIME_UP.name
        val reason = FinishReason.values().firstOrNull { it.name == reasonName }
            ?: FinishReason.TIME_UP
        return RunResult(
            chain = args?.getInt(ARG_CHAIN) ?: 0,
            chainTarget = args?.getInt(ARG_CHAIN_TARGET) ?: 1,
            correctPicks = args?.getInt(ARG_CORRECT) ?: 0,
            wrongPicks = args?.getInt(ARG_WRONG) ?: 0,
            crashes = args?.getInt(ARG_CRASHES) ?: 0,
            accuracy = args?.getInt(ARG_ACCURACY) ?: 0,
            points = args?.getInt(ARG_POINTS) ?: 0,
            isWin = args?.getBoolean(ARG_IS_WIN) ?: false,
            reason = reason
        )
    }

    private fun playAgain() {
        if (!isAdded) {
            return
        }
        try {
            Navigator(parentFragmentManager, R.id.fragment_container)
                .replaceTopWithFade(GameFragment(), GameFragment.TAG)
        } catch (e: Exception) {
        }
    }

    private fun goToMenu() {
        if (!isAdded) {
            return
        }
        try {
            Navigator(parentFragmentManager, R.id.fragment_container).backToRoot()
        } catch (e: Exception) {
        }
    }

    override fun onDestroyView() {
        pulseAnimator?.cancel()
        pulseAnimator = null
        val views = binding
        if (views != null) {
            PressFeedback.detach(views.resultPlayAgain)
            PressFeedback.detach(views.resultMenu)
            listOf(
                views.resultVerdict,
                views.resultStatsRow,
                views.resultPoints,
                views.resultPlayAgain,
                views.resultMenu,
                views.resultNewBest
            ).forEach { it.animate().cancel() }
        }
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "gameover"

        private const val ARG_CHAIN = "arg_chain"
        private const val ARG_CHAIN_TARGET = "arg_chain_target"
        private const val ARG_CORRECT = "arg_correct"
        private const val ARG_WRONG = "arg_wrong"
        private const val ARG_CRASHES = "arg_crashes"
        private const val ARG_ACCURACY = "arg_accuracy"
        private const val ARG_POINTS = "arg_points"
        private const val ARG_IS_WIN = "arg_is_win"
        private const val ARG_REASON = "arg_reason"

        private const val ENTRANCE_OFFSET_DP = 28f
        private const val ENTRANCE_DURATION_MS = 300L
        private const val ENTRANCE_STAGGER_MS = 70L
        private const val NEW_BEST_START_SCALE = 0.8f
        private const val NEW_BEST_POP_MS = 380L
        private const val NEW_BEST_PULSE_SCALE = 1.03f
        private const val NEW_BEST_PULSE_MS = 1000L

        fun newInstance(result: RunResult): GameOverFragment {
            val fragment = GameOverFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_CHAIN, result.chain)
                putInt(ARG_CHAIN_TARGET, result.chainTarget)
                putInt(ARG_CORRECT, result.correctPicks)
                putInt(ARG_WRONG, result.wrongPicks)
                putInt(ARG_CRASHES, result.crashes)
                putInt(ARG_ACCURACY, result.accuracy)
                putInt(ARG_POINTS, result.points)
                putBoolean(ARG_IS_WIN, result.isWin)
                putString(ARG_REASON, result.reason.name)
            }
            return fragment
        }
    }
}
