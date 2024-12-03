package com.example.ligasmartapp1.CapitanView

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ligasmartapp1.Torneo.TablaGeneral
import com.example.ligasmartapp1.components.DropdownMenuContentCapitan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Cambio del nombre del data class a `Representante`
data class Representante(
    val torneo_id: String = ""
)

// Repository modificado solo para obtener `torneo_id`, renombrado a `RepresentanteRepository`
class RepresentanteRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    fun getCurrentRepresentanteTorneoId(
        onSuccess: (String) -> Unit,
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
                val representante = userSnapshot.getValue(Representante::class.java)
                representante?.let {
                    onSuccess(it.torneo_id)
                } ?: onError("Error al convertir los datos del representante")
            } else {
                onError("Representante no encontrado")
            }
        }.addOnFailureListener {
            onError("Error: ${it.message}")
        }
    }
}

// ViewModel modificado para obtener el `torneo_id`, renombrado a `RepresentanteViewModel`
class RepresentanteViewModel : ViewModel() {
    private val repository = RepresentanteRepository()

    private val _torneoId = MutableStateFlow<RepresentanteResult<String>>(RepresentanteResult.Loading)
    val torneoId: StateFlow<RepresentanteResult<String>> = _torneoId.asStateFlow()

    fun loadTorneoId() {
        _torneoId.value = RepresentanteResult.Loading
        repository.getCurrentRepresentanteTorneoId(
            onSuccess = { torneoId ->
                _torneoId.value = RepresentanteResult.Success(torneoId)
            },
            onError = { error ->
                _torneoId.value = RepresentanteResult.Error(error)
            }
        )
    }
}

// Resultado de la operación modificado a `RepresentanteResult`
sealed class RepresentanteResult<out T> {
    object Loading : RepresentanteResult<Nothing>()
    data class Success<out T>(val data: T) : RepresentanteResult<T>()
    data class Error(val message: String) : RepresentanteResult<Nothing>()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisiticasCapitan(
    viewModel: RepresentanteViewModel = viewModel(),
    navController: NavHostController
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val torneoIdState by viewModel.torneoId.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTorneoId()
    }

    Column {
        TopAppBar(
            title = { Text(
                text = "Estadísticas del equipo",
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
            when (val result = torneoIdState) {
                is RepresentanteResult.Loading -> { /* Cargando */ }
                is RepresentanteResult.Success -> {
                    val torneoId = result.data
                    TablaGeneral(navController, torneoId)
                }
                is RepresentanteResult.Error -> {
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
        }
    }
}
