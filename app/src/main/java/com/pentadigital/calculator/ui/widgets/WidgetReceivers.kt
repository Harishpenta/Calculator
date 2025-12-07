package com.pentadigital.calculator.ui.widgets

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.navigation.Screen

class CalculatorWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShortcutWidget(
        title = "Calculator",
        iconRes = R.drawable.ic_calculator,
        route = Screen.Basic.route
    )
}

class BmiWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShortcutWidget(
        title = "BMI",
        iconRes = R.drawable.ic_bmi,
        route = Screen.BMI.route
    )
}

class AgeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShortcutWidget(
        title = "Age",
        iconRes = R.drawable.ic_age,
        route = Screen.Age.route
    )
}

class EmiWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShortcutWidget(
        title = "EMI",
        iconRes = R.drawable.ic_loan, // Assuming loan icon for EMI
        route = Screen.EMI.route
    )
}

class SipWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShortcutWidget(
        title = "SIP",
        iconRes = R.drawable.ic_investment, // Assuming investment icon for SIP
        route = Screen.SIP.route
    )
}

class CurrencyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShortcutWidget(
        title = "Currency",
        iconRes = R.drawable.ic_currency,
        route = Screen.Currency.route
    )
}

class UnitConverterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShortcutWidget(
        title = "Units",
        iconRes = R.drawable.ic_unit_converter,
        route = Screen.UnitConverter.route
    )
}

class GeometryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ShortcutWidget(
        title = "Geometry",
        iconRes = R.drawable.ic_geometry,
        route = Screen.Geometry.route
    )
}
