package com.NqXkLmR.vJpTzF.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.NqXkLmR.vJpTzF.core.config.GameConfig
import com.NqXkLmR.vJpTzF.domain.model.FinishReason
import com.NqXkLmR.vJpTzF.domain.model.Lane
import com.NqXkLmR.vJpTzF.domain.model.ParcelColor
import com.NqXkLmR.vJpTzF.domain.model.PickOutcome
import com.NqXkLmR.vJpTzF.domain.model.RoadItem
import com.NqXkLmR.vJpTzF.domain.model.RunResult
import com.NqXkLmR.vJpTzF.domain.model.ShiftPreset
import com.NqXkLmR.vJpTzF.domain.repository.ShiftRepository
import com.NqXkLmR.vJpTzF.domain.usecase.EvaluatePickUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.GenerateSequenceUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.GetProfileUseCase
import com.NqXkLmR.vJpTzF.domain.usecase.SpawnPlanUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class GameViewModel(
    private val getProfile: GetProfileUseCase,
    private val shiftRepository: ShiftRepository,
    private val generateSequence: GenerateSequenceUseCase,
    private val spawnPlan: SpawnPlanUseCase,
    private val evaluatePick: EvaluatePickUseCase
) : ViewModel() {

    private val mutableState = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = mutableState.asStateFlow()

    private val items = ArrayList<RoadItem>()

    private var preset: ShiftPreset = shiftRepository.presets().first()
    private var sequence: List<ParcelColor> = emptyList()
    private var phase = GamePhase.READY
    private var cartLane = Lane.CENTER
    private var nextId = 1L
    private var feedbackCounter = 0L
    private var lastFeedback: PickOutcome? = null
    private var elapsedMs = 0L
    private var sinceSpawnMs = 0L
    private var chain = 0
    private var correctPicks = 0
    private var wrongPicks = 0
    private var crashes = 0
    private var speedFactor = 1f
    private var invulnerableUntilMs = -1L
    private var lastInputMs = -1L
    private var started = false
    private var loopJob: Job? = null
    private var finalResult: RunResult? = null

    fun start() {
        if (started) {
            return
        }
        started = true
        val profile = getProfile()
        preset = shiftRepository.byId(profile.selectedShiftId)
        sequence = generateSequence(preset.chainTarget)
        speedFactor = preset.speedFactor
        phase = GamePhase.READY
        publish()
        loopJob = viewModelScope.launch {
            delay(GameConfig.READY_FLASH_MS)
            phase = GamePhase.RUNNING
            publish()
            runLoop()
        }
    }

    fun moveLane(delta: Int) {
        if (phase != GamePhase.RUNNING) {
            return
        }
        lastInputMs = elapsedMs
        val target = cartLane.shifted(delta)
        if (target != cartLane) {
            cartLane = target
            publish()
        }
    }

    fun pause() {
        if (phase == GamePhase.RUNNING) {
            phase = GamePhase.PAUSED
            publish()
        }
    }

    fun resume() {
        if (phase == GamePhase.PAUSED) {
            lastInputMs = elapsedMs
            phase = GamePhase.RUNNING
            publish()
        }
    }

    private suspend fun runLoop() {
        while (phase != GamePhase.FINISHED) {
            delay(GameConfig.TICK_MS)
            elapsedMs += GameConfig.TICK_MS
            if (phase == GamePhase.RUNNING) {
                advanceItems()
                maybeSpawn()
                resolvePicks()
            }
            if (checkRoundEnd()) {
                return
            }
            publish()
        }
    }

    private fun advanceItems() {
        val travelMs = GameConfig.BASE_TRAVEL_MS / speedFactor
        val step = GameConfig.TICK_MS / travelMs
        var index = 0
        while (index < items.size) {
            val item = items[index]
            val moved = item.copy(progress = item.progress + step)
            if (moved.progress > GameConfig.DESPAWN_PROGRESS) {
                items.removeAt(index)
            } else {
                items[index] = moved
                index++
            }
        }
    }

    private fun maybeSpawn() {
        sinceSpawnMs += GameConfig.TICK_MS
        val ramp = (speedFactor / preset.speedFactor).coerceAtLeast(1f)
        val interval = (preset.spawnIntervalMs / ramp).toLong().coerceAtLeast(320L)
        if (sinceSpawnMs < interval) {
            return
        }
        sinceSpawnMs = 0L
        val targetColor = sequence.getOrNull(chain) ?: return
        val wave = spawnPlan(targetColor, cartLane, nextId)
        nextId += wave.size + 1
        items.addAll(wave)
    }

    private fun resolvePicks() {
        var index = 0
        while (index < items.size) {
            val item = items[index]
            if (item.resolved || item.progress < GameConfig.PICK_PROGRESS) {
                index++
                continue
            }
            items[index] = item.copy(resolved = true)
            val outcome = evaluatePick(
                item,
                cartLane,
                sequence.getOrNull(chain),
                elapsedMs < invulnerableUntilMs
            )
            applyOutcome(outcome)
            index++
        }
    }

    private fun applyOutcome(outcome: PickOutcome) {
        when (outcome) {
            PickOutcome.CORRECT -> {
                chain++
                correctPicks++
                if (correctPicks % GameConfig.SPEED_STEP_EVERY == 0) {
                    speedFactor = min(
                        speedFactor * GameConfig.SPEED_STEP,
                        preset.speedFactor * GameConfig.MAX_SPEED_FACTOR
                    )
                }
                registerFeedback(outcome)
            }

            PickOutcome.WRONG_COLOR -> {
                wrongPicks++
                registerFeedback(outcome)
            }

            PickOutcome.CRASH -> {
                crashes++
                invulnerableUntilMs = elapsedMs + GameConfig.INVULNERABLE_MS
                registerFeedback(outcome)
            }

            PickOutcome.IGNORED -> Unit
        }
    }

    private fun registerFeedback(outcome: PickOutcome) {
        feedbackCounter++
        lastFeedback = outcome
    }

    private fun checkRoundEnd(): Boolean {
        if (chain >= preset.chainTarget) {
            finish(FinishReason.CHAIN_DONE, true)
            return true
        }
        if (crashes >= GameConfig.MAX_CRASHES) {
            finish(FinishReason.TOO_MANY_CRASHES, false)
            return true
        }
        if (wrongPicks >= GameConfig.MAX_WRONG_PICKS) {
            finish(FinishReason.TOO_MANY_WRONG_PICKS, false)
            return true
        }
        if (elapsedMs >= preset.roundDurationMs) {
            finish(FinishReason.TIME_UP, false)
            return true
        }
        if (elapsedMs >= idleDeadline()) {
            finish(FinishReason.TIME_UP, false)
            return true
        }
        return false
    }

    private fun idleDeadline(): Long {
        if (lastInputMs < 0L) {
            return GameConfig.IDLE_FORCE_END_MS
        }
        return max(lastInputMs + GameConfig.ENGAGED_IDLE_MS, GameConfig.MIN_RUN_MS_FROM_START)
    }

    private fun finish(reason: FinishReason, isWin: Boolean) {
        phase = GamePhase.FINISHED
        val attempts = correctPicks + wrongPicks
        val accuracy = if (attempts == 0) 0 else correctPicks * 100 / attempts
        val points = if (isWin) {
            preset.reward
        } else {
            preset.reward * chain / preset.chainTarget.coerceAtLeast(1)
        }
        finalResult = RunResult(
            chain = chain,
            chainTarget = preset.chainTarget,
            correctPicks = correctPicks,
            wrongPicks = wrongPicks,
            crashes = crashes,
            accuracy = accuracy,
            points = points,
            isWin = isWin,
            reason = reason
        )
        items.clear()
        publish()
    }

    private fun publish() {
        mutableState.value = GameUiState(
            phase = phase,
            items = items.toList(),
            cartLane = cartLane,
            sequence = sequence,
            chain = chain,
            chainTarget = preset.chainTarget,
            wrongPicks = wrongPicks,
            crashes = crashes,
            timeLeftMs = (preset.roundDurationMs - elapsedMs).coerceAtLeast(0L),
            roundDurationMs = preset.roundDurationMs,
            feedbackId = feedbackCounter,
            feedback = lastFeedback,
            result = finalResult
        )
    }

    override fun onCleared() {
        loopJob?.cancel()
        loopJob = null
        super.onCleared()
    }
}
