package com.example.ligasmartapp1.CapitanView

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ligasmartapp1.Torneo.CrearJugadorForm
import com.example.ligasmartapp1.components.DropdownMenuContentCapitan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearJugadorFormCapitan(navController: NavHostController, equipoId: String?) {
    var nombre by remember { mutableStateOf("") }
    var lugarnac by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var estatura by remember { mutableStateOf("") }
    var curp by remember { mutableStateOf("") }
    var numerocamiseta by remember { mutableStateOf("") }
    var correoelectronico by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TopAppBar en la parte superior
        TopAppBar(
            title = {
                Text(
                    text = "Crear Jugador",
                    color = Color(0xFF232E3C),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFF0F4FC)
            ),
            navigationIcon = {
                androidx.compose.material3.IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.Gray)
                }
            },
        )

        // Dropdown menu
        DropdownMenuContentCapitan(
            menuExpanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            navController = navController
        )

        // Contenedor para el formulario
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp) // Añade un pequeño espacio después del TopAppBar
        ) {
            CrearJugadorForm(navController, equipoId)
        }
    }
}