package com.projectzenith.official

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import es.dmoral.toasty.Toasty

private data class GamePreset(
    val nameRes: Int,
    val command: String
)

private val maxFpsPreset = GamePreset(
    R.string.preset_max_fps,
    "settings put system min_refresh_rate 120 && settings put system peak_refresh_rate 120 && " +
        "settings put global window_animation_scale 0.5 && settings put global transition_animation_scale 0.5 && " +
        "settings put global animator_duration_scale 0.5"
)

private val balancedPreset = GamePreset(
    R.string.preset_balanced,
    "settings put system min_refresh_rate 60 && settings put system peak_refresh_rate 60 && " +
        "settings put global window_animation_scale 0.5 && settings put global transition_animation_scale 0.5 && " +
        "settings put global animator_duration_scale 0.5"
)

private val batteryPreset = GamePreset(
    R.string.preset_battery,
    "settings put system min_refresh_rate 60 && settings put system peak_refresh_rate 60 && " +
        "settings put global window_animation_scale 1 && settings put global transition_animation_scale 1 && " +
        "settings put global animator_duration_scale 1"
)

internal fun MainActivity.restoreGamePresetStatus() {
    val preset = getSharedPreferences(Prefs.MAIN, Context.MODE_PRIVATE)
        .getString(Prefs.KEY_GAME_PRESET, null)
    if (!preset.isNullOrEmpty()) {
        findViewById<TextView>(R.id.tvStatusGamePresets).text = getString(R.string.status_preset, preset)
    }
}

internal fun MainActivity.showGamePresetDialog() {
    val apply: (GamePreset) -> Unit = { preset ->
        val name = getString(preset.nameRes)
        runAdbCommand(preset.command, getString(R.string.toast_preset_applied, name)) {
            getSharedPreferences(Prefs.MAIN, Context.MODE_PRIVATE).edit()
                .putString(Prefs.KEY_GAME_PRESET, name)
                .apply()
            findViewById<TextView>(R.id.tvStatusGamePresets).text = getString(R.string.status_preset, name)
            fetchSystemStatuses()
        }
    }

    Sheet(this)
        .icon(R.drawable.ic_tune)
        .title(getString(R.string.dlg_presets))
        .message(getString(R.string.dlg_presets_msg))
        .button(getString(R.string.preset_max_fps), SheetStyle.PRIMARY) { apply(maxFpsPreset) }
        .button(getString(R.string.preset_balanced), SheetStyle.TONAL) { apply(balancedPreset) }
        .button(getString(R.string.preset_battery), SheetStyle.TONAL) { apply(batteryPreset) }
        .button(getString(R.string.btn_cancel), SheetStyle.TEXT)
        .show()
}

internal fun MainActivity.resetGamePreset() {
    runAdbCommand(
        "settings delete system min_refresh_rate && settings delete system peak_refresh_rate && " +
            "settings put global window_animation_scale 1 && settings put global transition_animation_scale 1 && " +
            "settings put global animator_duration_scale 1",
        getString(R.string.msg_presets_reset)
    ) {
        getSharedPreferences(Prefs.MAIN, Context.MODE_PRIVATE).edit()
            .remove(Prefs.KEY_GAME_PRESET)
            .apply()
        findViewById<TextView>(R.id.tvStatusGamePresets).text = getString(R.string.status_ready)
        fetchSystemStatuses()
    }
}
