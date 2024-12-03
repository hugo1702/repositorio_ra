package com.example.ligasmartapp1.Torneo

import JugadorEjemplo
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Vibrator
import android.os.VibrationEffect
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.icons.filled.Add
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.ligasmartapp1.R
import com.example.ligasmartapp1.components.DropdownMenuContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorneoNavigation(navController: NavHostController, torneoId: String?) {
    var menuExpanded by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("Torneo Dasboard") }

    // Función para cambiar el título
    fun cambiarTitulo(nuevoTitulo: String) {
        titulo = nuevoTitulo
    }

    Column {
        TopAppBar(
            title = {
                androidx.compose.material3.Text(
                    text = titulo,
                    color = Color(0xFF232E3C),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFF0F4FC)
            ),
            navigationIcon = {
                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    androidx.compose.material3.Icon(
                        Icons.Filled.Menu,
                        contentDescription = "Menu",
                        tint = Color(0xFF232E3C)
                    )
                }
            },
        )
        DropdownMenuContent(
            menuExpanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            navController = navController
        )
        EstadisticasButtom(torneoId)
    }
}

@Composable
fun EstadisticasButtom(torneoId: String?) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "acercade/{torneoId}",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("acercade/{torneoId}") { AcercaDe(torneoId) }

            composable("calendario/{torneoId}") { Calendario(navController, torneoId) }
            composable("equipos/{torneoId}") { Equipos(navController, torneoId) }
            composable("estadisticas/{torneoId}") { Estadisiticas(navController, torneoId) }
            composable("canchas/{torneoId}") { Canchas(torneoId) }
            composable(
                route = "jugadores/{equipoId}",
                arguments = listOf(
                    navArgument("equipoId") {
                        type = NavType.StringType // Sigue siendo un String
                    }
                )
            ) { backStackEntry ->
                val equipoId = backStackEntry.arguments?.getString("equipoId") ?: "" // Valor por defecto es un String vacío
                JugadoresExample(navController = navController, equipoId = equipoId) // Asegúrate de que este argumento sea un String
            }

            composable(
                route = "crearjugador/{equipoId}",
                arguments = listOf(
                    navArgument("equipoId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val equipoId = backStackEntry.arguments?.getString("equipoId") ?: "" // Valor por defecto es un String vacío
                CrearJugador(navController = navController, equipoId = equipoId)
            }
            //composable("crearjugador") { CrearJugador(navController) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.AcercaDe,
        BottomNavItem.Calendario,
        BottomNavItem.Equipos,
        BottomNavItem.Estadisticas,
        BottomNavItem.Canchas
    )
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    BottomNavigation(
        modifier = Modifier.height(55.dp),
        backgroundColor = Color(0xFF3E4F62),
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(26.dp),
                        tint = if (isSelected) Color.White else Color.White // Cambiar color según selección
                    )
                },
                label = {
                    Text(
                        item.title,
                        color = if (isSelected) Color.White else Color.White, // Cambiar color según selección
                        style = TextStyle(fontSize = 10.sp)
                    )
                },
                selected = isSelected,
                onClick = {
                    // Hacer vibrar al pulsar el botón
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        vibrator.vibrate(100)
                    }

                    // Navegar y eliminar la vista anterior del backstack
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true  // Esto elimina la vista anterior del backstack
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White
            )
        }
    }
}

sealed class BottomNavItem(var title: String, var icon: Int, var route: String) {
    object AcercaDe : BottomNavItem("Información", R.drawable.acercade, "acercade/{torneoId}")
    object Calendario : BottomNavItem("Calendario", R.drawable.calendario, "calendario/{torneoId}")
    object Canchas : BottomNavItem("Canchas", R.drawable.cancha, "canchas/{torneoId}")
    object Equipos : BottomNavItem("Equipos", R.drawable.equipob, "equipos/{torneoId}")
    object Estadisticas : BottomNavItem("Estadísticas", R.drawable.estadisticasb, "estadisticas/{torneoId}")
}

@Composable
fun AcercaDe(torneoId: String?) {
    AcercaDeVista(torneoId)
}
@Composable
fun JugadoresExample(
    navController: NavController,
    equipoId: String?
) {
    Box(
        modifier = Modifier
            .fillMaxSize() // Llenar toda la pantalla
            .padding(0.dp)
    ) {
        JugadorEjemplo(equipoId)
        FloatingActionButton(
            onClick = { navController.navigate("crearjugador/${equipoId}")},
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd) // Alinear en la parte inferior derecha
                .padding(16.dp), // Margen para separarlo del borde
            backgroundColor = Color(0xFF5E6F82), // Color de fondo del botón flotante
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp, // Elevación por defecto
                pressedElevation = 12.dp  // Elevación al presionar
            )
        ) {
            Icon(modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.Add, // Ícono de "Agregar"
                contentDescription = "Agregar Jugador",
                tint = Color.White // Color del ícono
            )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendario(navController: NavHostController, torneoId: String?) {
    PartidosScreen(torneoId.toString())
}


@Composable
fun Equipos(navController: NavHostController, torneoId: String?) {
    ListaEquipos(navController, torneoId)
}
@Composable
fun Estadisiticas(navController: NavHostController, torneoId: String?) {
    TablaGeneral(navController, torneoId)
}
@Composable
fun CrearJugador(navController: NavHostController, equipoId: String?) {
    CrearJugadorForm(navController, equipoId)
}

@Composable
fun Canchas(torneoId: String?) {
    ListaCanchas(torneoId)
}
