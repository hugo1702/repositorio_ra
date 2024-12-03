package com.example.ligasmartapp1.CapitanView

import JugadorEjemplo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.ligasmartapp1.components.DropdownMenuContentCapitan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.material.FloatingActionButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

// Modelos de datos previos sin cambios...
data class Usuario(
    val email: String = "",
    val equipo_id: String = "",
    val name: String = "",
    val role: String = "",
    val torneo_id: String = ""
)

data class Equipo(
    val nombre_equipo: String = "",
    val logotipo: String = ""
)

data class UserWithTeam(
    val usuario: Usuario,
    val equipo: Equipo
)

// Repository y ViewModel sin cambios...
class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    fun getCurrentUserDataWithTeam(
        onSuccess: (UserWithTeam) -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError("No hay usuario autenticado")
            return
        }

        val userRef = database.reference.child("usuarios").child(currentUser.uid)

        userRef.get().addOnSuccessListener { userSnapshot ->
            if (userSnapshot.exists()) {
                val usuario = userSnapshot.getValue(Usuario::class.java)
                usuario?.let {
                    val equipoRef = database.reference.child("equipos").child(it.equipo_id)
                    equipoRef.get().addOnSuccessListener { equipoSnapshot ->
                        if (equipoSnapshot.exists()) {
                            val equipo = equipoSnapshot.getValue(Equipo::class.java)
                            equipo?.let { equipoData ->
                                onSuccess(UserWithTeam(usuario, equipoData))
                            } ?: onError("Error al convertir los datos del equipo")
                        } else {
                            onError("Equipo no encontrado")
                        }
                    }.addOnFailureListener { error ->
                        onError("Error al obtener datos del equipo: ${error.message}")
                    }
                } ?: onError("Error al convertir los datos del usuario")
            } else {
                onError("Usuario no encontrado")
            }
        }.addOnFailureListener {
            onError("Error: ${it.message}")
        }
    }
}

class UserViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userData = MutableStateFlow<UserResult<UserWithTeam>>(UserResult.Loading)
    val userData: StateFlow<UserResult<UserWithTeam>> = _userData.asStateFlow()

    fun loadUserData() {
        _userData.value = UserResult.Loading
        repository.getCurrentUserDataWithTeam(
            onSuccess = { userWithTeam ->
                _userData.value = UserResult.Success(userWithTeam)
            },
            onError = { error ->
                _userData.value = UserResult.Error(error)
            }
        )
    }
}

sealed class UserResult<out T> {
    object Loading : UserResult<Nothing>()
    data class Success<out T>(val data: T) : UserResult<T>()
    data class Error(val message: String) : UserResult<Nothing>()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadoresCapitan(
    viewModel: UserViewModel = viewModel(),
    navController: NavHostController
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val userDataState by viewModel.userData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    Column {
        TopAppBar(
            title = { Text(
                text = "Jugadores del equipo",
                color = Color(0xFF232E3C),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) }, // Título vacío ya que mostraremos la información del equipo abajo
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFF0F4FC)
            ),
            navigationIcon = {
                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.Gray)
                }
            },
        )

        DropdownMenuContentCapitan(
            menuExpanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            navController = navController
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (val result = userDataState) {
                is UserResult.Loading -> {

                }
                is UserResult.Success -> {
                    val userWithTeam = result.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()

                    ) {
                        Row(
                            modifier = Modifier
                                .background(Color(0xFF3E4F62))
                                .padding( horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AsyncImage(
                                model = userWithTeam.equipo.logotipo,
                                contentDescription = "Logotipo del equipo",
                                modifier = Modifier
                                    .background(Color.White, CircleShape)
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color(0xFFF0F4FC), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Equipo ${ userWithTeam.equipo.nombre_equipo }",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                        }
                        // Lista de jugadores
                        JugadorEjemplo(equipoId = userWithTeam.usuario.equipo_id)
                    }
                }
                is UserResult.Error -> {
                    Text(
                        text = "Error: ${result.message}",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .wrapContentSize(Alignment.Center)
                    )
                }
            }

            // Botón flotante para agregar jugadores
            FloatingActionButton(
                onClick = {
                    val equipoId = (userDataState as? UserResult.Success)?.data?.usuario?.equipo_id
                    equipoId?.let {
                        navController.navigate("crearjugadorformcapitan/$it")
                    }
                },
                modifier = Modifier
                    .size(95.dp)
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFF3E4F62)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Jugador",
                    tint = Color.White
                )
            }
        }
    }
}