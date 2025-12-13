package com.pentadigital.calculator.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.pentadigital.calculator.R

class SoundManager(context: Context) {
    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<CyberpunkSound, Int>()
    private var isSoundEnabled = true 
    // Ideally this would come from settings, but for now we default true or manage externally

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
            
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
            
        // Note: In a real app, you would load MP3/WAV files here.
        // Since we don't have assets, we will set up the structure.
        // If the user provides assets later, they just add R.raw.click_sound
        
        // Example placeholders (commented out until assets exist)
        // soundMap[CyberpunkSound.Click] = soundPool.load(context, R.raw.mech_click, 1)
        // soundMap[CyberpunkSound.Error] = soundPool.load(context, R.raw.error_buzz, 1)
    }

    fun playSound(sound: CyberpunkSound) {
        if (!isSoundEnabled) return
        
        val soundId = soundMap[sound] ?: return
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    fun release() {
        soundPool.release()
    }
}

enum class CyberpunkSound {
    Click,
    Clear,
    Result,
    Error
}
