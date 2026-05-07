package com.empresa.caru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.empresa.caru.data.repository.AuthRepositoryImpl
import com.empresa.caru.ui.theme.CarUTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            
            CarUTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                
                // ViewModels
                val authRepository = remember { AuthRepositoryImpl() }
                val registrationViewModel: RegistrationViewModel = viewModel()
                val homeViewModel: HomeViewModel = viewModel()
                val favoritesViewModel: FavoritesViewModel = viewModel()
                val savedStationsViewModel: SavedStationsViewModel = viewModel()
                val onboardingViewModel: OnboardingViewModel = viewModel()
                val profileViewModel: ProfileViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController    = navController,
                        startDestination = "start"
                    ) {
                        composable("start") {
                            CarUAppStartScreen(
                                onLoginClick         = { navController.navigate("login") },
                                onCreateAccountClick = { navController.navigate("register") },
                                innerPadding         = innerPadding,
                                isDarkTheme          = isDarkTheme,
                                onToggleTheme        = { isDarkTheme = !isDarkTheme }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                onBackClick            = { navController.popBackStack() },
                                onLoginSuccess         = {
                                    navController.navigate("home") {
                                        popUpTo("start") { inclusive = false }
                                    }
                                },
                                onForgotPasswordClick  = { navController.navigate("forgot_password") },
                                authRepository         = authRepository,
                                isDarkTheme            = isDarkTheme,
                                onToggleTheme          = { isDarkTheme = !isDarkTheme },
                                innerPadding           = innerPadding
                            )
                        }

                        composable("register_station") {
                            RegistrationStationScreen(
                                viewModel = registrationViewModel,
                                onBackClick = { navController.popBackStack() },
                                onFoodTypeClick = { navController.navigate("food_type") },
                                onInfoClick = { navController.navigate("station_info") },
                                onLocationClick = { navController.navigate("location_selection") },
                                onScheduleClick = { navController.navigate("schedule") },
                                onImageClick = { navController.navigate("image_upload") },
                                onSuccess = {
                                    homeViewModel.refresh()
                                    registrationViewModel.reset()
                                    navController.navigate("home") {
                                        popUpTo("start") { inclusive = false }
                                    }
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        composable("food_type") { FoodTypeScreen(registrationViewModel, { navController.popBackStack() }, { navController.navigate("station_info") }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        composable("station_info") { StationInfoScreen(registrationViewModel, { navController.popBackStack() }, { navController.navigate("location_selection") }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        composable("location_selection") { LocationSelectionScreen(registrationViewModel, { navController.popBackStack() }, { navController.navigate("schedule") }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        composable("schedule") { ScheduleScreen(registrationViewModel, { navController.popBackStack() }, { navController.navigate("image_upload") }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        
                        composable("image_upload") {
                            ImageUploadScreen(
                                viewModel = registrationViewModel,
                                onBackClick = { navController.popBackStack() },
                                onSaveClick = {
                                    navController.popBackStack("register_station", inclusive = false)
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        composable(
                            route = "station_detail/{stationId}",
                            arguments = listOf(navArgument("stationId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val stationId = backStackEntry.arguments?.getString("stationId")
                            StationDetailScreen(
                                stationId = stationId,
                                viewModel = registrationViewModel,
                                favoritesViewModel = favoritesViewModel,
                                savedStationsViewModel = savedStationsViewModel,
                                onBackClick = { navController.popBackStack() },
                                onEditClick = { navController.navigate("register_station") },
                                onDeleteConfirm = {
                                    registrationViewModel.deleteStation {
                                        homeViewModel.refresh()
                                        navController.navigate("home") {
                                            popUpTo("start") { inclusive = false }
                                        }
                                    }
                                },
                                onSaveClick = {
                                    navController.navigate("home") {
                                        popUpTo("start") { inclusive = false }
                                    }
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        composable(route = "home") {
                            HomeScreen(
                                viewModel = homeViewModel,
                                favoritesViewModel = favoritesViewModel,
                                savedStationsViewModel = savedStationsViewModel,
                                onStationClick = { stationId ->
                                    navController.navigate("station_detail/$stationId")
                                },
                                onProfileClick = { navController.navigate("profile") },
                                onFavoritesClick = { navController.navigate("favorites") },
                                onSavedStationsClick = { navController.navigate("saved_stations") },
                                onMyStationsClick = { navController.navigate("my_stations") },
                                onCreateStationClick = {
                                    registrationViewModel.reset()
                                    navController.navigate("register_station")
                                },
                                onSettingsClick = { navController.navigate("settings") },
                                onLogoutClick = {
                                    scope.launch {
                                        authRepository.logout()
                                        navController.navigate("start") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }
                        
                        composable("my_stations") {
                            MyStationsScreen(
                                viewModel = homeViewModel,
                                onBackClick = { navController.popBackStack() },
                                onCreateStationClick = {
                                    registrationViewModel.reset()
                                    navController.navigate("register_station")
                                },
                                onStationClick = { station ->
                                    navController.navigate("station_detail/${station.id}")
                                },
                                isDarkTheme = isDarkTheme,
                                innerPadding = innerPadding
                            )
                        }
                        
                        composable("favorites") { FavoritesScreen(favoritesViewModel, { navController.popBackStack() }, { id -> navController.navigate("station_detail/$id") }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        
                        composable("saved_stations") { 
                            val state by savedStationsViewModel.uiState.collectAsState()
                            SavedStationsScreen(
                                stations = state.savedStations,
                                isLoading = state.isLoading,
                                onBackClick = { navController.popBackStack() },
                                onStationClick = { id -> navController.navigate("station_detail/$id") },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            ) 
                        }

                        composable("profile") { ProfileScreen({ navController.popBackStack() }, profileViewModel, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        
                        composable("settings") {
                            SettingsScreen(
                                onBackClick = { navController.popBackStack() },
                                onChangePasswordClick = { },
                                onDeleteStationClick = {
                                    registrationViewModel.deleteMyStation { success ->
                                        if (success) {
                                            homeViewModel.refresh()
                                            navController.navigate("start") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                    }
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        composable("register") { RegisterScreen({ navController.popBackStack() }, { navController.navigate("create_user_account") }, { navController.navigate("register_station_user") }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        composable("register_station_user") { RegisterStationUserScreen({ navController.popBackStack() }, { registrationViewModel.reset(); navController.navigate("register_station") { popUpTo("register_station_user") { inclusive = true } } }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        composable("create_user_account") { CreateUserAccountScreen({ navController.popBackStack() }, { navController.navigate("onboarding_profile_image") { popUpTo("start") { inclusive = false } } }, authRepository, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        
                        composable("onboarding_profile_image") {
                            CreateProfileImageScreen(
                                viewModel = onboardingViewModel,
                                onBackClick = { navController.popBackStack() },
                                onContinueClick = { navController.navigate("onboarding_interests") },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }
                    }
                }
            }
        }
    }
}
