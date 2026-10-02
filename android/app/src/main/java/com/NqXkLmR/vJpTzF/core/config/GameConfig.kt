package com.NqXkLmR.vJpTzF.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val SPLASH_PROGRESS_STEP_MS = 160L

    const val LANE_COUNT = 3
    const val CHAIN_TARGET = 30
    const val MAX_CRASHES = 3
    const val MAX_WRONG_PICKS = 5

    const val TICK_MS = 16L
    const val READY_FLASH_MS = 600L
    const val FINISH_DELAY_MS = 900L
    const val LANE_SHIFT_MS = 140L
    const val INVULNERABLE_MS = 700L
    const val FEEDBACK_FLASH_MS = 160L

    const val IDLE_FORCE_END_MS = 28000L
    const val ENGAGED_IDLE_MS = 12000L
    const val MIN_RUN_MS_FROM_START = 26000L

    const val BOARD_PAD_DP = 6
    const val BOARD_BORDER_DP = 3
    const val BOARD_MAX_W_DP = 380
    const val BOARD_RESERVED_BOTTOM_DP = 140
    const val ROAD_ITEM_DP = 72

    const val PICK_PROGRESS = 0.86f
    const val DESPAWN_PROGRESS = 1.12f
    const val BASE_TRAVEL_MS = 2600f
    const val MAX_SPEED_FACTOR = 1.45f
    const val SPEED_STEP = 1.06f
    const val SPEED_STEP_EVERY = 6
    const val SEQUENCE_WINDOW = 7
}
