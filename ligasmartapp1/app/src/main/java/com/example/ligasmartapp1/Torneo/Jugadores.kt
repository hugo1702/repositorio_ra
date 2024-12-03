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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.ligasmartapp1.R
import com.example.ligasmartapp1.components.CargandoAnimacion
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Jugadores(
    val id: String = "",
    val curp: String = "",
    val email: String = "",
    val equipo_id: String = "",
    val estatura: String = "",
    val fecha_nacimiento: String = "",
    val foto: String = "",
    val lugar_nacimiento: String = "",
    val nombre_completo: String = "",
    val numero_camiseta: String = "",
    val peso: String = ""
)
@Composable
fun JugadorEjemplo (equipoId: String?){
    var jugadores by remember { mutableStateOf<List<Jugadores>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(equipoId) {
        val database = FirebaseDatabase.getInstance()
        val jugadoresRef = database.getReference("jugadores")

        jugadoresRef.orderByChild("equipo_id").equalTo(equipoId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val jugadoresList = mutableListOf<Jugadores>()
                    for (jugadorSnapshot in snapshot.children) {
                        val jugador = jugadorSnapshot.getValue(Jugadores::class.java)
                        jugador?.let {
                            jugadoresList.add(it.copy(id = jugadorSnapshot.key ?: ""))
                        }
                    }
                    jugadores = jugadoresList
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when {
            isLoading -> {
                CargandoAnimacion()
            }
            jugadores.isEmpty() -> {
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
                        Text(text = "No hay jugadores registrados", fontSize = 20.sp, color = Color.Gray)
                    }

                }// Mostrar mensaje si no se encontraron torneos
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    items(jugadores) { jugador ->
                        JugadorCard(jugador)
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorCard(jugador: Jugadores) {
    var showDetalles by remember { mutableStateOf(false) }

    Card(
        onClick = { showDetalles = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF0F4FC)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberImagePainter(data = jugador.foto),
                        contentDescription = "Foto del Jugador",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFF0F4FC), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = jugador.nombre_completo,
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
                                    append("Email: ")
                                }
                                append(jugador.email)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF5E6F82),
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    if (showDetalles) {
        DetallesJugadorDialog(
            jugador = jugador,
            onDismiss = { showDetalles = false }
        )
    }
}
@Composable
fun DetallesJugadorDialog(
    jugador: Jugadores,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false // Esto permite que el diálogo sea más ancho
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 26.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Encabezado con imagen y nombre
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberImagePainter(
                            data = jugador.foto,
                        ),
                        contentDescription = "Foto del Jugador",
                        modifier = Modifier
                            .size(height = 145.dp, width = 120.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = jugador.nombre_completo,
                            style = MaterialTheme.typography.titleMedium, fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                        Divider(
                            modifier = Modifier.padding(vertical = 1.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                        Text(
                            text = "No. de jugador: ${jugador.numero_camiseta}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Divider(
                            modifier = Modifier.padding(vertical = 1.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }

                }


                // Información personal
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        DetalleItem("Email", jugador.email)
                        DetalleItem("CURP", jugador.curp)
                        DetalleItem("Fecha de Nacimiento", jugador.fecha_nacimiento)
                        DetalleItem("Lugar de Nacimiento", jugador.lugar_nacimiento)
                        DetalleItem(
                            "Estatura",
                            "${jugador.estatura} Metros",
                        )
                        DetalleItem(
                            "Peso",
                            "${jugador.peso} Kilogramos",
                        )
                    }
                }

                // Botón cerrar
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(top = 4.dp)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
private fun DetalleItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


