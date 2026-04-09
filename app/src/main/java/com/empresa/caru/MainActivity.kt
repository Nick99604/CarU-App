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
            contentDescription = "Patron de fondo",
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
                contentDescription = "Cambiar tema",
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
                    contentDescription = "Logo CarU",
                    modifier           = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                )
            }

            Text(
                text       = "CarU",
                fontFamily = CaruTitleFontFamily,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFFF60606),
                fontSize   = 110.sp,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.padding(bottom = 2.dp)
            )

            Text(
                text       = "\"Lo mejor cerca de ti\"",
                fontFamily = CaruFontFamily,
                style      = MaterialTheme.typography.bodyLarge,
                color      = sloganColor,
                textAlign  = TextAlign.Center,
                fontWeight = FontWeight.Normal,
                fontSize   = 25.sp,
                modifier   = Modifier.padding(bottom = 52.dp)
            )

            AppButton(
                text     = "Iniciar sesion",
                onClick  = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                text        = "Crear cuenta",
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
                                onCreateAccountClick = { navController.navigate("register") }, // ← NUEVO
                                innerPadding         = innerPadding,
                                isDarkTheme          = isDarkTheme,
                                onToggleTheme        = { isDarkTheme = !isDarkTheme }
                            )
                        }

                        // Pantalla de recuperación de contraseña
                        composable("forgot_password") {
                            ForgotPasswordScreen(
                                onBackClick = { navController.popBackStack() },
                                onSendClick = { nombre, correo ->
                                    Log.d("Nav", "Recuperar: $nombre | $correo")
                                },
                                isDarkTheme   = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding  = innerPadding
                            )
                        }

                        // Pantalla de inicio de sesión
                        composable("login") {
                            LoginScreen(
                                onBackClick            = { navController.popBackStack() },
                                onLoginClick           = { Log.d("Nav", "Login exitoso") },
                                onForgotPasswordClick  = { navController.navigate("forgot_password") },
                                isDarkTheme            = isDarkTheme,
                                onToggleTheme          = { isDarkTheme = !isDarkTheme },
                                innerPadding           = innerPadding
                            )
                        }

                        // Pantalla de registro de puesto
                        composable("register_station") {
                            RegisterStationScreen(
                                onBackClick = { navController.popBackStack() },
                                onCreateClick = { nombre, nombrePuesto, correo, contrasena ->
                                    Log.d("Nav", "Registrar puesto: $nombre | $nombrePuesto | $correo")
                                },
                                isDarkTheme   = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                innerPadding  = innerPadding
                            )
                        }

                        // Pantalla de selección de rol
                        composable("register") {
                            RegisterScreen(
                                onBackClick      = { navController.popBackStack() },
                                onBuscarClick    = { navController.navigate("create_user_account") },
                                onRegistrarClick = { navController.navigate("register_station") },
                                isDarkTheme      = isDarkTheme,
                                onToggleTheme    = { isDarkTheme = !isDarkTheme },
                                innerPadding     = innerPadding
                            )
                        }

                        // Crear cuenta usuario
                        composable("create_user_account") {
                            CreateUserAccountScreen(
                                onBackClick = { navController.popBackStack() },
                                onCreateClick = { nombre, correo, contrasena ->
                                    // Aquí irá tu lógica real (Firebase o API)
                                    Log.d("Nav", "Crear cuenta: $nombre | $correo")

                                    // Ejemplo futuro:
                                    // navController.navigate("home")
                                },
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
        RegisterStationScreen(
            onBackClick = {},
            onCreateClick = { _, _, _, _ -> },
            isDarkTheme   = false,
            onToggleTheme = {},
            innerPadding  = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "RegisterStation - Modo Oscuro")
@Composable
fun RegisterStationScreenDarkPreview() {
    CarUTheme(darkTheme = true) {
        RegisterStationScreen(
            onBackClick = {},
            onCreateClick = { _, _, _, _ -> },
            isDarkTheme   = true,
            onToggleTheme = {},
            innerPadding  = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "ForgotPassword - Modo Claro")
@Composable
fun ForgotPasswordScreenLightPreview() {
    CarUTheme(darkTheme = false) {
        ForgotPasswordScreen(
            onBackClick = {},
            onSendClick = { _, _ -> },
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
            onSendClick = { _, _ -> },
            isDarkTheme   = true,
            onToggleTheme = {},
            innerPadding  = PaddingValues()
        )
    }
}