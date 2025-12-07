package com.pentadigital.calculator.utils

import android.content.Context
import android.content.Intent

fun shareResult(context: Context, subject: String, body: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    context.startActivity(Intent.createChooser(intent, "Share Result"))
}
