package com.example.ligasmartapp1

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ligasmartapp1.components.DropdownMenuContent
import com.example.ligasmartapp1.components.DropdownMenuContentCapitan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcercaDeApp(navController: NavHostController) {
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
        DropdownMenuContent(
            menuExpanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            navController = navController
        )
        infoAcercade()
    }
}


@Composable
fun infoAcercade (){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White))
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally // Centrar contenido horizontalmente
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Asegúrate de tener una imagen llamada logo.png en res/drawable
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Text(
                text = "LigaSmart App",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold

            )
            Text(text = "Versión: 1.0")
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Organiza torneos sin esfuerzo!",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.h6, color = Color(0xFF3D4855),
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center // Asegúrate de centrar el texto
            )
            // Description
            Text(
                text = "Para organizadores que desean tener una agradable experiencia al gestionar sus eventos deportivos!",
                style = MaterialTheme.typography.body1, color = Color(0xFF3D4855),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            // Basic functionalities section
            Text(
                text = "Funcionalidades básicas",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.h5, color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center // Asegúrate de centrar el texto
            )

            // Calendar feature
            FeatureCard(
                title = "Calendario",
                description = "Mantén a todos al día con nuestro calendario interactivo. Programa partidos, gestiona fechas clave y asegura que ningún encuentro se pierda. ¡Organizar torneos nunca fue tan fácil!",
                imageRes = R.drawable.calendar // Reemplaza con tu recurso de imagen
            )

            // Statistics feature
            FeatureCard(
                title = "Estadísticas",
                description = "Conoce el rendimiento de cada equipo y jugador con nuestras estadísticas avanzadas. Desde goles anotados hasta tarjetas recibidas, toda la información clave a un solo clic para mejorar la estrategia de tu equipo.",
                imageRes = R.drawable.graph // Reemplaza con tu recurso de imagen
            )

            // Results feature
            FeatureCard(
                title = "Resultados",
                description = "Mantén a todos al día con nuestros resultados actualizados. Asegúrate de que todos estén informados sobre el progreso del torneo.",
                imageRes = R.drawable.medal // Reemplaza con tu recurso de imagen
            )
            Spacer(modifier = Modifier.height(30.dp))
            ContactButtons()
        }
    }
}
@Composable
fun FeatureCard(title: String, description: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color(0xFFF0F4FC), shape = RoundedCornerShape(4.dp))
                .padding(16.dp) ,
            horizontalAlignment = Alignment.CenterHorizontally // Centrar contenido horizontalmente
        ) {
            // Feature image placeholder
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 8.dp)
            )

            // Feature title
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.h6, color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp),
                textAlign = TextAlign.Center // Centrar el texto
            )

            // Feature description
            Text(
                text = description,
                style = MaterialTheme.typography.body1, color = Color(0xFF3D4855),
                textAlign = TextAlign.Center // Centrar el texto
            )
        }
    }
}

@Composable
fun ContactButtons() {
    val context = LocalContext.current
    Text(
        text = "Ponte en contacto con nosotros a través de:",
        style = MaterialTheme.typography.body1, color = Color(0xFF5E6F82),
        textAlign = TextAlign.Center // Centrar el texto
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally // Centra los íconos y el texto
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:wws10integ@gmail.com")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .size(60.dp) // Tamaño total del botón circular
                    .shadow(8.dp, CircleShape)
                    .background(Color.White)
                    .padding(3.dp)
                    .clip(CircleShape) // Forma circular
                    .background(Color(0xFFF14336)) // Color de fondo del botón

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.gmail),
                    contentDescription = "Contactar por correo",
                    tint = Color.White, // Color del ícono
                    modifier = Modifier.size(40.dp) // Tamaño del ícono
                )
            }
            Text(
                text = "Correo",
                fontSize = 10.sp, // Tamaño del texto
                color = Color(0xFF5E6F82), // Color del texto
                modifier = Modifier.padding(top = 4.dp) // Espacio entre el ícono y el texto
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:9191524467")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .size(60.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0090B8))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.phone),
                    contentDescription = "Contactar por teléfono",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "Teléfono",
                fontSize = 10.sp,
                color = Color(0xFF5E6F82),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/123456789")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .size(60.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4DCB5B))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.watsapp),
                    contentDescription = "Contactar por WhatsApp",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "WhatsApp",
                fontSize = 10.sp,
                color = Color(0xFF5E6F82),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://m.me/username") // Reemplaza 'username' con el usuario de tu página de Facebook
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .size(60.dp)
                    .shadow(8.dp, CircleShape)
                    .background(Color.White)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0866FF)) // Color de Facebook Messenger
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Contactar por Facebook Messenger",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Text(
                text = "Facebook",
                fontSize = 10.sp,
                color = Color(0xFF5E6F82),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "© 2024 Web Wiz Studio. Todos los derechos reservados. \n Creado por: Web Wiz Studio.",
        fontSize = 10.sp,
        color = Color(0xFF5E6F82),
        modifier = Modifier.padding(top = 4.dp),
        textAlign = TextAlign.Center
    )

}
