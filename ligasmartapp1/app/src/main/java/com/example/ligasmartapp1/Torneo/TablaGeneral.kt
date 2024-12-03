package com.example.ligasmartapp1.Torneo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ligasmartapp1.R
import com.google.firebase.database.*


data class Team(
    val id: String = "",
    val nombre_equipo: String = "",
    val logotipo: String = "",
    val torneo_id: String = ""
)

data class Match(
    val id: String = "",
    val torneo_id: String = "",
    val equipo_local: String = "",
    val equipo_visitante: String = ""
)

data class MatchResult(
    val goles_local: Int = 0,
    val goles_visitante: Int = 0
)

data class TeamStanding(
    val equipo: String = "",
    val logotipo: String = "",
    var JJ: Int = 0,
    var JG: Int = 0,
    var JE: Int = 0,
    var JP: Int = 0,
    var GA: Int = 0,
    var GR: Int = 0,
    var GDif: Int = 0,
    var P: Int = 0
)

class StandingsViewModel {
    private val database = FirebaseDatabase.getInstance().reference
    var standings = mutableStateListOf<TeamStanding>()
        private set
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun loadStandings(tournamentId: String?) {
        if (tournamentId == null) {
            error = "ID del torneo no válido"
            return
        }

        isLoading = true
        error = null

        database.child("equipos")
            .orderByChild("torneo_id")
            .equalTo(tournamentId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        error = "No se encontraron equipos para este torneo"
                        isLoading = false
                        return
                    }

                    val standingsMap = mutableMapOf<String, TeamStanding>()

                    for (teamSnapshot in snapshot.children) {
                        val team = teamSnapshot.getValue(Team::class.java)
                        team?.let {
                            standingsMap[teamSnapshot.key!!] = TeamStanding(
                                equipo = it.nombre_equipo,
                                logotipo = it.logotipo
                            )
                        }
                    }

                    database.child("partidos")
                        .orderByChild("torneo_id")
                        .equalTo(tournamentId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(matchesSnapshot: DataSnapshot) {
                                for (matchSnapshot in matchesSnapshot.children) {
                                    val match = matchSnapshot.getValue(Match::class.java)
                                    match?.let { processMatch(it, matchSnapshot.key!!, standingsMap) }
                                }

                                // Ordenamiento mejorado con múltiples criterios
                                val sortedStandings = standingsMap.values.sortedWith(
                                    compareByDescending<TeamStanding> { it.P }  // Primero por puntos
                                        .thenByDescending { it.GDif }  // Luego por diferencia de goles
                                        .thenByDescending { it.GA }    // Luego por goles a favor
                                        .thenByDescending { it.JG }    // Luego por juegos ganados
                                        .thenBy { it.equipo }         // Finalmente por nombre de equipo (alfabético)
                                )

                                standings.clear()
                                standings.addAll(sortedStandings)
                                isLoading = false
                            }

                            override fun onCancelled(error: DatabaseError) {
                                this@StandingsViewModel.error = "Error al cargar los partidos: ${error.message}"
                                isLoading = false
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    this@StandingsViewModel.error = "Error al cargar los equipos: ${error.message}"
                    isLoading = false
                }
            })
    }

    private fun processMatch(match: Match, matchId: String, standingsMap: MutableMap<String, TeamStanding>) {
        database.child("resultados").child(matchId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(resultSnapshot: DataSnapshot) {
                    val result = resultSnapshot.getValue(MatchResult::class.java)
                    result?.let {
                        standingsMap[match.equipo_local]?.let { localStanding ->
                            updateTeamStats(localStanding, it.goles_local, it.goles_visitante)
                        }

                        standingsMap[match.equipo_visitante]?.let { visitorStanding ->
                            updateTeamStats(visitorStanding, it.goles_visitante, it.goles_local)
                        }

                        // Reordenar la tabla después de cada actualización
                        val sortedStandings = standingsMap.values.sortedWith(
                            compareByDescending<TeamStanding> { it.P }
                                .thenByDescending { it.GDif }
                                .thenByDescending { it.GA }
                                .thenByDescending { it.JG }
                                .thenBy { it.equipo }
                        )

                        standings.clear()
                        standings.addAll(sortedStandings)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    this@StandingsViewModel.error = "Error al cargar los resultados: ${error.message}"
                }
            })
    }

    private fun updateTeamStats(standing: TeamStanding, golesAFavor: Int, golesEnContra: Int) {
        standing.JJ++
        standing.GA += golesAFavor
        standing.GR += golesEnContra
        standing.GDif = standing.GA - standing.GR

        when {
            golesAFavor > golesEnContra -> {
                standing.JG++
                standing.P += 3
            }
            golesAFavor == golesEnContra -> {
                standing.JE++
                standing.P += 1
            }
            else -> standing.JP++
        }
    }
}
@Composable
fun TablaGeneral(navController: NavController, torneoId: String?) {
    val viewModel = remember { StandingsViewModel() }
    val standings = viewModel.standings
    val isLoading by remember { mutableStateOf(viewModel.isLoading) }
    val error by remember { mutableStateOf(viewModel.error) }
    val horizontalScrollState = rememberScrollState()

    LaunchedEffect(torneoId) {
        viewModel.loadStandings(torneoId)
    }

    Box(modifier = Modifier.padding(8.dp) .fillMaxHeight() )  {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }
            error != null -> {
                Text(
                    text = error ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            standings.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center // Centra todo el contenido en el Box
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally // Alinea el contenido de la columna al centro horizontalmente
                    ) {

                        Text(text = "No se encontraron datos disponibles", fontSize = 20.sp, color = Color.Gray)
                    }

                }// Mostrar mensaje si no se encontraron torneos
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header con scroll horizontal
                    Row(
                        modifier = Modifier
                            .horizontalScroll(horizontalScrollState)
                            .background(Color(0xFF5E6F82))
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text("Pos", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Equipo", modifier = Modifier.width(220.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Pts", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("JJ", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("JG", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("JE", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("JP", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("GF", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("GR", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("GDIF", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, color = Color.White)
                    }

                    // Contenido de la tabla con scroll horizontal sincronizado
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        itemsIndexed(standings) { index, standing ->
                            Row(
                                modifier = Modifier.background(Color(0xFFF0F4FC))
                                    .horizontalScroll(horizontalScrollState)
                                    .padding(vertical = 6.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${(index + 1).toString()}°",
                                    modifier = Modifier.width(40.dp),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(16.dp))

                                Row(
                                    modifier = Modifier.width(220.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AsyncImage(
                                        model = standing.logotipo,
                                        contentDescription = "Logo ${standing.equipo}",
                                        modifier = Modifier
                                            .background(Color.White, CircleShape)
                                            .size(55.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 8.dp),
                                        text = standing.equipo,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = standing.P.toString(), textAlign = TextAlign.Center,  modifier = Modifier.width(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = standing.JJ.toString(), textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = standing.JG.toString(), textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = standing.JE.toString(), textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = standing.JP.toString(), textAlign = TextAlign.Center,  modifier = Modifier.width(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = standing.GA.toString(), textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = standing.GR.toString(), textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = standing.GDif.toString(), textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))

                            }
                            Divider(color = Color.White)
                        }
                    }
                }
            }
        }
    }
}