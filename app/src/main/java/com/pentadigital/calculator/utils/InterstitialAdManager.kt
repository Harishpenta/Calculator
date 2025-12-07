package com.pentadigital.calculator.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        // Real Interstitial Ad Unit ID
        InterstitialAd.load(
            context,
            "ca-app-pub-3360377725254476/8667008833",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("InterstitialAd", "Failed to load: ${adError.message}")
                    interstitialAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("InterstitialAd", "Ad loaded successfully")
                    interstitialAd = ad
                }
            }
        )
    }

    fun showAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("InterstitialAd", "Ad dismissed")
                    interstitialAd = null
                    loadAd() // Load the next ad
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e("InterstitialAd", "Failed to show: ${adError.message}")
                    interstitialAd = null
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("InterstitialAd", "Ad showed")
                }
            }
            interstitialAd?.show(activity)
        } else {
            Log.d("InterstitialAd", "Ad not ready, loading for next time")
            loadAd() // Try to load for next time
            onAdDismissed()
        }
    }
}
