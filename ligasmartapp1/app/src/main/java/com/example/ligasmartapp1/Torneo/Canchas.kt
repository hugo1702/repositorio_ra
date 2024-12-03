package com.example.ligasmartapp1.Torneo

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.compose.ui.text.style.TextOverflow
import com.example.ligasmartapp1.R
import com.example.ligasmartapp1.components.CargandoAnimacion

data class Cancha(
    val id: String = "",
    val nombre_campo: String = "",
    val direccion: String = "",
    val latitud: String = "",
    val longitud: String = "",
    val fecha_creacion: String = "",
    val torneo_id: String = ""
)

@Composable
fun ListaCanchas(torneoId: String?) {
    var canchas by remember { mutableStateOf<List<Cancha>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(torneoId) {
        if (torneoId == null) {
            error = "ID de torneo no proporcionado"
            isLoading = false
            return@LaunchedEffect
        }

        val database = FirebaseDatabase.getInstance()
        val canchasRef = database.getReference("canchas")

        // Consulta para obtener canchas filtradas por torneo_id
        canchasRef.orderByChild("torneo_id").equalTo(torneoId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val canchasList = mutableListOf<Cancha>()
                    for (canchaSnapshot in snapshot.children) {
                        canchaSnapshot.getValue(Cancha::class.java)?.let { cancha ->
                            canchasList.add(cancha)
                        }
                    }
                    canchas = canchasList
                    isLoading = false
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    error = "Error al cargar las canchas: ${databaseError.message}"
                    isLoading = false
                }
            })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        when {
            isLoading -> {
                CargandoAnimacion()
            }
            error != null -> {
                Text(
                    text = error ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            canchas.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center // Centra todo el contenido en el Box
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally // Alinea el contenido de la columna al centro horizontalmente
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.notfound),
                            contentDescription = "",
                            modifier = Modifier
                                .size(190.dp)
                        )
                        Text(text = "No se encontraron Canchas", fontSize = 20.sp, color = Color.Gray)
                    }

                }// Mostrar mensaje si no se encontraron torneos
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(canchas) { cancha ->
                        CanchaCard(cancha)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanchaCard(cancha: Cancha) {
    val context = LocalContext.current
    Card(onClick = {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.google.com/maps?q=${cancha.latitud},${cancha.longitud}") // Reemplaza 'username' con el usuario de tu página de Facebook
        }
        context.startActivity(intent)
    },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp, horizontal = 0.dp), // Espaciado interno de la tarjeta
        shape = RoundedCornerShape(8.dp), // Forma redondeada
        elevation = CardDefaults.cardElevation(4.dp) // Sombra de la tarjeta
    ) {
        // Tarjeta interna con fondo gris
        Box(
            modifier = Modifier
                .padding(0.dp) // Espaciado interno dentro de la tarjeta
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(), // Ocupa todo el ancho disponible
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre elementos
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFF0F4FC)
                                ), // Degradado de azul claro a un tono más oscuro
                            )
                        )
                        .padding(5.dp), // Espaciado interno
                    verticalAlignment = Alignment.CenterVertically, // Centra verticalmente los elementos
                    horizontalArrangement = Arrangement.SpaceBetween // Espacia los elementos de manera uniforme
                ) {
                    // Imagen del logotipo del torneo
                    Image(
                        painter = painterResource(id = R.drawable.cachav),
                        contentDescription = "Cancha",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp)) // Espacio entre la imagen y el texto

                    // Columna para el nombre y la categoría del equipo
                    Column(
                        modifier = Modifier.weight(1f) // Ocupa el espacio restante entre la imagen y el ícono
                    ) {
                        Text(
                            text = cancha.nombre_campo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = cancha.direccion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            lineHeight = 14.sp,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    // Ícono de Google Maps a la derecha (sin funcionalidad)
                    Image(
                        painter = painterResource(id = R.drawable.maps), // Asegúrate de tener el ícono en tus recursos
                        contentDescription = "Google Maps",
                        modifier = Modifier.size(35.dp),
                    )
                }
            }
        }
    }
}