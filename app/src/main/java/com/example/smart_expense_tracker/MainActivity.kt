package com.example.smart_expense_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smart_expense_tracker.ui.screens.AssetsScreen
import com.example.smart_expense_tracker.ui.screens.HomeScreen
import com.example.smart_expense_tracker.ui.screens.StatisticsScreen
import com.example.smart_expense_tracker.ui.screens.AiScreen
import com.example.smart_expense_tracker.ui.theme.SmartExpenseTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartExpenseTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartExpenseApp()
                }
            }
        }
    }
}

@Composable
fun SmartExpenseApp(
    navController: NavHostController = rememberNavController()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToAssets = { navController.navigate("assets") },
                    onNavigateToStatistics = { navController.navigate("statistics") },
                    onNavigateToAi = { navController.navigate("ai") }
                )
            }
            composable("assets") {
                AssetsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("statistics") {
                StatisticsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("ai") {
                AiScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}