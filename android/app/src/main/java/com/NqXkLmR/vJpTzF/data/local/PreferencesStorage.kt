package com.NqXkLmR.vJpTzF.data.local

import android.content.Context
import android.content.SharedPreferences
import com.NqXkLmR.vJpTzF.data.sample.SampleData

class PreferencesStorage(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    var bestChain: Int
        get() = preferences.getInt(KEY_BEST_CHAIN, 0)
        set(value) = preferences.edit().putInt(KEY_BEST_CHAIN, value).apply()

    var bestAccuracy: Int
        get() = preferences.getInt(KEY_BEST_ACCURACY, 0)
        set(value) = preferences.edit().putInt(KEY_BEST_ACCURACY, value).apply()

    var totalPoints: Int
        get() = preferences.getInt(KEY_TOTAL_POINTS, 0)
        set(value) = preferences.edit().putInt(KEY_TOTAL_POINTS, value).apply()

    var runsPlayed: Int
        get() = preferences.getInt(KEY_RUNS_PLAYED, 0)
        set(value) = preferences.edit().putInt(KEY_RUNS_PLAYED, value).apply()

    var selectedShiftId: String
        get() = preferences.getString(KEY_SELECTED_SHIFT, SampleData.DEFAULT_SHIFT_ID)
            ?: SampleData.DEFAULT_SHIFT_ID
        set(value) = preferences.edit().putString(KEY_SELECTED_SHIFT, value).apply()

    var tutorialSeen: Boolean
        get() = preferences.getBoolean(KEY_TUTORIAL_SEEN, false)
        set(value) = preferences.edit().putBoolean(KEY_TUTORIAL_SEEN, value).apply()

    private companion object {
        const val STORE_NAME = "chiko_road_pulse_store"
        const val KEY_BEST_CHAIN = "best_chain"
        const val KEY_BEST_ACCURACY = "best_accuracy"
        const val KEY_TOTAL_POINTS = "total_points"
        const val KEY_RUNS_PLAYED = "runs_played"
        const val KEY_SELECTED_SHIFT = "selected_shift"
        const val KEY_TUTORIAL_SEEN = "tutorial_seen"
    }
}
