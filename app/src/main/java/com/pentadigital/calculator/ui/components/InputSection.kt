package com.pentadigital.calculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.ui.theme.FinanceGreen
import com.pentadigital.calculator.ui.theme.FinanceGreenDark
import com.pentadigital.calculator.ui.theme.TextSecondaryLight

import androidx.compose.ui.focus.focusRequester

@Composable
fun InputSection(
    title: String,
    value: Double,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    prefix: String = "",
    suffix: String = "",
    imeAction: ImeAction = ImeAction.Done,
    focusRequester: androidx.compose.ui.focus.FocusRequester? = null,
    onNextClick: (() -> Unit)? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = TextSecondaryLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (prefix.isNotEmpty()) {
                    Text(
                        text = prefix,
                        color = FinanceGreenDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                BasicTextField(
                    value = if (value == 0.0) "" else if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.1f", value),
                    onValueChange = {
                        if (it.isEmpty()) {
                            onValueChange(0f)
                        } else {
                            it.toDoubleOrNull()?.let { num ->
                                onValueChange(num.toFloat())
                            }
                        }
                    },
                    modifier = if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier,
                    textStyle = TextStyle(
                        color = FinanceGreenDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = imeAction
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        onNext = {
                            if (onNextClick != null) {
                                onNextClick()
                            } else {
                                focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down)
                            }
                        }
                    ),
                    cursorBrush = SolidColor(FinanceGreenDark)
                )
                if (suffix.isNotEmpty()) {
                    Text(
                        text = suffix,
                        color = FinanceGreenDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = FinanceGreenDark,
                activeTrackColor = FinanceGreenDark,
                inactiveTrackColor = FinanceGreen.copy(alpha = 0.3f)
            )
        )
    }
}
