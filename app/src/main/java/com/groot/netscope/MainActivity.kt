package com.groot.netscope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.groot.netscope.ui.RequestDetailScreen
import com.groot.netscope.ui.RequestListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "request_list"
                    ) {
                        composable("request_list") {
                            RequestListScreen(
                                onRequestClick = { requestId ->
                                    navController.navigate("request_detail/$requestId")
                                }
                            )
                        }
                        composable("request_detail/{requestId}") { backStackEntry ->
                            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
                            RequestDetailScreen(
                                requestId = requestId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}