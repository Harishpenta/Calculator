package com.pentadigital.calculator.ui.screens.compound

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.ui.components.*

import java.text.NumberFormat
import java.util.Currency

@Composable
fun CicResultSection(state: CompoundInterestState) {
    val result = state.result ?: return

    val formatCurrency = { amount: java.math.BigDecimal ->
        val formatter = NumberFormat.getCurrencyInstance(state.selectedCurrency.locale)
        formatter.currency = Currency.getInstance(state.selectedCurrency.code)
        formatter.format(amount)
    }

    CyberpunkCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = Color(0xFF00FFCC) // Neon Cyan
    ) {
        TechText("CALCULATION RESULTS", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Top prominent result
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TechText("Future Investment Value", fontSize = 14.sp, color = Color.Gray)
            Text(
                text = formatCurrency(result.futureValue),
                color = Color(0xFF00FFCC),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Secondary metrics grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricBox(
                label = "Total Interest",
                value = formatCurrency(result.totalInterestEarned),
                color = Color(0xFFFF9900), // Orange-ish
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MetricBox(
                label = "Initial Balance",
                value = formatCurrency(result.principal),
                color = Color(0xFF00BFFF), // Blue-ish
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricBox(
                label = "All-time RoR",
                value = "${result.allTimeRateOfReturn.toPlainString()}%",
                color = Color(0xFF32CD32), // Lime Green
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MetricBox(
                label = "APY (Compounded Rate)",
                value = "${result.apy.toPlainString()}%",
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        TechText(label, fontSize = 11.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        TechText(value, fontSize = 16.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CicBreakdownSection(
    state: CompoundInterestState,
    viewModel: CompoundInterestViewModel
) {
    val result = state.result ?: return
    val breakdown = if (state.isYearlyBreakdown) result.yearlyBreakdown else result.monthlyBreakdown

    CyberpunkCard(modifier = Modifier.fillMaxWidth()) {
        val formatCurrency = { amount: java.math.BigDecimal ->
            val formatter = NumberFormat.getCurrencyInstance(state.selectedCurrency.locale)
            formatter.setCurrency(java.util.Currency.getInstance(state.selectedCurrency.code))
            formatter.format(amount)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TechText(if (state.isYearlyBreakdown) "YEARLY BREAKDOWN" else "MONTHLY BREAKDOWN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            
            // Toggle
            Row(
                modifier = Modifier
                    .background(Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(2.dp)
            ) {
                TextButton(
                    onClick = { viewModel.onEvent(CompoundInterestEvent.ToggleBreakdownView(false)) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (!state.isYearlyBreakdown) Color.White else Color.Gray,
                        containerColor = if (!state.isYearlyBreakdown) MaterialTheme.colorScheme.primary else Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Monthly", fontSize = 12.sp)
                }
                TextButton(
                    onClick = { viewModel.onEvent(CompoundInterestEvent.ToggleBreakdownView(true)) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (state.isYearlyBreakdown) Color.White else Color.Gray,
                        containerColor = if (state.isYearlyBreakdown) MaterialTheme.colorScheme.primary else Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Yearly", fontSize = 12.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(vertical = 8.dp, horizontal = 4.dp),
        ) {
            TechText("Period", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.White)
            TechText("Interest", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = Color.White, textAlign = TextAlign.End)
            TechText("Accrued Int.", modifier = Modifier.weight(1.5f), fontSize = 12.sp, color = Color.White, textAlign = TextAlign.End)
            TechText("Balance", modifier = Modifier.weight(2f), fontSize = 12.sp, color = Color.White, textAlign = TextAlign.End)
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.primary)

        // Custom list behavior inside a scrolling Column.
        // It's acceptable since the list size isn't massive (e.g. 50 years max typically).
        // A LazyColumn would conflict with the parent verticalScroll.
        Column(modifier = Modifier.fillMaxWidth()) {
            breakdown.forEachIndexed { index, row ->
                val bgColor = if (index % 2 == 0) Color.Transparent else Color.White.copy(alpha = 0.05f)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TechText(row.periodLabel, modifier = Modifier.weight(1f), fontSize = 11.sp)
                    TechText(formatCurrency(row.interestEarned), modifier = Modifier.weight(1.5f), fontSize = 11.sp, textAlign = TextAlign.End)
                    TechText(formatCurrency(row.totalInterestAccrued), modifier = Modifier.weight(1.5f), fontSize = 11.sp, textAlign = TextAlign.End)
                    TechText(
                        formatCurrency(row.balance),
                        modifier = Modifier.weight(2f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF32CD32),
                        textAlign = TextAlign.End
                    )
                }
                HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f))
            }
        }
    }
}
