package com.pentadigital.calculator.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticManager(private val context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun performHapticFeedback(type: CyberpunkHapticType) {
        if (!vibrator.hasVibrator()) return

        val effect = when (type) {
            CyberpunkHapticType.LightTick -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                } else {
                    VibrationEffect.createOneShot(10, 50) // Reduced duration for lighter feel
                }
            }
            CyberpunkHapticType.MediumClick -> {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                } else {
                    VibrationEffect.createOneShot(20, 100)
                }
            }
            CyberpunkHapticType.HeavyThud -> {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                } else {
                     VibrationEffect.createOneShot(50, 255) // Max amplitude
                }
            }
            CyberpunkHapticType.Glitch -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Pattern: tick-tick-buzz
                     VibrationEffect.createWaveform(longArrayOf(10, 30, 20, 30), intArrayOf(100, 0, 200, 0), -1)
                } else {
                    VibrationEffect.createOneShot(40, 150)
                }
            }
        }
        
        vibrator.cancel() // Cancel previous vibration for crispness
        vibrator.vibrate(effect)
    }
}

enum class CyberpunkHapticType {
    LightTick,
    MediumClick,
    HeavyThud,
    Glitch
}
