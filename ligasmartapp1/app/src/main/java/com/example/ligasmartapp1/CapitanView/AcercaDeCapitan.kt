package com.example.ligasmartapp1.CapitanView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ligasmartapp1.components.DropdownMenuContent
import com.example.ligasmartapp1.components.DropdownMenuContentCapitan
import com.example.ligasmartapp1.infoAcercade

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcercaDeAppCapitan(navController: NavHostController) {
    var menuExpanded by remember { mutableStateOf(false) }
    Column(

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                androidx.compose.material3.Text(
                    text = "Acerca de la App",
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
                    Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.Gray)
                }
            },
        )
        // Llama al DropdownMenuContent
        DropdownMenuContentCapitan(
            menuExpanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            navController = navController
        )
        infoAcercade()
    }
}
