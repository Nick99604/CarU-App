package com.empresa.caru

import android.os.Bundle
import android.util.Log
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
import com.empresa.caru.domain.repository.AuthRepository
import com.empresa.caru.data.repository.AuthRepositoryImpl
import com.empresa.caru.ui.theme.CarUTheme


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

            CarUTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController    = navController,
                        startDestination = "start"
                    ) {
                        // Pantalla de inicio
                        composable("start") {
                            CarUAppStartScreen(
                                onLoginClick         = { navController.navigate("login") },
                                onCreateAccountClick = { navController.navigate("register") },
                                innerPadding         = innerPadding,
                                isDarkTheme          = isDarkTheme,
                                onToggleTheme        = { isDarkTheme = !isDarkTheme }
                            )
                        }

                        // Pantalla de enlace enviado
                        composable("reset_email_sent") {
                            ResetEmailSentScreen(
                                onBackToLoginClick = {
                                    navController.navigate("login") {
                                        popUpTo("start") { inclusive = false }
                                    }
                                },
                                isDarkTheme   = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding  = innerPadding
                            )
                        }

                        // Pantalla de recuperación de contraseña
                        composable("forgot_password") {
                            ForgotPasswordScreen(
                                onBackClick = { navController.popBackStack() },
                                onSuccess   = { navController.navigate("reset_email_sent") },
                                authRepository = authRepository,
                                isDarkTheme   = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding  = innerPadding
                            )
                        }

                        // Pantalla de inicio de sesión
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

                        // Pantalla de registro de puesto (Menú principal)
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
                                    navController.navigate("home") {
                                        popUpTo("start") { inclusive = false }
                                    }
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        // Pantalla: Tipo de comida
                        composable("food_type") {
                            FoodTypeScreen(
                                viewModel = registrationViewModel,
                                onBackClick = { navController.popBackStack() },
                                onContinueClick = { navController.navigate("station_info") },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        // Pantalla: Info del puesto
                        composable("station_info") {
                            StationInfoScreen(
                                viewModel = registrationViewModel,
                                onBackClick = { navController.popBackStack() },
                                onContinueClick = { navController.navigate("location_selection") },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        // Pantalla: Selección de ubicación
                        composable("location_selection") {
                            LocationSelectionScreen(
                                viewModel = registrationViewModel,
                                onBackClick = { navController.popBackStack() },
                                onContinueClick = { navController.navigate("schedule") },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        // Pantalla: Horario
                        composable("schedule") {
                            ScheduleScreen(
                                viewModel = registrationViewModel,
                                onBackClick = { navController.popBackStack() },
                                onContinueClick = { navController.navigate("image_upload") },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding = innerPadding
                            )
                        }

                        // Pantalla: Subir imagen
                        composable("image_upload") {
                            ImageUploadScreen(
                                viewModel = registrationViewModel,
                                onBackClick = { navController.popBackStack() },
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

                        // Pantalla de selección de rol
                        composable("register") {
                            RegisterScreen(
                                onBackClick      = { navController.popBackStack() },
                                onBuscarClick    = { navController.navigate("create_user_account") },
                                onRegistrarClick = { navController.navigate("register_station_user") },
                                isDarkTheme      = isDarkTheme,
                                onToggleTheme    = { isDarkTheme = !isDarkTheme },
                                innerPadding     = innerPadding
                            )
                        }

                        // Pantalla: Registrar puesto (usuario vendor)
                        composable("register_station_user") {
                            RegisterStationUserScreen(
                                onBackClick = { navController.popBackStack() },
                                onSuccess   = {
                                    navController.navigate("register_station") {
                                        popUpTo("register_station_user") { inclusive = true }
                                    }
                                },
                                isDarkTheme   = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding  = innerPadding
                            )
                        }

                        // Pantalla principal (home)
                        composable("home") {
                            HomeScreen(
                                isDarkTheme   = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding  = innerPadding
                            )
                        }

                        // Crear cuenta usuario
                        composable("create_user_account") {
                            CreateUserAccountScreen(
                                onBackClick = { navController.popBackStack() },
                                onSuccess   = {
                                    navController.navigate("home") {
                                        popUpTo("start") { inclusive = false }
                                    }
                                },
                                authRepository = authRepository,
                                isDarkTheme   = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding  = innerPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true, name = "Inicio - Modo Claro")
@Composable
fun CarUAppStartScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        CarUAppStartScreen(
            onLoginClick         = {},
            onCreateAccountClick = {},
            innerPadding         = PaddingValues(),
            isDarkTheme          = false,
            onToggleTheme        = {}
        )
    }
}

@Preview(showBackground = true, name = "Inicio - Modo Oscuro")
@Composable
fun CarUAppStartScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        CarUAppStartScreen(
            onLoginClick         = {},
            onCreateAccountClick = {},
            innerPadding         = PaddingValues(),
            isDarkTheme          = true,
            onToggleTheme        = {}
        )
    }
}

@Preview(showBackground = true, name = "Register - Modo Claro")
@Composable
fun RegisterScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        RegisterScreen(
            onBackClick      = {},
            onBuscarClick    = {},
            onRegistrarClick = {},
            isDarkTheme      = false,
            onToggleTheme    = {},
            innerPadding     = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "Register - Modo Oscuro")
@Composable
fun RegisterScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        RegisterScreen(
            onBackClick      = {},
            onBuscarClick    = {},
            onRegistrarClick = {},
            isDarkTheme      = true,
            onToggleTheme    = {},
            innerPadding     = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "RegisterStation - Modo Claro")
@Composable
fun RegisterStationScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        RegistrationStationScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onFoodTypeClick = {},
            onInfoClick = {},
            onLocationClick = {},
            onScheduleClick = {},
            onImageClick = {},
            onSuccess = {},
            isDarkTheme = false,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "RegisterStation - Modo Oscuro")
@Composable
fun RegisterStationScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        RegistrationStationScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onFoodTypeClick = {},
            onInfoClick = {},
            onLocationClick = {},
            onScheduleClick = {},
            onImageClick = {},
            onSuccess = {},
            isDarkTheme = true,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "ForgotPassword - Modo Claro")
@Composable
fun ForgotPasswordScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        ForgotPasswordScreen(
            onBackClick = {},
            onSuccess = {},
            authRepository = AuthRepositoryImpl(),
            isDarkTheme   = false,
            onToggleTheme = {},
            innerPadding  = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "ForgotPassword - Modo Oscuro")
@Composable
fun ForgotPasswordScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        ForgotPasswordScreen(
            onBackClick = {},
            onSuccess = {},
            authRepository = AuthRepositoryImpl(),
            isDarkTheme   = true,
            onToggleTheme = {},
            innerPadding  = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "ResetEmailSent - Modo Claro")
@Composable
fun ResetEmailSentScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        ResetEmailSentScreen(
            onBackToLoginClick = {},
            isDarkTheme   = false,
            onToggleTheme = {},
            innerPadding  = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "ResetEmailSent - Modo Oscuro")
@Composable
fun ResetEmailSentScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        ResetEmailSentScreen(
            onBackToLoginClick = {},
            isDarkTheme   = true,
            onToggleTheme = {},
            innerPadding  = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "FoodType - Modo Claro")
@Composable
fun FoodTypeScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        FoodTypeScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onContinueClick = {},
            isDarkTheme = false,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "FoodType - Modo Oscuro")
@Composable
fun FoodTypeScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        FoodTypeScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onContinueClick = {},
            isDarkTheme = true,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "StationInfo - Modo Claro")
@Composable
fun StationInfoScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        StationInfoScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onContinueClick = {},
            isDarkTheme = false,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "StationInfo - Modo Oscuro")
@Composable
fun StationInfoScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        StationInfoScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onContinueClick = {},
            isDarkTheme = true,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "LocationSelection - Modo Claro")
@Composable
fun LocationSelectionScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        LocationSelectionScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onContinueClick = {},
            isDarkTheme = false,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "LocationSelection - Modo Oscuro")
@Composable
fun LocationSelectionScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        LocationSelectionScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onContinueClick = {},
            isDarkTheme = true,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "Schedule - Modo Claro")
@Composable
fun ScheduleScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        ScheduleScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onContinueClick = {},
            isDarkTheme = false,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "Schedule - Modo Oscuro")
@Composable
fun ScheduleScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        ScheduleScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onContinueClick = {},
            isDarkTheme = true,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "ImageUpload - Modo Claro")
@Composable
fun ImageUploadScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        ImageUploadScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onSaveClick = {},
            isDarkTheme = false,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "ImageUpload - Modo Oscuro")
@Composable
fun ImageUploadScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        ImageUploadScreen(
            viewModel = RegistrationViewModel(),
            onBackClick = {},
            onSaveClick = {},
            isDarkTheme = true,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "RegisterStationUser - Modo Claro")
@Composable
fun RegisterStationUserScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        RegisterStationUserScreen(
            onBackClick = {},
            onSuccess = {},
            isDarkTheme = false,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "RegisterStationUser - Modo Oscuro")
@Composable
fun RegisterStationUserScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        RegisterStationUserScreen(
            onBackClick = {},
            onSuccess = {},
            isDarkTheme = true,
            onToggleTheme = {},
            innerPadding = PaddingValues()
        )
    }
}
