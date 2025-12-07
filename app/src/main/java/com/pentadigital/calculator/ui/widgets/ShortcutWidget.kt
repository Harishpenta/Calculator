package com.pentadigital.calculator.ui.widgets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pentadigital.calculator.MainActivity
import com.pentadigital.calculator.R

class ShortcutWidget(
    private val title: String,
    private val iconRes: Int,
    private val route: String
) : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetContent(context)
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun WidgetContent(context: Context) {
        val routeKey = ActionParameters.Key<String>("route")
        val intent = Intent(context, MainActivity::class.java)
        
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .clickable(
                    actionStartActivity(
                        intent,
                        parameters = actionParametersOf(routeKey to route)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(iconRes),
                    contentDescription = title,
                    modifier = GlanceModifier.size(32.dp)
                )
                Text(
                    text = title,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = GlanceModifier.padding(top = 4.dp)
                )
            }
        }
    }
}
