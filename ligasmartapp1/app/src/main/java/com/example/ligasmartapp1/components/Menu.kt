package com.example.ligasmartapp1.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.ligasmartapp1.R
import com.example.ligasmartapp1.Routes
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import com.google.firebase.database.FirebaseDatabase

data class User(val name: String, val email: String)

// Función para obtener los datos del usuario
fun getUserData(onResult: (User?) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    if (user != null) {
        // Obtén el nombre y el correo del usuario
        val name = user.displayName ?: "Sin Nombre"
        val email = user.email ?: "Sin Correo"
        onResult(User(name, email))
    } else {
        onResult(null) // No hay usuario autenticado
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuContent(
    menuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    navController: NavController,
//    userId: String?
) {
    var user by remember { mutableStateOf<User?>(null) }

    // Obtener los datos del usuario al inicializar el menú
    if (user == null) {
        getUserData { fetchedUser ->
            user = fetchedUser
        }
    }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { onDismissRequest() },
            modifier = Modifier
                .fillMaxHeight()
                .width(LocalConfiguration.current.screenWidthDp.dp * 0.5f)
                .background(Color.White, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(8.dp)) // Bordes redondeados
        ) {

            // Contenido del menú
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Opciones",
                            tint = Color.Black
                        )
                    }
                },
                onClick = { onDismissRequest() }
            )

            // Aquí el resto de las opciones de menú
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.imageuser),
                    contentDescription = "Administrador",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop // Ajusta la imagen para llenar el círculo
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Mostrar el nombre y el correo del usuario
                user?.let {
                    Text(
                        text = it.name,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center // Alinea el texto en el centro
                    )
                    Text(
                        text = it.email,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } ?: run {
                    // Mostrar un texto de carga si los datos no están disponibles
                    Text(text = "Cargando...", color = Color.Gray, fontSize = 16.sp)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star, // Cambia este ícono por el que desees
                        contentDescription = "Premium Icon",
                        tint = Color(0xFFFFB700),
                        modifier = Modifier.size(14.dp) // Tamaño del ícono
                    )
                    Spacer(modifier = Modifier.width(1.dp)) // Espacio entre el ícono y el texto
                    Text(
                        text = "Premium",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB700),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        navController.navigate(Routes.EditarPerfil.route)
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .height(40.dp)
                        .width(130.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3461FF),
                        contentColor = Color.White
                    )
                ) {
                    Text("Editar Perfil")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            DropdownMenuItem(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Inicio",
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            "Inicio",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                onClick = {
                    navController.navigate(Routes.Home.route)
                    onDismissRequest()
                }
            )
            DropdownMenuItem(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.torneo),
                            contentDescription = "Torneo",
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            "Torneos",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                onClick = {
                    navController.navigate(Routes.Torneo.route)
                    onDismissRequest()
                }
            )
//            DropdownMenuItem(
//                modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
//                text = {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Image(
//                            painter = painterResource(id = R.drawable.notification),
//                            contentDescription = "Notificaciones",
//                            modifier = Modifier.size(26.dp)
//                        )
//                        Spacer(modifier = Modifier.width(15.dp))
//                        Text(
//                            "Notificaciones",
//                            color = Color.Black,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                },
//                onClick = {
//                    /*navController.navigate(Routes.Notificaciones.route)*/
//                    onDismissRequest()
//                }
//            )

            DropdownMenuItem(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 0.dp),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.acercademenu),
                            contentDescription = "Acerca de",
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            "Acerca de la App",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                onClick = {
                    navController.navigate(Routes.AcercaDeApp.route)
                    onDismissRequest()
                }
            )

            DropdownMenuItem(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .background(Color(0xFF5E6F82), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 0.dp, vertical = 0.dp),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Cerrar sesión",
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            "Cerrar Cesión",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                onClick = {
                    navController.navigate(Routes.Login.route)
                    onDismissRequest()
                }
            )
        }
    }


