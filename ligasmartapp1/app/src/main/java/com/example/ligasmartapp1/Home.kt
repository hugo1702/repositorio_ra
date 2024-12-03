package com.example.ligasmartapp1

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ligasmartapp1.components.DropdownMenuContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = "Aprende a Usar la App",
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
        DropdownMenuContent(
            menuExpanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            navController = navController,
        )

        CardYoutube()
        CardPage()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardYoutube(){

    val context = LocalContext.current
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Accion para abrir el navegador con el enlace
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/channel/UCw4NZ9PxmpERq64sIAt1TBQ")
                )
                context.startActivity(intent)
            }
            .padding(horizontal = 8.dp, vertical = 4.dp), // Espaciado interno de la tarjeta
        shape = RoundedCornerShape(8.dp), // Forma redondeada
        elevation = CardDefaults.cardElevation(4.dp) // Sombra de la tarjeta
    ) {
        // Tarjeta interna con fondo gris
        Box(
            modifier = Modifier
                .background(Color(0xFFF0F4FC)) // Color de fondo de la tarjeta interna
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically  // Alineación vertical de los elementos
            ) {
                // Imagen (logo de YouTube) a la izquierda
                Image(
                    painter = painterResource(id = R.drawable.youtubelogo), // Tu logo de YouTube
                    contentDescription = "Torneo",
                    modifier = Modifier
                        .size(90.dp)  // Tamaño del logotipo
                        .padding(end = 12.dp),  // Añade padding a la derecha
                    contentScale = ContentScale.Fit
                )

                // Texto a la derecha
                Text(
                    text = "Video Tutoriales\n" +
                            "\n" +
                            "¡Aprende a usar LigaSmart App con nuestros videos tutoriales de YouTube! Suscríbete y no te pierdas ninguna actualización.",
                    fontSize = 16.sp,
                    lineHeight = 20.sp, // Ajusta este valor según sea necesario
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)  // Ocupa el espacio disponible en la fila
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPage(){

    val context = LocalContext.current
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Accion para abrir el navegador con el enlace
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://ligasmartapp.com/")
                )
                context.startActivity(intent)
            }
            .padding(horizontal = 8.dp, vertical = 4.dp), // Espaciado interno de la tarjeta
        shape = RoundedCornerShape(8.dp), // Forma redondeada
        elevation = CardDefaults.cardElevation(4.dp) // Sombra de la tarjeta
    ) {
        // Tarjeta interna con fondo gris
        Box(
            modifier = Modifier
                .background(Color(0xFFF0F4FC)) // Color de fondo de la tarjeta interna
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically  // Alineación vertical de los elementos
            ) {
                // Imagen (logo de YouTube) a la izquierda
                Image(
                    painter = painterResource(id = R.drawable.logo), // Tu logo de YouTube
                    contentDescription = "Torneo",
                    modifier = Modifier
                        .size(90.dp)  // Tamaño del logotipo
                        .padding(end = 12.dp),  // Añade padding a la derecha
                    contentScale = ContentScale.Fit
                )

                // Texto a la derecha
                Text(
                    text = "Visita nuestra página web\n" + "\n" +
                            "¡Crea torneos, ligas, equipos y jugadores fácilmente con LigaSmart App! Visita nuestra página web para descubrir cómo aprovechar al máximo todas nuestras funcionalidades.",
                    fontSize = 16.sp,
                    lineHeight = 20.sp, // Ajusta este valor según sea necesario
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)  // Ocupa el espacio disponible en la fila
                )

            }
        }
    }
}