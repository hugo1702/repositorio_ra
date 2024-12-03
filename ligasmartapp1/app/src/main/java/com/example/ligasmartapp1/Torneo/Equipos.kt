package com.example.ligasmartapp1.Torneo
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.ligasmartapp1.R
import com.example.ligasmartapp1.components.CargandoAnimacion
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Equipos(
    val id: String = "",
    val correo: String = "",
    val grupo: String = "",
    val logotipo: String = "",
    val nombre_equipo: String = "",
    val representante: String = "",
    val telefono: String = "",
    val torneo_id: String = ""
)

@Composable
fun ListaEquipos(navController: NavHostController, torneoId: String?) {
    var equipos by remember { mutableStateOf<List<Equipos>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(torneoId) {
        if (torneoId == null) {
            error = "ID de torneo no proporcionado"
            isLoading = false
            return@LaunchedEffect
        }

        val database = FirebaseDatabase.getInstance()
        val equiposRef = database.getReference("equipos")

        // Consulta para obtener equipos filtrados por torneo_id
        equiposRef.orderByChild("torneo_id").equalTo(torneoId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val equiposList = mutableListOf<Equipos>()
                    for (equipoSnapshot in snapshot.children) {
                        val equipo = equipoSnapshot.getValue(Equipos::class.java)
                        equipo?.let {
                            // Asignar el ID del documento al objeto Equipos
                            equiposList.add(it.copy(id = equipoSnapshot.key ?: ""))
                        }
                    }
                    equipos = equiposList
                    isLoading = false
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    error = "Error al cargar los equipos: ${databaseError.message}"
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
            equipos.isEmpty() -> {
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
                        Text(text = "No hay equipos registrados para este torneo", fontSize = 20.sp, color = Color.Gray)
                    }

                }// Mostrar mensaje si no se encontraron torneos
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(equipos) { equipo ->
                        EquipoCard(navController, equipo)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun EquipoCard(navController: NavHostController, equipo: Equipos) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Dialog content
    if (showDialog) {
        AlertDialog(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
            ),
            onDismissRequest = { showDialog = false },
            title = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 0.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        AsyncImage(
                            model = equipo.logotipo,
                            contentDescription = "Logotipo del equipo",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = equipo.nombre_equipo,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E3A59)
                            )
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        color = Color(0xFFE8EFF5)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Grupo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .background(
                                color = Color(0xFFF8FAFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = "Grupo",
                            tint = Color(0xFF3D4855),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Grupo: ${equipo.grupo}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF2E3A59),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    // Representante
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .background(
                                color = Color(0xFFF8FAFF),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Representante",
                            tint = Color(0xFF3D4855),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Admin: ${equipo.representante}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF2E3A59),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    // Teléfono
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${equipo.telefono}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .background(
                                color = Color(0xFFE8F5FF),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF0090B8)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Teléfono",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Tel: ${equipo.telefono}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    // Correo
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${equipo.correo}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .background(
                                color = Color(0xFFFFEFED),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFFF14336)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Correo",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Email: ${equipo.correo}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.Underline
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false },
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 8.dp)
                        .background(Color(0xFF3E4F62), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        "Cerrar",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )

    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp, horizontal = 0.dp)
            .combinedClickable(
                onClick = { navController.navigate("jugadores/${equipo.id}") },
                onLongClick = { showDialog = true }
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // El contenido existente de la Card se mantiene igual
        Box(
            modifier = Modifier.padding(0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White, Color(0xFFF0F4FC)),
                            ))
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AsyncImage(
                        model = equipo.logotipo,
                        contentDescription = "Logotipo del equipo",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFF0F4FC), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = equipo.nombre_equipo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF3D4855))) {
                                    append("Administrador: ")
                                }
                                append(equipo.representante)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.width(24.dp)
                    ) {
                        Text(
                            text = equipo.grupo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3D4855),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}