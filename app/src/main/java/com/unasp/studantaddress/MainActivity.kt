package com.unasp.studantaddress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unasp.studantaddress.ui.add.AddStudentScreen
import com.unasp.studantaddress.ui.list.StudentListScreen
import com.unasp.studantaddress.ui.theme.StudantAddressTheme
import com.unasp.studantaddress.worker.SyncWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SyncWorker.schedulePeriodic(this)

        SyncWorker.syncNow(this)

        enableEdgeToEdge()
        setContent {
            StudantAddressTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "student_list"
    ) {
        composable("student_list") {
            StudentListScreen(
                onNavigateToAdd = { navController.navigate("add_student") }
            )
        }
        composable("add_student") {
            AddStudentScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}