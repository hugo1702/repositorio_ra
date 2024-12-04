package com.example.ligasmartapp1

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ligasmartapp1.components.InternetConnectionAlert
import com.example.ligasmartapp1.data.UserPreferencesStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(navController: NavHostController, activity: ComponentActivity) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordFocusRequester = remember { FocusRequester() }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Inicializar FirebaseAuth y UserPreferencesStore
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val userPreferencesStore = UserPreferencesStore(activity)

    fun validateInputU(input: String): String {
        return input.filter { it.isLetterOrDigit() || it == '.' || it == '@' || it == '-' || it == '_' }.take(50)
    }
    fun validateInputP(input: String): String {
        return input.take(25)
    }

    val userIcon: Painter = painterResource(id = R.drawable.userlogin)
    val passwordIcon: Painter = painterResource(id = R.drawable.password)

    BackHandler {
        activity.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF3271D5), Color(0xFF55D952))
                )
            )
    ) {
        InternetConnectionAlert()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FC))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "LigaSmart App",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        singleLine = true,
                        value = username,
                        onValueChange = {
                            username = validateInputU(it)
                            errorMessage = ""
                        },
                        label = { Text("Ingrese su correo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                painter = userIcon,
                                modifier = Modifier.size(22.dp),
                                contentDescription = "Usuario",
                                tint = Color.Gray
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    OutlinedTextField(
                        singleLine = true,
                        value = password,
                        onValueChange = {
                            password = validateInputP(it)
                            errorMessage = ""
                        },
                        label = { Text("Ingrese su contraseña") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(62.dp)
                            .focusRequester(passwordFocusRequester),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                painter = passwordIcon,
                                modifier = Modifier.size(22.dp),
                                contentDescription = "Contraseña",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = Color.Gray
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF3461FF),
                                uncheckedColor = Color.Gray
                            ),
                            checked = checked,
                            onCheckedChange = { checked = it }
                        )
                        Text(
                            text = "Aceptar ",
                            modifier = Modifier.padding(start = 0.dp)
                        )
                        Text(
                            text = "Términos y Condiciones",
                            modifier = Modifier
                                .padding(start = 0.dp)
                                .clickable { navController.navigate(Routes.TerminosCondiciones.route) },
                            color = Color(0xFF3461FF),
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (username.isEmpty() || password.isEmpty()) {
                                errorMessage = "Los campos no pueden estar vacíos."
                            } else if (!checked) {
                                errorMessage = "Debe aceptar los términos y condiciones."
                            } else {
                                isLoading = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val authResult = withTimeout(10000) {
                                            auth.signInWithEmailAndPassword(username, password).await()
                                        }
                                        val user: FirebaseUser? = auth.currentUser

                                        user?.let { currentUser ->
                                            // Guardar el UID del usuario que se acaba de loguear
                                            userPreferencesStore.saveUserUid(currentUser.uid)

                                            // Continuar con la lógica de navegación
                                            val roleSnapshot = withTimeout(10000) {
                                                FirebaseDatabase.getInstance().getReference("roles")
                                                    .child(currentUser.uid).get().await()
                                            }

                                            if (roleSnapshot.exists()) {
                                                val role: String? = roleSnapshot.child("role").value as? String

                                                val subscriptionSnapshot = withTimeout(10000) {
                                                    FirebaseDatabase.getInstance().getReference("subscriptions")
                                                        .child(currentUser.uid).get().await()
                                                }

                                                if (subscriptionSnapshot.exists()) {
                                                    val subscriptionStatus: String? = subscriptionSnapshot.child("status").value as? String

                                                    withContext(Dispatchers.Main) {
                                                        when {
                                                            role == "capitan" -> {
                                                                navController.navigate(Routes.HomeCapitan.route)
                                                            }
                                                            role == "superadmin" && subscriptionStatus == "premium" -> {
                                                                navController.navigate(Routes.Home.route)
                                                            }
                                                            role == "superadmin" && subscriptionStatus != "premium" -> {
                                                                errorMessage = "No tienes acceso a suscripción premium."
                                                            }
                                                            else -> {
                                                                errorMessage = "Rol no reconocido."
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    val userRoleSnapshot = withTimeout(10000) {
                                                        FirebaseDatabase.getInstance().getReference("usuarios")
                                                            .child(currentUser.uid).get().await()
                                                    }

                                                    if (userRoleSnapshot.exists()) {
                                                        val userRole: String? = userRoleSnapshot.child("role").value as? String
                                                        withContext(Dispatchers.Main) {
                                                            if (userRole == "representante") {
                                                                navController.navigate(Routes.HomeCapitan.route)
                                                            } else {
                                                                errorMessage = "No tienes un rol asignado correctamente."
                                                            }
                                                        }
                                                    } else {
                                                        withContext(Dispatchers.Main) {
                                                            errorMessage = "No estás autenticado."
                                                        }
                                                    }
                                                }
                                            } else {
                                                val userRoleSnapshot = withTimeout(10000) {
                                                    FirebaseDatabase.getInstance().getReference("usuarios")
                                                        .child(currentUser.uid).get().await()
                                                }

                                                if (userRoleSnapshot.exists()) {
                                                    val userRole: String? = userRoleSnapshot.child("role").value as? String
                                                    withContext(Dispatchers.Main) {
                                                        if (userRole == "representante") {
                                                            navController.navigate(Routes.HomeCapitan.route)
                                                        } else {
                                                            errorMessage = "No tienes un rol asignado correctamente."
                                                        }
                                                    }
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        errorMessage = "No estás autenticado."
                                                    }
                                                }
                                            }
                                        }
                                    } catch (e: TimeoutCancellationException) {
                                        Log.e("FirebaseError", "Timeout de conexión", e)
                                        withContext(Dispatchers.Main) {
                                            errorMessage = "Conexión lenta. Intenta nuevamente."
                                        }
                                    } catch (e: FirebaseAuthException) {
                                        Log.e("FirebaseError", "Error de autenticación", e)
                                        withContext(Dispatchers.Main) {
                                            errorMessage = getErrorMessageInSpanish(e)
                                        }
                                    } catch (e: Exception) {
                                        Log.e("FirebaseError", "Error inesperado", e)
                                        withContext(Dispatchers.Main) {
                                            errorMessage = "Comprueba la conexión a internet"
                                        }
                                    } finally {
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .width(180.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF3271D5),
                                        Color(0xFF55D952)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            Text("Procesando...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        } else {
                            Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LoginScreen()
                }
                ShowUidScreen(activity)
            }
        }
    }
}

// Función para manejar mensajes de error en español
fun getErrorMessageInSpanish(exception: Exception?): String {
    return when (exception?.message) {
        "The email address is badly formatted." -> "El correo tiene un formato incorrecto."
        "There is no user record corresponding to this identifier. The user may have been deleted." -> "No hay un registro de usuario correspondiente a este identificador. El usuario puede haber sido eliminado."
        "The password is invalid or the user does not have a password." -> "La contraseña es inválida o el usuario no tiene una contraseña."
        "The user account has been disabled by an administrator." -> "La cuenta de usuario ha sido deshabilitada por un administrador."
        "The email address is already in use by another account." -> "La dirección de correo electrónico ya está en uso por otra cuenta."
        else -> "Usuario y/o contraseña incorrectos."
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current

    val annotatedText = buildAnnotatedString {
        append("¿No tiene cuenta? ")

        pushStringAnnotation(tag = "URL", annotation = "https://ligasmartapp.com/register")
        withStyle(
            style = SpanStyle(
                color = Color(0xFF3461FF),
                fontWeight = FontWeight.Bold,
            )
        ) {
            append("Regístrate ahora")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        modifier = Modifier,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
        }
    )
}


@Composable
fun ShowUidScreen(activity: ComponentActivity) {
    // Crear una variable de estado para almacenar el UID
    var userUid by remember { mutableStateOf<String?>(null) }
    val userPreferencesStore = UserPreferencesStore(activity)

    // Utilizar LaunchedEffect para obtener el UID cuando se carga la vista
    LaunchedEffect(Unit) {
        userUid = userPreferencesStore.getUserUid()
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (userUid != null) {
            Text(text = "$userUid", fontSize = 10.sp, color = Color.Gray)
        } else {
            Text(text = "UID no disponible", fontSize = 10.sp)
        }
    }
}
