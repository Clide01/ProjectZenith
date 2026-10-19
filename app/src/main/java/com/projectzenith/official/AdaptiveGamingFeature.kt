package com.projectzenith.official

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.TextView

internal data class AdaptiveProfile(val refreshRate: Int, val animations: String)

internal fun MainActivity.currentAdaptiveProfile(): AdaptiveProfile {
    val battery = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, 100) ?: 100
    return if (level <= 30) AdaptiveProfile(60, "1") else AdaptiveProfile(120, "0.5")
}

internal fun MainActivity.adaptiveCommand(packageName: String): String {
    val profile = currentAdaptiveProfile()
    return "cmd game mode performance $packageName; " +
        "settings put system min_refresh_rate ${profile.refreshRate}; " +
        "settings put system peak_refresh_rate ${profile.refreshRate}; " +
        "settings put global window_animation_scale ${profile.animations}; " +
        "settings put global transition_animation_scale ${profile.animations}; " +
        "settings put global animator_duration_scale ${profile.animations}"
}

internal fun MainActivity.showAdaptiveGamingDialog() {
    showGamePicker(getString(R.string.dlg_adaptive), getString(R.string.dlg_adaptive_msg)) { selectedApp ->
        val packageName = selectedApp.activityInfo.packageName
        val appName = selectedApp.loadLabel(packageManager).toString()
        runAdbCommand(adaptiveCommand(packageName), getString(R.string.toast_adaptive_enabled, appName)) {
            getSharedPreferences(Prefs.MAIN, Context.MODE_PRIVATE).edit()
                .putString(Prefs.KEY_ADAPTIVE_GAME, packageName)
                .apply()
            findViewById<TextView>(R.id.tvStatusAdaptive).text = getString(R.string.status_adaptive, appName)
            fetchSystemStatuses()
        }
    }
}

internal fun MainActivity.resetAdaptiveGaming() {
    val packageName = getSharedPreferences(Prefs.MAIN, Context.MODE_PRIVATE)
        .getString(Prefs.KEY_ADAPTIVE_GAME, null)
    val gameReset = if (packageName.isNullOrEmpty()) "" else "cmd game mode standard $packageName; "
    runAdbCommand(
        gameReset +
            "settings delete system min_refresh_rate; settings delete system peak_refresh_rate; " +
            "settings put global window_animation_scale 1; settings put global transition_animation_scale 1; " +
            "settings put global animator_duration_scale 1",
        getString(R.string.toast_adaptive_reset)
    ) {
        getSharedPreferences(Prefs.MAIN, Context.MODE_PRIVATE).edit()
            .remove(Prefs.KEY_ADAPTIVE_GAME)
            .apply()
        findViewById<TextView>(R.id.tvStatusAdaptive).text = getString(R.string.status_default)
        fetchSystemStatuses()
    }
}

internal fun MainActivity.restoreAdaptiveGamingStatus() {
    val packageName = getSharedPreferences(Prefs.MAIN, Context.MODE_PRIVATE)
        .getString(Prefs.KEY_ADAPTIVE_GAME, null)
    if (!packageName.isNullOrEmpty()) {
        val label = try {
            packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString()
        } catch (_: Exception) {
            packageName
        }
        findViewById<TextView>(R.id.tvStatusAdaptive).text = getString(R.string.status_adaptive, label)
    }
}
