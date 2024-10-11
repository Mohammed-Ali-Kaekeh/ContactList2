package com.example.contactlistproject.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.contactlistproject.Screens.MainPage
import com.example.contactlistproject.Screens.AddContact
import com.example.contactlistproject.Screens.DetailsScreen
import com.example.contactlistproject.contactList // Import the contact list

@Composable
fun Nav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "MainPage") {
        composable(route = "MainPage") {
            MainPage(modifier = Modifier, navController = navController, contacts = contactList)
        }
        composable(route = "AddContact") {
            AddContact(navController)
        }

        composable(
            route = "DetailsScreen/{name}/{phoneNO}/{address}/{email}/{imageUri}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("phoneNO") { type = NavType.StringType },
                navArgument("address") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType },
                navArgument("imageUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val phoneNO = backStackEntry.arguments?.getString("phoneNO") ?: ""
            val address = backStackEntry.arguments?.getString("address") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val imageUri = backStackEntry.arguments?.getString("imageUri")?.let { Uri.decode(it) }?.let { Uri.parse(it) }


            DetailsScreen(
                name = name,
                phoneNO = phoneNO,
                address = address,
                email = email,
                imageUri = imageUri,
                onBack = { navController.popBackStack() } // Go back to MainPage
            )
        }
    }
}
