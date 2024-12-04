package com.example.ligasmartapp1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ligasmartapp1.CapitanView.*
import com.example.ligasmartapp1.Torneo.Torneo
import com.example.ligasmartapp1.Torneo.TorneoNavigation
import com.example.ligasmartapp1.data.Notificaciones.NotificacionResultados
import com.example.ligasmartapp1.data.Notificaciones.Notificaciones
import com.example.ligasmartapp1.ui.theme.Ligasmartapp1Theme
import com.example.ligasmartapp1.data.UserPreferencesStore
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Error initializing Firebase", e)
            Toast.makeText(this, "Error al inicializar Firebase. Por favor, inténtelo más tarde.", Toast.LENGTH_LONG).show()
            finish()
        }

        // Solicitar permisos de notificación para Android 13 y superior
        requestNotificationPermission()

        // Inicializar el UID del usuario y las notificaciones
        val auth = FirebaseAuth.getInstance()
        val userPreferencesStore = UserPreferencesStore(this)
        val context = this

        CoroutineScope(Dispatchers.Main).launch {
            val userUid = auth.currentUser?.uid ?: userPreferencesStore.getUserUid()
            userUid?.let { uid ->
                // Crear canales de notificaciones
                val notificaciones = Notificaciones(context = context, userUid = uid)
                notificaciones.createNotificationChannel()

                val resultadosNotificaciones = NotificacionResultados(context = context, userUid = uid)
                resultadosNotificaciones.createResultadosNotificationChannel()

                // Escuchar cambios en resultados y partidos
                withContext(Dispatchers.IO) {
                    notificaciones.startPartidosListener()
                    resultadosNotificaciones.startResultadosListener()
                }
            }
        }

        setContent {
            Ligasmartapp1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationController = rememberNavController()
                    NavHost(navController = navigationController, startDestination = Routes.SplashScreen.route) {
                        composable(Routes.SplashScreen.route) { SplashScreen(navController = navigationController) }
                        composable(Routes.Login.route) {
                            val activity = LocalContext.current as ComponentActivity
                            Login(navController = navigationController, activity = activity)
                        }
                        composable(Routes.Torneo.route) { Torneo(navController = navigationController) }
                        composable(Routes.TorneoNavigation.route) { backStackEntry ->
                            val torneoId = backStackEntry.arguments?.getString("torneoId")
                            TorneoNavigation(navController = navigationController, torneoId = torneoId)
                        }
                        composable(Routes.EditarPerfil.route) { EditarPefil(navController = navigationController) }
                        composable(Routes.Home.route) { Home(navController = navigationController) }
                        composable(Routes.TerminosCondiciones.route) { TerminosCondiciones(navController = navigationController) }
                        composable(Routes.AcercaDeApp.route) { AcercaDeApp(navController = navigationController) }

                        // Capitan views
                        composable(Routes.HomeCapitan.route) { HomeCapitan(navController = navigationController) }
                        composable(Routes.AcercaDeAppCapitan.route) { AcercaDeAppCapitan(navController = navigationController) }
                        composable(Routes.JugadoresCapitan.route) { JugadoresCapitan(navController = navigationController) }
                        composable(Routes.CrearJugadorFormCapitan.route) { backStackEntry ->
                            val equipoId = backStackEntry.arguments?.getString("equipoId")
                            CrearJugadorFormCapitan(navigationController, equipoId = equipoId)
                        }
                        composable(Routes.EditarPerfilCapitan.route) { EditarPefilCapitan(navController = navigationController) }
                        composable(Routes.EstadisiticaslCapitan.route) { EstadisiticasCapitan(navController = navigationController) }
                    }
                }
            }
        }
    }

    // Función para solicitar el permiso de notificaciones
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }
}
