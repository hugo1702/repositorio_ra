package com.example.ligasmartapp1

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPefil(navController: NavHostController) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = "Editar Perfil",
                    color = Color(0xFF232E3C),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFF0F4FC)
            ),
            navigationIcon = {
                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color(0xFF232E3C))
                }
            }
        )
        com.example.ligasmartapp1.components.DropdownMenuContent(
            menuExpanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            navController = navController
        )

        InfoPerfil()
    }
}

@Composable
fun InfoPerfil() {
    // Estado para los campos de texto
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) } // Indica si fue exitoso o no


    // Obtener los datos del usuario autenticado
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            nombre = it.displayName ?: "" // Nombre completo del usuario
            correo = it.email ?: "" // Correo del usuario
        }
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Información del perfil
        Text(text = "Información del Perfil", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            singleLine = true,
            value = nombre,
            onValueChange = {
                if (it.length <= 30) { // Validar longitud máxima
                    nombre = it
                }
            },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            singleLine = true,
            value = correo,
            onValueChange = { /* No hacer nada */ },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            readOnly = true, // Establecer como solo lectura
        )

        Button(
            onClick = {
                val user = FirebaseAuth.getInstance().currentUser
                if (nombre.isNotEmpty()) { // Verificar si el nombre no está vacío
                    user?.let {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nombre) // Actualiza el nombre
                            .build()

                        // Actualizar el nombre del perfil
                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    dialogMessage = " El nombre se actualizó correctamente. Los cambios se visualizarán en el próximo inicio de sesión."
                                    isSuccess = true // Indicar que fue exitoso
                                    showDialog = true
                                } else {
                                    dialogMessage = "Error al actualizar el nombre: ${task.exception?.message}"
                                    isSuccess = false
                                    showDialog = true
                                }
                            }
                    }
                } else {
                    // Si el nombre está vacío, mostrar un mensaje de error
                    dialogMessage = "El nombre no puede estar vacío"
                    isSuccess = false
                    showDialog = true
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
                .width(200.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3461FF),
                contentColor = Color.White),

            ){
            Text("Guardar Infomación")
        }
        Spacer(modifier = Modifier.height(15.dp))
        Text(text = "Cambiar contraseña", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        OutlinedTextField(
            singleLine = true,
            value = nuevaContrasena,
            onValueChange = {
                if (it.length <= 20) { // Validar longitud máxima
                    nuevaContrasena = it
                }
            },
            label = { Text("Nueva Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            singleLine = true,
            value = confirmarContrasena,
            onValueChange = {
                if (it.length <= 20) { // Validar longitud máxima
                    confirmarContrasena = it
                }
            },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = {
                val user = FirebaseAuth.getInstance().currentUser
                if (nuevaContrasena == confirmarContrasena && nuevaContrasena.isNotEmpty()) {
                    user?.let {
                        user.updatePassword(nuevaContrasena)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    dialogMessage = "La contraseña se actualizó correctamente. Los cambios se visualizarán en el próximo inicio de sesión."
                                    isSuccess = true // Indicar que fue exitoso
                                    showDialog = true
                                } else {
                                    dialogMessage = "Error al actualizar la contraseña: ${task.exception?.message}"
                                    isSuccess = false
                                    showDialog = true
                                }
                            }
                    }
                } else {
                    dialogMessage = "Las contraseñas no coinciden o están vacías"
                    isSuccess = false
                    showDialog = true
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
                .width(200.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3461FF),
                contentColor = Color.White),

            ){
            Text("Guardar Contraseña")
        }

// Mostrar el AlertDialog si showDialog es verdadero
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Aceptar")
                    }
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Mostrar la imagen según el éxito o error
                        if (isSuccess) {
                            Image(
                                painter = painterResource(id = R.drawable.succes), // Imagen para éxito
                                contentDescription = "Éxito",
                                modifier = Modifier.size(90.dp) // Tamaño de la imagen
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.error), // Imagen para error
                                contentDescription = "Error",
                                modifier = Modifier.size(90.dp) // Tamaño de la imagen
                            )
                        }
                        Text(
                            text = if (isSuccess) "¡Operación exitosa!" else "Error: Ocurrió un problema.", // Cambiar título dinámicamente
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // Espacio entre la imagen y el texto
                        Text(dialogMessage, textAlign = TextAlign.Center , fontSize = 16.sp) // Centrar el texto
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically) // Centrar el contenido verticalmente
            )
        }
    }
}
