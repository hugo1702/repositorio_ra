package com.example.ligasmartapp1.Torneo
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ligasmartapp1.components.CargandoAnimacion
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import android.app.DatePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.ligasmartapp1.R

@Composable
fun CrearJugadorForm(navController: NavController, equipoId: String?) {
    if (equipoId == null) {
        // Si no hay ID de equipo, mostramos un error y opción de regresar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Error: No se proporcionó ID del equipo",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigateUp() }) {
                Text("Regresar")
            }
        }
        return
    }

    RegistroJugadorScreen(
        equipoId = equipoId,
        onRegistroExitoso = {
            // Navegamos de vuelta a la pantalla anterior y mostramos un mensaje de éxito
            navController.navigateUp()
            // Aquí podrías implementar un sistema de mensajes para mostrar el éxito
        },
        onCancelar = {
            navController.navigateUp()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroJugadorScreen(
    equipoId: String,
    onRegistroExitoso: () -> Unit,
    onCancelar: () -> Unit
) {
    var nombreCompleto by remember { mutableStateOf("") }
    var curp by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var lugarNacimiento by remember { mutableStateOf("") }
    var estatura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var numeroCamiseta by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    // Calendar setup
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            // Format the date as YYYY-MM-DD
            fechaNacimiento = String.format(
                "%04d-%02d-%02d",
                selectedYear,
                selectedMonth + 1,
                selectedDay
            )
        },
        year,
        month,
        day
    )

    // Set max date to current date (no future dates allowed)
    datePickerDialog.datePicker.maxDate = calendar.timeInMillis

    // Set min date to 100 years ago
    calendar.add(Calendar.YEAR, -100)
    datePickerDialog.datePicker.minDate = calendar.timeInMillis

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        imagenUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Foto del jugador",
                modifier = Modifier
                    .size(150.dp)
                    .padding(vertical = 0.dp)
                    .clip(CircleShape),  // Hace que la imagen sea redonda
                contentScale = ContentScale.Crop  // Ajusta la imagen para que llene el tamaño sin distorsión
            )
        }
        // Imagen del jugador
        Button(modifier = Modifier,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3461FF),
                contentColor = Color.White
            ),
            onClick = { imagePicker.launch("image/*") }) {
            Icon(
                imageVector = Icons.Default.Photo,  // Ícono predeterminado de la cámara
                contentDescription = "Seleccionar imagen",
                modifier = Modifier.size(25.dp)  // Ajusta el tamaño del ícono
            )
            Spacer(modifier = Modifier.width(8.dp))  // Espacio entre el ícono y el texto
            Text(text = if (imagenUri != null) "Cambiar Foto" else "Seleccionar Foto")
        }

        // Campos del formulario
        OutlinedTextField(
            shape = RoundedCornerShape(12.dp),
            value = nombreCompleto,
            onValueChange = {
                // Valida que solo contenga letras y no supere los 30 caracteres
                if (it.matches(Regex("^[a-zA-Z\\s]*$")) && it.length <= 30) {
                    nombreCompleto = it
                }
            },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth(),
            isError = nombreCompleto.isBlank() && error != null,
            singleLine = true // Evita el salto de línea y el agrandamiento de la caja de texto
        )

        OutlinedTextField(
            shape = RoundedCornerShape(12.dp),
            value = curp,
            onValueChange = {
                // Permite letras minúsculas, mayúsculas y números, y no supera los 18 caracteres
                if (it.length <= 18) {
                    curp = it.uppercase() // Convierte la entrada a mayúsculas automáticamente
                }
            },
            label = { Text("CURP(18 caracteres)") },
            modifier = Modifier.fillMaxWidth(),
            isError = curp.isBlank() && error != null,
            singleLine = true // Evita el salto de línea y el agrandamiento de la caja de texto
        )


        OutlinedTextField(
            shape = RoundedCornerShape(12.dp),
            value = email,
            onValueChange = {
                // Valida que el correo contenga caracteres válidos
                if (it.matches(Regex("^[a-zA-Z0-9@._-]*$"))) {
                    email = it
                }
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = email.isBlank() && error != null,
            singleLine = true // Evita el salto de línea y el agrandamiento de la caja de texto
        )

        OutlinedTextField(
            shape = RoundedCornerShape(12.dp),
            value = fechaNacimiento,
            onValueChange = { /* Readonly */ },
            label = { Text("Fecha de Nacimiento") },
            modifier = Modifier.fillMaxWidth(),
            isError = fechaNacimiento.isBlank() && error != null,
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    datePickerDialog.show()
                }) {
                    Icon(
                        modifier = Modifier.size(25.dp),
                        painter = painterResource(id = R.drawable.calendarioicon),
                        contentDescription = "Seleccionar fecha"
                    )
                }
            }
        )

        OutlinedTextField(
            shape = RoundedCornerShape(12.dp),
            value = lugarNacimiento,
            onValueChange = {
                // Valida que solo se introduzcan letras y espacios, y que no supere los 40 caracteres
                if (it.matches(Regex("^[a-zA-Z\\s,]*\$")) && it.length <= 40) {
                    lugarNacimiento = it
                }
            },
            label = { Text("Lugar de Nacimiento") },
            modifier = Modifier.fillMaxWidth(),
            isError = lugarNacimiento.isBlank() && error != null,
            singleLine = true // Evita el salto de línea y el agrandamiento de la caja de texto
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,  // Distribuye el espacio de forma uniforme
            verticalAlignment = Alignment.CenterVertically     // Centra verticalmente ambos campos
        ) {
            OutlinedTextField(
                shape = RoundedCornerShape(12.dp),
                value = estatura,
                onValueChange = {
                    // Valida que la estatura esté en el formato correcto (1.00 - 2.50 metros)
                    if (it.matches(Regex("^\\d{0,1}(\\.\\d{0,2})?\$"))) {
                        estatura = it
                    }
                },
                label = { Text("Estatura (M)") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                isError = (estatura.isBlank() || !esEstaturaValida(estatura)) && error != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,  // Teclado numérico con decimales
                    imeAction = ImeAction.Done           // Acción final en el teclado
                )
            )

            // Función para validar si la estatura está en el rango adecuado
            fun esEstaturaValida(estatura: String): Boolean {
                return try {
                    val estaturaEnMetros = estatura.toFloat()
                    estaturaEnMetros in 1.0..2.5  // Rango aceptable: 1.00 a 2.50 metros
                } catch (e: NumberFormatException) {
                    false
                }
            }


            OutlinedTextField(
                shape = RoundedCornerShape(12.dp),
                value = peso,
                onValueChange = {
                    // Valida que el valor contenga solo números o un punto decimal y que no exceda los 5 caracteres
                    if (it.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$")) && it.length <= 6) {
                        peso = it
                    }
                },
                label = { Text("Peso (KG)") },
                modifier = Modifier
                    .weight(1f)  // Igual peso para los campos
                    .padding(start = 8.dp),  // Espaciado entre los campos
                isError = peso.isBlank() && error != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,  // Configura el teclado numérico
                    imeAction = ImeAction.Done           // Acción final en el teclado
                )
            )
        }


        OutlinedTextField(
            shape = RoundedCornerShape(12.dp),
            value = numeroCamiseta,
            onValueChange = {
                // Valida que el número de camiseta sea solo un número entero y esté entre 1 y 99
                if (it.matches(Regex("^[1-9][0-9]?$"))) {
                    numeroCamiseta = it
                }
            },
            label = { Text("Número de Camiseta") },
            modifier = Modifier.fillMaxWidth(),
            isError = numeroCamiseta.isBlank() && error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,  // Configura el teclado numérico
                imeAction = ImeAction.Done           // Acción final en el teclado
            )
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                shape = RoundedCornerShape(10.dp),
                onClick = onCancelar,
                modifier = Modifier.weight(1f),

            ) {
                Text("Cancelar")
            }

            Button(
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3461FF),
                    contentColor = Color.White
                ),
                onClick = {
                    if (validarCampos(
                            nombreCompleto,
                            curp,
                            email,
                            fechaNacimiento,
                            lugarNacimiento,
                            estatura,
                            peso,
                            numeroCamiseta
                        )
                    ) {
                        error = null
                        isLoading = true
                        registrarJugador(
                            imagenUri = imagenUri,
                            nombreCompleto = nombreCompleto,
                            curp = curp,
                            email = email,
                            fechaNacimiento = fechaNacimiento,
                            lugarNacimiento = lugarNacimiento,
                            estatura = estatura,
                            peso = peso,
                            numeroCamiseta = numeroCamiseta,
                            equipoId = equipoId,
                            onSuccess = {
                                isLoading = false
                                onRegistroExitoso()
                            },
                            onError = { e ->
                                isLoading = false
                                error = e.message ?: "Error al registrar jugador"
                            }
                        )
                    } else {
                        error = "Por favor, complete todos los campos correctamente"
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CargandoAnimacion()
                } else {
                    Text("Registrar")
                }
            }
        }
    }
}

private fun validarCampos(
    nombreCompleto: String,
    curp: String,
    email: String,
    fechaNacimiento: String,
    lugarNacimiento: String,
    estatura: String,
    peso: String,
    numeroCamiseta: String
): Boolean {
    return nombreCompleto.isNotBlank() &&
            curp.isNotBlank() && curp.length == 18 &&
            email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            fechaNacimiento.isNotBlank() && fechaNacimiento.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
            lugarNacimiento.isNotBlank() &&
            estatura.isNotBlank() && estatura.toFloatOrNull() != null &&
            peso.isNotBlank() && peso.toFloatOrNull() != null &&
            numeroCamiseta.isNotBlank() && numeroCamiseta.toIntOrNull() != null
}

private fun registrarJugador(
    imagenUri: Uri?,
    nombreCompleto: String,
    curp: String,
    email: String,
    fechaNacimiento: String,
    lugarNacimiento: String,
    estatura: String,
    peso: String,
    numeroCamiseta: String,
    equipoId: String,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    val storage = FirebaseStorage.getInstance()
    val database = FirebaseDatabase.getInstance()

    val jugadorId = database.reference.child("jugadores").push().key ?: run {
        onError(Exception("Error al generar ID del jugador"))
        return
    }

    if (imagenUri != null) {
        val fotoRef = storage.reference.child("fotos_jugadores/$jugadorId.jpg")

        fotoRef.putFile(imagenUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            fotoRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fotoUrl = task.result.toString()
                guardarDatosJugador(
                    jugadorId,
                    fotoUrl,
                    nombreCompleto,
                    curp,
                    email,
                    fechaNacimiento,
                    lugarNacimiento,
                    estatura,
                    peso,
                    numeroCamiseta,
                    equipoId,
                    onSuccess,
                    onError
                )
            } else {
                onError(task.exception ?: Exception("Error al subir la imagen"))
            }
        }
    } else {
        guardarDatosJugador(
            jugadorId,
            null,
            nombreCompleto,
            curp,
            email,
            fechaNacimiento,
            lugarNacimiento,
            estatura,
            peso,
            numeroCamiseta,
            equipoId,
            onSuccess,
            onError
        )
    }
}

private fun guardarDatosJugador(
    jugadorId: String,
    fotoUrl: String?,
    nombreCompleto: String,
    curp: String,
    email: String,
    fechaNacimiento: String,
    lugarNacimiento: String,
    estatura: String,
    peso: String,
    numeroCamiseta: String,
    equipoId: String,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    val jugadorData = hashMapOf(
        "nombre_completo" to nombreCompleto,
        "curp" to curp,
        "email" to email,
        "fecha_nacimiento" to fechaNacimiento,
        "lugar_nacimiento" to lugarNacimiento,
        "estatura" to estatura,
        "peso" to peso,
        "numero_camiseta" to numeroCamiseta,
        "equipo_id" to equipoId
    )

    fotoUrl?.let { jugadorData["foto"] = it }

    FirebaseDatabase.getInstance()
        .reference
        .child("jugadores")
        .child(jugadorId)
        .setValue(jugadorData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onError(it) }
}

fun esEstaturaValida(estatura: String): Boolean {
    return try {
        val estaturaEnMetros = estatura.toFloat()
        estaturaEnMetros in 1.0..2.5  // Rango aceptable: 1.00 a 2.50 metros
    } catch (e: NumberFormatException) {
        false
    }
}