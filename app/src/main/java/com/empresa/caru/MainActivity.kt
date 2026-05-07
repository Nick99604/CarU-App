package com.empresa.caru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.data.repository.AuthRepositoryImpl
import com.empresa.caru.ui.theme.CarUTheme
import com.google.firebase.auth.FirebaseAuth


// ── Botón reutilizable ───────────────────────────────────────────────────────
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = RedButtonColor,
    textColor: Color = Color.White
) {
    Button(
        onClick   = onClick,
        modifier  = modifier
            .height(60.dp)
            .fillMaxWidth(),
        shape     = RoundedCornerShape(50.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor   = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text       = text,
            fontFamily = CaruFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize   = 30.sp
        )
    }
}

// ── Pantalla de inicio ───────────────────────────────────────────────────────
@Composable
fun CarUAppStartScreen(
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    innerPadding: PaddingValues,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val backgroundColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)
    val sloganColor     = if (isDarkTheme) Color(0xFFAAAAAA)  else TextColorSecondary

    Box(
        modifier        = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
       Image(
            painter            = painterResource(id = R.drawable.background_food_pattern),
            contentDescription = stringResource(R.string.background_pattern_description),
            modifier           = Modifier.matchParentSize(),
            contentScale       = ContentScale.Crop,
            alpha              = if (isDarkTheme) 0.08f else 0.9f
        )

        IconButton(
            onClick  = onToggleTheme,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0))
        ) {
            Icon(
                imageVector        = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = stringResource(R.string.change_theme_description),
                tint               = if (isDarkTheme) Color(0xFFFFD700) else Color(0xFF333333),
                modifier           = Modifier.size(22.dp)
            )
        }

        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .size(300.dp)
                    .padding(bottom = 1.dp)
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.logo_caru),
                    contentDescription = stringResource(R.string.logo_caru_description),
                    modifier           = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                )
            }

            Text(
                text       = stringResource(R.string.app_name),
                fontFamily = CaruTitleFontFamily,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFFF60606),
                fontSize   = 110.sp,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.padding(bottom = 2.dp)
            )

            Text(
                text       = stringResource(R.string.app_slogan),
                fontFamily = CaruFontFamily,
                style      = MaterialTheme.typography.bodyLarge,
                color      = sloganColor,
                textAlign  = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                fontSize   = 25.sp,
                modifier   = Modifier.padding(bottom = 52.dp)
            )

            AppButton(
                text     = stringResource(R.string.login_button),
                onClick  = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                text        = stringResource(R.string.create_account_button),
                onClick     = onCreateAccountClick,
                modifier    = Modifier.fillMaxWidth(),
                buttonColor = RedButtonColor
            )
        }
    }
}

// ── Actividad principal ──────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val navController = rememberNavController()
            val authRepository: AuthRepository = remember { AuthRepositoryImpl() }
            val registrationViewModel: RegistrationViewModel = remember { RegistrationViewModel() }
            val favoritesViewModel: FavoritesViewModel = remember { FavoritesViewModel() }
            val onboardingViewModel: OnboardingViewModel = remember { OnboardingViewModel() }
            val homeViewModel: HomeViewModel = remember { HomeViewModel() }
            val savedStationsViewModel: SavedStationsViewModel = remember { SavedStationsViewModel() }
            val profileViewModel: ProfileViewModel = remember { ProfileViewModel() }


            CarUTheme(darkTheme = isDarkTheme) {
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
                                    if (registrationViewModel.isAllCompleted) {
                                        registrationViewModel.saveStation { success ->
                                            if (success) {
                                                homeViewModel.refresh()
                                                navController.navigate("home") {
                                                    popUpTo("start") { inclusive = false }
                                                }
                                            }
                                        }
                                    } else {
                                        navController.popBackStack("register_station", inclusive = false)
                                    }
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        // Pantalla de Detalle ACTUALIZADA para recibir ID
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
                                onSettingsClick = { navController.navigate("settings") },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }
                        
                        // Rutas secundarias (favoritos, perfil, etc.)
                        composable("favorites") { FavoritesScreen(favoritesViewModel, { navController.popBackStack() }, { id -> navController.navigate("station_detail/$id") }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        
                        composable("saved_stations") { 
                            val state by savedStationsViewModel.uiState.collectAsState()
                            SavedStationsScreen(
                                stations = state.savedStations,
                                isLoading = state.isLoading,
                                onStationClick = { id -> navController.navigate("station_detail/$id") },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            ) 
                        }

                        composable("profile") { ProfileScreen({ navController.popBackStack() }, profileViewModel, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        composable("register") { RegisterScreen({ navController.popBackStack() }, { navController.navigate("create_user_account") }, { navController.navigate("register_station_user") }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        composable("register_station_user") { RegisterStationUserScreen({ navController.popBackStack() }, { registrationViewModel.reset(); navController.navigate("register_station") { popUpTo("register_station_user") { inclusive = true } } }, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                        composable("create_user_account") { CreateUserAccountScreen({ navController.popBackStack() }, { navController.navigate("onboarding_profile_image") { popUpTo("start") { inclusive = false } } }, authRepository, isDarkTheme, { isDarkTheme = !isDarkTheme }, innerPadding) }
                    }
                }
            }
        }
    }
}
