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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import com.example.ligasmartapp1.R


// Modelo de datos
data class Partido(
    var id: String = "",
    val direccion: String = "",
    val equipo_local: String = "",
    val equipo_visitante: String = "",
    val fecha: String = "",
    val hora: String = "",
    var numero_jornada: Any = "",
    val torneo_id: String = "",
    var nombre_equipo_local: String = "",
    var logotipo_local: String = "",
    var nombre_equipo_visitante: String = "",
    var logotipo_visitante: String = "",
    var goles_local: Int = 0,
    var goles_visitante: Int = 0,
    var comentarios: String = "",
    var jugadores_local: Map<String, JugadorEstadisticas> = emptyMap(),
    var jugadores_visitante: Map<String, JugadorEstadisticas> = emptyMap(),
    var nombres_jugadores_local: Map<String, String> = emptyMap(),
    var nombres_jugadores_visitante: Map<String, String> = emptyMap()
)

data class JugadorEstadisticas(
    val amonestacion: String = "0",
    val asistencia: String = "0",
    val autogol: String = "0",
    val goles: String = "0"
)

class PartidosViewModel : ViewModel() {
    private val _partidosPorJornada = MutableStateFlow<Map<String, List<Partido>>>(emptyMap())
    val partidosPorJornada: StateFlow<Map<String, List<Partido>>> = _partidosPorJornada.asStateFlow()

    // Contador para rastrear las operaciones pendientes
    private var pendingOperations = 0
    private val partidos = mutableListOf<Partido>()

    @Synchronized
    private fun incrementPendingOperations() {
        pendingOperations++
    }

    @Synchronized
    private fun decrementPendingOperations() {
        pendingOperations--
        if (pendingOperations == 0) {
            actualizarPartidosPorJornada(partidos)
        }
    }

    fun cargarPartidos(torneoId: String) {
        val database = Firebase.database
        val partidosRef = database.getReference("partidos")
        val equiposRef = database.getReference("equipos")
        val resultadosRef = database.getReference("resultados")
        val jugadoresRef = database.getReference("jugadores")

        Log.d("PartidosViewModel", "Cargando partidos para torneoId: $torneoId")

        partidosRef.orderByChild("torneo_id")
            .equalTo(torneoId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    partidos.clear()
                    pendingOperations = 0

                    snapshot.children.forEach { partidoSnapshot ->
                        val partido = partidoSnapshot.getValue(Partido::class.java)
                        partido?.let {
                            it.id = partidoSnapshot.key ?: ""
                            partidos.add(it)

                            // Incrementar operaciones pendientes para cada carga
                            incrementPendingOperations() // Para equipo local
                            incrementPendingOperations() // Para equipo visitante
                            incrementPendingOperations() // Para resultados

                            cargarDetallesEquipo(equiposRef, it)
                            cargarResultados(resultadosRef, it, jugadoresRef)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error: ${error.message}")
                }
            })
    }

    private fun cargarDetallesEquipo(
        equiposRef: DatabaseReference,
        partido: Partido
    ) {
        equiposRef.child(partido.equipo_local).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(localSnapshot: DataSnapshot) {
                partido.nombre_equipo_local = localSnapshot.child("nombre_equipo").value as? String ?: ""
                partido.logotipo_local = localSnapshot.child("logotipo").value as? String ?: ""
                decrementPendingOperations()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
                decrementPendingOperations()
            }
        })

        equiposRef.child(partido.equipo_visitante).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(visitanteSnapshot: DataSnapshot) {
                partido.nombre_equipo_visitante = visitanteSnapshot.child("nombre_equipo").value as? String ?: ""
                partido.logotipo_visitante = visitanteSnapshot.child("logotipo").value as? String ?: ""
                decrementPendingOperations()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
                decrementPendingOperations()
            }
        })
    }

    private fun cargarResultados(
        resultadosRef: DatabaseReference,
        partido: Partido,
        jugadoresRef: DatabaseReference
    ) {
        resultadosRef.child(partido.id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                partido.goles_local = (snapshot.child("goles_local").value as? Long)?.toInt() ?: 0
                partido.goles_visitante = (snapshot.child("goles_visitante").value as? Long)?.toInt() ?: 0
                partido.comentarios = snapshot.child("comentarios").value as? String ?: ""

                // Incrementar operaciones pendientes para cada jugador
                snapshot.child("jugadores_local").children.forEach { _ ->
                    incrementPendingOperations()
                }
                snapshot.child("jugadores_visitante").children.forEach { _ ->
                    incrementPendingOperations()
                }

                // Cargar estadísticas y nombres de jugadores locales
                val jugadoresLocalMap = mutableMapOf<String, JugadorEstadisticas>()
                val nombresLocalMap = mutableMapOf<String, String>()
                snapshot.child("jugadores_local").children.forEach { jugadorSnapshot ->
                    val jugadorId = jugadorSnapshot.key ?: return@forEach
                    val jugadorStats = jugadorSnapshot.getValue(JugadorEstadisticas::class.java)
                    jugadorStats?.let { jugadoresLocalMap[jugadorId] = it }
                    cargarNombreJugador(jugadoresRef, jugadorId) { nombre ->
                        nombresLocalMap[jugadorId] = nombre
                        partido.nombres_jugadores_local = nombresLocalMap.toMap()
                        decrementPendingOperations()
                    }
                }
                partido.jugadores_local = jugadoresLocalMap

                // Cargar estadísticas y nombres de jugadores visitantes
                val jugadoresVisitanteMap = mutableMapOf<String, JugadorEstadisticas>()
                val nombresVisitanteMap = mutableMapOf<String, String>()
                snapshot.child("jugadores_visitante").children.forEach { jugadorSnapshot ->
                    val jugadorId = jugadorSnapshot.key ?: return@forEach
                    val jugadorStats = jugadorSnapshot.getValue(JugadorEstadisticas::class.java)
                    jugadorStats?.let { jugadoresVisitanteMap[jugadorId] = it }
                    cargarNombreJugador(jugadoresRef, jugadorId) { nombre ->
                        nombresVisitanteMap[jugadorId] = nombre
                        partido.nombres_jugadores_visitante = nombresVisitanteMap.toMap()
                        decrementPendingOperations()
                    }
                }
                partido.jugadores_visitante = jugadoresVisitanteMap

                decrementPendingOperations() // Para la carga inicial de resultados
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error cargando resultados: ${error.message}")
                decrementPendingOperations()
            }
        })
    }

    private fun cargarNombreJugador(jugadoresRef: DatabaseReference, jugadorId: String, onComplete: (String) -> Unit) {
        jugadoresRef.child(jugadorId).child("nombre_completo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombre = snapshot.value as? String ?: "Desconocido"
                    onComplete(nombre)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error al cargar nombre de jugador: ${error.message}")
                    onComplete("Desconocido")
                }
            })
    }

    private fun actualizarPartidosPorJornada(listaPartidos: List<Partido>) {
        Log.d("PartidosViewModel", "Actualizando partidos por jornada. Partidos: ${listaPartidos.size}")
        _partidosPorJornada.value = listaPartidos.groupBy { it.numero_jornada.toString() }
    }
}

// Composables
@Composable
fun PartidosScreen(
    torneoId: String,
    viewModel: PartidosViewModel = viewModel()
) {
    val partidosPorJornada = viewModel.partidosPorJornada.collectAsState()

    // Lanzar la carga de partidos solo una vez
    remember {
        viewModel.cargarPartidos(torneoId)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        partidosPorJornada.value.forEach { (jornada, partidos) ->
            item {
                Text(
                    text = "Jornada $jornada",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E4F62),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
            items(partidos) { partido ->
                PartidoItem(partido = partido)
            }
        }
    }
}

// PartidoItem Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartidoItem(partido: Partido) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { showDialog = true }
    ) {
        Box(modifier = Modifier.background(Color(0xFFF0F4FC))) {
            Column(
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AsyncImage(
                        model = partido.logotipo_local,
                        contentDescription = "Logotipo local",
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = partido.nombre_equipo_local,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = partido.goles_local.toString(),
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5E6F82),
                            fontSize = 18.sp
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "Símbolo plus",
                        modifier = Modifier
                            .size(35.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = partido.nombre_equipo_visitante,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = partido.goles_visitante.toString(),
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5E6F82),
                            fontSize = 18.sp
                        )
                    }

                    AsyncImage(
                        model = partido.logotipo_visitante,
                        contentDescription = "Logotipo visitante",
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.reloj),
                        contentDescription = "reloj",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hora: ${partido.hora} - Fecha: ${partido.fecha}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ubicacion),
                        contentDescription = "ubicacion",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = partido.direccion,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis

                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false), // Deshabilita el ancho predeterminado
            title = {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = "Estadísticas del Partido",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                LazyColumn() {
                    item {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding()
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column() {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF3E4F62))
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = partido.logotipo_local,
                                        contentDescription = "Logo local",
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = partido.nombre_equipo_local,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.weight(0.1f)) // Empuja el texto de goles hacia la derecha
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp) // Tamaño del círculo
                                            .clip(CircleShape) // Hace que el fondo sea circular
                                            .background(Color.White), // Color de fondo blanco
                                        contentAlignment = Alignment.Center // Centra el texto dentro del círculo
                                    ) {
                                        Text(
                                            text = partido.goles_local.toString(), // Muestra los goles del equipo visitante
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3E4F62), // Cambia el color del texto para que sea visible sobre el fondo blanco
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.width(157.dp)) {

                                    }
                                    Column(Modifier.width(33.dp)) {
                                        Text(text = "xG.", fontWeight = FontWeight.Bold,)
                                    }
                                    Column(Modifier.width(39.dp)) {
                                        Text(text = "Asis.", fontWeight = FontWeight.Bold,)
                                    }
                                    Column(Modifier.width(50.dp)) {
                                        Text(text = "Amon.", fontWeight = FontWeight.Bold,)
                                    }
                                    Column(Modifier.width(40.dp)) {
                                        Text(text = "AG.", fontWeight = FontWeight.Bold,)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                partido.jugadores_local.forEach { (id, stats) ->
                                    val nombreJugador =
                                        partido.nombres_jugadores_local[id] ?: "Desconocido"
                                    JugadorEstadisticasItem(
                                        nombreJugador = nombreJugador,
                                        stats = stats
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(5.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column() {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF3E4F62))
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = partido.logotipo_visitante,
                                        contentDescription = "Logo visitante",
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = partido.nombre_equipo_visitante,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 22.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.weight(0.1f)) // Empuja el texto de goles hacia la derecha
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp) // Tamaño del círculo
                                            .clip(CircleShape) // Hace que el fondo sea circular
                                            .background(Color.White), // Color de fondo blanco
                                        contentAlignment = Alignment.Center // Centra el texto dentro del círculo
                                    ) {
                                        Text(
                                            text = partido.goles_visitante.toString(), // Muestra los goles del equipo visitante
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF3E4F62), // Cambia el color del texto para que sea visible sobre el fondo blanco
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                                //copiar
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.width(157.dp)) {

                                    }
                                    Column(Modifier.width(33.dp)) {
                                        Text(text = "xG.", fontWeight = FontWeight.Bold,)
                                    }
                                    Column(Modifier.width(39.dp)) {
                                        Text(text = "Asis.", fontWeight = FontWeight.Bold,)
                                    }
                                    Column(Modifier.width(50.dp)) {
                                        Text(text = "Amon.", fontWeight = FontWeight.Bold,)
                                    }
                                    Column(Modifier.width(40.dp)) {
                                        Text(text = "AG.", fontWeight = FontWeight.Bold,)
                                    }

                                    Spacer(modifier = Modifier.height(0.dp))
                                }
                                partido.jugadores_visitante.forEach { (id, stats) ->
                                    val nombreJugador =
                                        partido.nombres_jugadores_visitante[id] ?: "Desconocido"
                                    JugadorEstadisticasItem(
                                        nombreJugador = nombreJugador,
                                        stats = stats
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp)
                                .border(
                                    1.dp,
                                    Color.LightGray,
                                    RoundedCornerShape(8.dp)
                                ) // Borde redondeado de color gris claro
                                .padding(16.dp) // Padding interno del Box
                        ) {
                            Column {
                                Text(
                                    text = "Comentarios del partido:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )

                                Text(
                                    text = partido.comentarios,
                                    fontSize = 14.sp,
                                    color = Color(0xFF5E6F82)
                                )
                            }

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
                    Text("Cerrar", color = Color.White)
                }
            },

            )
    }
}

@Composable
fun JugadorEstadisticasItem(
    nombreJugador: String,
    stats: JugadorEstadisticas
) {
    Column(
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column (modifier = Modifier.width(115.dp), horizontalAlignment = Alignment.Start,){

                Text(
                    text = nombreJugador,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(modifier = Modifier.width(20.dp)) {
                EstadisticaItem(label = "Goles", valor = stats.goles)
            }
            Row(modifier = Modifier.width(20.dp)){
                EstadisticaItem(label = "Asistencias.", valor = stats.asistencia)
            }
            Row(modifier = Modifier.width(20.dp)){
                EstadisticaItem(label = "Amonestaciones.", valor = stats.amonestacion)
            }
            Row(modifier = Modifier.width(20.dp)){
                EstadisticaItem(label = "Autogoles", valor = stats.autogol)
            }
        }
    }
}

@Composable
fun EstadisticaItem(
    label: String,
    valor: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}