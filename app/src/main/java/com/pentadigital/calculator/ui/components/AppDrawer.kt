package com.pentadigital.calculator.ui.components

import com.pentadigital.calculator.ui.navigation.Screen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pentadigital.calculator.ui.theme.MediumGray

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AppDrawer(
    navController: NavController,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MediumGray)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Menu",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Divider(color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        DrawerItem(
            text = Screen.Basic.title,
            isSelected = currentRoute == Screen.Basic.route,
            onClick = {
                navController.navigate(Screen.Basic.route) {
                    popUpTo(Screen.Basic.route)
                }
                closeDrawer()
            }
        )
        DrawerItem(
            text = Screen.SIP.title,
            isSelected = currentRoute == Screen.SIP.route,
            onClick = {
                navController.navigate(Screen.SIP.route) {
                    popUpTo(Screen.Basic.route)
                }
                closeDrawer()
            }
        )
        DrawerItem(
            text = Screen.EMI.title,
            isSelected = currentRoute == Screen.EMI.route,
            onClick = {
                navController.navigate(Screen.EMI.route) {
                    popUpTo(Screen.Basic.route)
                }
                closeDrawer()
            }
        )
        DrawerItem(
            text = Screen.BMI.title,
            isSelected = currentRoute == Screen.BMI.route,
            onClick = {
                navController.navigate(Screen.BMI.route) {
                    popUpTo(Screen.Basic.route)
                }
                closeDrawer()
            }
        )
        DrawerItem(
            text = Screen.Age.title,
            isSelected = currentRoute == Screen.Age.route,
            onClick = {
                navController.navigate(Screen.Age.route) {
                    popUpTo(Screen.Basic.route)
                }
                closeDrawer()
            }
        )
        DrawerItem(
            text = Screen.Currency.title,
            isSelected = currentRoute == Screen.Currency.route,
            onClick = {
                navController.navigate(Screen.Currency.route) {
                    popUpTo(Screen.Basic.route)
                }
                closeDrawer()
            }
        )
        DrawerItem(
            text = Screen.UnitConverter.title,
            isSelected = currentRoute == Screen.UnitConverter.route,
            onClick = {
                navController.navigate(Screen.UnitConverter.route) {
                    popUpTo(Screen.Basic.route)
                }
                closeDrawer()
            }
        )
        DrawerItem(
            text = Screen.Settings.title,
            isSelected = currentRoute == Screen.Settings.route,
            onClick = {
                navController.navigate(Screen.Settings.route) {
                    popUpTo(Screen.Basic.route)
                }
                closeDrawer()
            }
        )
    }
}

@Composable
fun DrawerItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .background(if (isSelected) Color.White.copy(alpha = 0.1f) else Color.Transparent)
            .padding(8.dp),
        fontSize = 18.sp
    )
}
