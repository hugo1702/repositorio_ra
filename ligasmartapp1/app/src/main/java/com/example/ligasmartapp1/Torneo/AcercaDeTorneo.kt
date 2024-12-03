package com.example.ligasmartapp1.Torneo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.database.*



data class TorneoA(
    val id: String = "",
    val nombre: String = "",
    val age_range_start: String = "",
    val age_range_end: String = "",
    val ciudad: String = "",
    val descripcion: String = "",
    val direccion: String = "",
    val estado: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val logotipo: String = "",
    val status: String = "",
    val t_categoria: String = "",
    val user_id: String = "",
    val diasPartido: List<String> = listOf() // Nueva lista de días
)
@Composable
fun AcercaDeVista(torneoId: String?) {
    var torneo by remember { mutableStateOf<TorneoA?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(torneoId) {
        if (torneoId == null) {
            error = "ID de torneo no proporcionado"
            isLoading = false
            return@LaunchedEffect
        }

        val database = FirebaseDatabase.getInstance()
        val torneoRef = database.getReference("torneos").child(torneoId)

        torneoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    torneo = snapshot.getValue(TorneoA::class.java)
                    isLoading = false
                } else {
                    error = "Torneo no encontrado"
                    isLoading = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                error = "Error al cargar el torneo: ${databaseError.message}"
                isLoading = false
            }
        })
    }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            ) // Aplica la sombra con la elevación y forma redondeada
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .fillMaxWidth() // Ocupa todo el ancho disponible
            .wrapContentHeight() // Ajusta la altura al contenido
            .padding(0.dp)
    ) {
        when {
            isLoading -> {
            }
            error != null -> {
                Text(
                    text = error ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            torneo != null -> {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(bottom = 26.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()

                            .background(Color(0xFF3E4F62))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically // Alinea verticalmente el contenido en el centro
                    ) {
                        // Imagen
                        AsyncImage(
                            model = torneo?.logotipo,
                            contentDescription = "Logo del torneo",
                            modifier = Modifier
                                .size(70.dp) // Ajusta el tamaño de la imagen
                                .clip(CircleShape)
                                .border(1.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(8.dp)) // Añade espacio entre la imagen y el texto

                        // Texto
                        Text(
                            text = torneo?.nombre ?: "",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp)) // Añade espacio entre la imagen y el texto
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)){
                        Text(
                            text = "Información del torneo",
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp)) // Añade espacio entre la imagen y el texto
                    // Detalles del torneo
                    InfoItem("Estado: ", torneo?.status ?: "")
                    InfoItem("Descripción del torneo: ", torneo?.descripcion ?: "")
                    InfoItem("Categoría: ", torneo?.t_categoria ?: "")
                    InfoItem("Fecha de inicio a fin: ", "${torneo?.fechaInicio}  -  ${torneo?.fechaFin}")
                    InfoItem("Ubicación (Municipio y Estado): ", "${torneo?.ciudad}, ${torneo?.estado}")
                    InfoItem("Dirección: ", torneo?.direccion ?: "")
                    InfoItem("Días de Partido: ", torneo?.diasPartido?.joinToString(", ") ?: "No especificado")
                    InfoItem("Horario: ", "${torneo?.horaInicio} a ${torneo?.horaFin}")
                    InfoItem("Rango de edad: ", "${torneo?.age_range_start} - ${torneo?.age_range_end} años")
                }
            }
        }
    }
}


@Composable
private fun InfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF3E4F62)
        )
        Text(
            text = value,
            lineHeight = 18.sp,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = Color(0xFFF0F4FC))
    }
}