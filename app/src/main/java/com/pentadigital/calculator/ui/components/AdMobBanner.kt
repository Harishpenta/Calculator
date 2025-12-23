package com.pentadigital.calculator.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isAdLoaded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    
    if (isAdLoaded) {
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    // Real Banner Ad Unit ID
                    adUnitId = "ca-app-pub-3360377725254476/2293172175"
                    
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d("AdMobBanner", "Ad loaded successfully")
                            isAdLoaded = true
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e("AdMobBanner", "Ad failed to load: ${error.message}")
                            isAdLoaded = false
                        }
                    }
                    
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    } else {
        // Invisible AdView to load the ad, respecting the modifier (padding)
        androidx.compose.ui.viewinterop.AndroidView(
            modifier = modifier.height(0.dp), // Height 0 but respects padding/insets
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = "ca-app-pub-3360377725254476/2293172175"
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            isAdLoaded = true
                        }
                    }
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
