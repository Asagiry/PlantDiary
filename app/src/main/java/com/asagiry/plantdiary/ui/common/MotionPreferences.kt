package com.asagiry.plantdiary.ui.common

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.provider.Settings

fun Context.shouldReduceMotion(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val lowRamDevice = activityManager?.isLowRamDevice == true
    val animationsDisabled =
        runCatching {
            Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        }.getOrDefault(false)

    return lowRamDevice || animationsDisabled || isProbablyEmulator()
}

private fun isProbablyEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT.lowercase()
    val model = Build.MODEL.lowercase()
    val manufacturer = Build.MANUFACTURER.lowercase()
    val brand = Build.BRAND.lowercase()
    val device = Build.DEVICE.lowercase()
    val product = Build.PRODUCT.lowercase()

    return fingerprint.contains("generic") ||
        fingerprint.contains("emulator") ||
        fingerprint.contains("virtual") ||
        model.contains("emulator") ||
        model.contains("android sdk") ||
        manufacturer.contains("genymotion") ||
        brand.startsWith("generic") ||
        device.startsWith("generic") ||
        product.contains("sdk")
}
