package com.NqXkLmR.vJpTzF.presentation.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.NqXkLmR.vJpTzF.R
import com.NqXkLmR.vJpTzF.core.config.GameConfig
import com.NqXkLmR.vJpTzF.core.di.AppViewModelFactory
import com.NqXkLmR.vJpTzF.core.navigation.Navigator
import com.NqXkLmR.vJpTzF.core.ui.PressFeedback
import com.NqXkLmR.vJpTzF.databinding.FragmentGameBinding
import com.NqXkLmR.vJpTzF.domain.model.PickOutcome
import com.NqXkLmR.vJpTzF.domain.model.RunResult
import com.NqXkLmR.vJpTzF.presentation.dialog.PauseDialog
import com.NqXkLmR.vJpTzF.presentation.gameover.GameOverFragment
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameFragment : Fragment() {

    private var binding: FragmentGameBinding? = null
    private val viewModel: GameViewModel by viewModels { AppViewModelFactory() }
    private val handler = Handler(Looper.getMainLooper())

    private var gestureDetector: GestureDetector? = null
    private var lastFeedbackId = 0L
    private var lastShownSecond = -1L
    private var navigationScheduled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentGameBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val views = binding ?: return
        views.gameHud.setLaneListeners(
            onLeft = { viewModel.moveLane(-1) },
            onRight = { viewModel.moveLane(1) }
        )
        PressFeedback.attach(views.gamePause)
        views.gamePause.setOnClickListener { openPauseDialog() }

        val detector = GestureDetector(requireContext(), SwipeListener { delta ->
            viewModel.moveLane(delta)
        })
        gestureDetector = detector
        views.root.setOnTouchListener { _, event ->
            try {
                detector.onTouchEvent(event)
            } catch (e: Exception) {
            }
            true
        }

        setFragmentResultListener(PauseDialog.RESULT_KEY) { _, bundle ->
            when (bundle.getString(PauseDialog.RESULT_ACTION)) {
                PauseDialog.ACTION_MENU -> goToMenu()
                else -> viewModel.resume()
            }
        }

        observeState()
        viewModel.start()
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

    private fun render(state: GameUiState) {
        val views = binding ?: return
        views.gameTrack.render(state.items, state.cartLane)
        views.gameSequence.render(state.sequence, state.chain)
        views.gameHud.render(state.crashes, state.wrongPicks)
        views.gameChain.text =
            getString(R.string.game_chain_counter, state.chain, state.chainTarget)
        renderTimer(state)
        renderPhase(state)
        renderFeedback(state)
    }

    private fun renderTimer(state: GameUiState) {
        val views = binding ?: return
        val context = context ?: return
        val totalSeconds = state.timeLeftMs / 1000L
        if (totalSeconds == lastShownSecond) {
            return
        }
        lastShownSecond = totalSeconds
        val minutes = (totalSeconds / 60L).toInt()
        val seconds = (totalSeconds % 60L).toInt()
        views.gameTimer.text = getString(R.string.game_timer, minutes, seconds)
        val ratio = if (state.roundDurationMs <= 0L) {
            0
        } else {
            (state.timeLeftMs * 1000L / state.roundDurationMs).toInt()
        }
        views.gameTimeBar.progress = ratio
        val colorRes = if (totalSeconds <= LOW_TIME_SECONDS) {
            R.color.accent_coral
        } else {
            R.color.accent_cyan
        }
        views.gameTimeBar.setIndicatorColor(ContextCompat.getColor(context, colorRes))
    }

    private fun renderPhase(state: GameUiState) {
        val views = binding ?: return
        views.gameReady.visibility =
            if (state.phase == GamePhase.READY) View.VISIBLE else View.GONE
        views.gameHud.setControlsEnabled(state.phase == GamePhase.RUNNING)
        if (state.phase == GamePhase.FINISHED && state.result != null) {
            scheduleResult(state.result)
        }
    }

    private fun renderFeedback(state: GameUiState) {
        if (state.feedbackId == lastFeedbackId) {
            return
        }
        lastFeedbackId = state.feedbackId
        val views = binding ?: return
        val context = context ?: return
        when (state.feedback) {
            PickOutcome.CORRECT -> {
                views.gameTrack.flashFrame(ContextCompat.getColor(context, R.color.accent_lime))
                views.gameSequence.shiftOut()
                showPlusOne()
            }

            PickOutcome.WRONG_COLOR -> {
                views.gameTrack.flashFrame(ContextCompat.getColor(context, R.color.accent_coral))
                views.gameTrack.shakeCart()
            }

            PickOutcome.CRASH -> {
                views.gameTrack.flashFrame(ContextCompat.getColor(context, R.color.accent_coral))
                views.gameTrack.blinkCart()
                blinkScreen()
            }

            else -> Unit
        }
    }

    private fun showPlusOne() {
        val views = binding ?: return
        val label = views.gamePlusOne
        label.animate().cancel()
        label.visibility = View.VISIBLE
        label.alpha = 1f
        label.translationY = 0f
        label.animate()
            .alpha(0f)
            .translationY(-PLUS_ONE_RISE_DP * resources.displayMetrics.density)
            .setDuration(PLUS_ONE_MS)
            .withEndAction {
                try {
                    if (!isAdded) {
                        return@withEndAction
                    }
                    val current = binding ?: return@withEndAction
                    current.gamePlusOne.visibility = View.GONE
                    current.gamePlusOne.translationY = 0f
                } catch (e: Exception) {
                }
            }
            .start()
    }

    private fun blinkScreen() {
        val views = binding ?: return
        val blink = views.gameBlink
        blink.animate().cancel()
        blink.visibility = View.VISIBLE
        blink.alpha = BLINK_ALPHA
        blink.animate()
            .alpha(0f)
            .setDuration(BLINK_MS)
            .withEndAction {
                try {
                    if (!isAdded) {
                        return@withEndAction
                    }
                    val current = binding ?: return@withEndAction
                    current.gameBlink.visibility = View.INVISIBLE
                } catch (e: Exception) {
                }
            }
            .start()
    }

    private fun scheduleResult(result: RunResult) {
        if (navigationScheduled) {
            return
        }
        navigationScheduled = true
        handler.postDelayed({
            try {
                if (!isAdded || binding == null) {
                    return@postDelayed
                }
                val pause = parentFragmentManager.findFragmentByTag(PauseDialog.TAG)
                if (pause is DialogFragment) {
                    pause.dismissAllowingStateLoss()
                }
                Navigator(parentFragmentManager, R.id.fragment_container)
                    .replaceTopWithFade(
                        GameOverFragment.newInstance(result),
                        GameOverFragment.TAG
                    )
            } catch (e: Exception) {
            }
        }, GameConfig.FINISH_DELAY_MS)
    }

    private fun openPauseDialog() {
        if (!isAdded) {
            return
        }
        try {
            viewModel.pause()
            PauseDialog().show(parentFragmentManager, PauseDialog.TAG)
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
        handler.removeCallbacksAndMessages(null)
        val views = binding
        if (views != null) {
            views.root.setOnTouchListener(null)
            PressFeedback.detach(views.gamePause)
            views.gamePlusOne.animate().cancel()
            views.gameBlink.animate().cancel()
            views.gameSequence.cancelAnimations()
            views.gameTrack.resetEffects()
        }
        gestureDetector = null
        binding = null
        super.onDestroyView()
    }

    private class SwipeListener(
        private val onSwipe: (Int) -> Unit
    ) : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean = true

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val start = e1 ?: return false
            val deltaX = e2.x - start.x
            val deltaY = e2.y - start.y
            if (abs(deltaX) < SWIPE_MIN_PX || abs(deltaX) <= abs(deltaY)) {
                return false
            }
            onSwipe(if (deltaX > 0f) 1 else -1)
            return true
        }

        private companion object {
            const val SWIPE_MIN_PX = 60f
        }
    }

    companion object {
        const val TAG = "game"
        private const val LOW_TIME_SECONDS = 15L
        private const val PLUS_ONE_RISE_DP = 26f
        private const val PLUS_ONE_MS = 420L
        private const val BLINK_ALPHA = 0.33f
        private const val BLINK_MS = 180L
    }
}
