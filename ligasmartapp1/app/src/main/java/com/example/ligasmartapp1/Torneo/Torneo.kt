package com.example.ligasmartapp1.Torneo

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.ligasmartapp1.R
import com.example.ligasmartapp1.components.CargandoAnimacion
import com.example.ligasmartapp1.components.DropdownMenuContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Torneo(navController: NavHostController) {
    var menuExpanded by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column {
        TopAppBar(
            title = {
                if (!isSearchExpanded) {
                    Text(
                        text = "Lista de Torneos",
                        color = Color(0xFF232E3C),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFF0F4FC)
            ),
            navigationIcon = {
                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color(0xFF232E3C))
                }
            },
        )

        DropdownMenuContent(
            menuExpanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            navController = navController,
        )
            TorneosScreen(navController)
    }
}


// Clase de datos para representar un Torneo
data class Torneo(
    val age_range_end: String = "",
    val age_range_start: String = "",
    val ciudad: String = "",
    val diasPartido: List<String> = listOf(),
    val direccion: String = "",
    val estado: String = "",
    val fechaFin: String = "",
    val fechaInicio: String = "",
    val horaFin: String = "",
    val horaInicio: String = "",
    val id: String = "",
    val logotipo: String = "",
    val nombre: String = "",
    val t_categoria: String = "",
    val user_id: String = ""
)

@Composable
fun TorneosScreen(navController: NavHostController) {
    // Estado para almacenar los torneos como objetos Kotlin
    var torneosList by remember { mutableStateOf(listOf<Torneo>()) }
    var isLoading by remember { mutableStateOf(true) } // Estado para manejar la carga
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    // Obtener los torneos del usuario
    LaunchedEffect(Unit) {
        uid?.let {
            val database = FirebaseDatabase.getInstance().getReference("torneos")
            database.orderByChild("user_id").equalTo(it).get()
                .addOnSuccessListener { dataSnapshot ->
                    val listaTorneos = mutableListOf<Torneo>()
                    for (torneoSnapshot in dataSnapshot.children) {
                        val torneo = torneoSnapshot.getValue(Torneo::class.java)
                        if (torneo != null) {
                            listaTorneos.add(torneo)
                        }
                    }
                    // Actualizar el estado con la lista de torneos
                    torneosList = listaTorneos
                    isLoading = false // Cambiar el estado de carga
                }
                .addOnFailureListener { exception ->
                    Log.e("TorneosScreen", "Error al obtener torneos: ${exception.message}")
                    isLoading = false // Cambiar el estado de carga incluso si hay un error
                }
        }
    }

    // UI para mostrar la lista de torneos
    Spacer(modifier = Modifier.height(6.dp))
    TorneosUI(torneosList, isLoading, navController)
}

@Composable
fun TorneosUI(torneos: List<Torneo>, isLoading: Boolean, navController: NavHostController) {
    if (isLoading) {
        CargandoAnimacion()
    } else if (torneos.isEmpty()) {
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
                Text(text = "No se encontraron torneos", fontSize = 20.sp, color = Color.Gray)
            }

        }// Mostrar mensaje si no se encontraron torneos
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(torneos) { torneo ->
                TorneoCard(torneo, navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TorneoCard(torneo: Torneo, navController: NavHostController) {
    Card(
        onClick = { navController.navigate("torneonavigation/${torneo.id}")},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp), // Espaciado interno de la tarjeta
        shape = RoundedCornerShape(8.dp), // Forma redondeada
        elevation = CardDefaults.cardElevation(4.dp) // Sombra de la tarjeta)

    ) {
        // Tarjeta interna con fondo gris
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White, Color(0xFFF0F4FC)), // Degradado de azul claro a un tono más oscuro
                    ))
                .padding(0.dp) // Espaciado interno dentro de la tarjeta
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(), // Ocupa todo el ancho disponible
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre elementos
            ) {
                Row(
                    modifier = Modifier

                        .padding(8.dp), // Espaciado interno
                    verticalAlignment = Alignment.CenterVertically // Centra verticalmente los elementos
                ) {
                    // Imagen del logotipo del torneo
                    Image(
                        painter = rememberImagePainter(data = torneo.logotipo),
                        contentDescription = "Logotipo del Torneo",
                        modifier = Modifier
                            .size(60.dp) // Tamaño del logotipo
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFD0D4DC), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(20.dp)) // Espacio entre la imagen y el texto

                    // Columna para el nombre y la categoría del equipo
                    Column {
                        Text(
                            text = torneo.nombre,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Categoria: ")
                                }
                                append(torneo.t_categoria)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF5E6F82),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}