package com.example.ligasmartapp1

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.sharp.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun TerminosCondiciones(navController: NavHostController) {
    var menuExpanded by remember { mutableStateOf(false) }

    Column () {
        TopAppBar(
            title = {
                Text(
                    text = "Contrato de Términos y Condiciones",
                    color = Color(0xFF232E3C),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),

                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFF0F4FC)
            ),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            // Contenedor para el contenido desplazable
            Box(
                modifier = Modifier.weight(1f)
            ) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(8.dp)

                ) {
                        Text(
                            text = "Este contrato establece los Términos y Condiciones de Uso de la plataforma Liga Smart App, propiedad de WebWiz Studio. El acceso y uso de la plataforma, tanto en su versión web como móvil, están sujetos a la aceptación de los términos descritos a continuación. Al crear una cuenta o utilizar cualquier parte del servicio, el usuario acepta estar vinculado por este contrato.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "1. DEFINICIONES",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )

                        Text(
                            text = "• Plataforma: Se refiere a la aplicación web y móvil Liga Smart App.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "• Usuario: Cualquier persona que acceda a la plataforma para gestionar torneos, ligas o equipos de fútbol.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "• Empresa: WebWiz Studio, propietaria y desarrolladora de la plataforma Liga Smart App.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        Text(
                            text = "2. REGISTRO Y CREACIÓN DE CUENTA",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )

                        Text(
                            text = "Para acceder a los servicios de Liga Smart App, el usuario debe registrarse proporcionando información válida y actualizada. El usuario es responsable de mantener la seguridad de su cuenta, incluyendo su nombre de usuario y contraseña.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "3. PLANES DE SUSCRIPCIÓN",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )

                        Text(
                            text = "Liga Smart App ofrece dos tipos de planes:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "• Plan Gratis:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                        )

                        Text(
                            text = "o Creación de hasta 2 torneos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Máximo de 15 equipos por torneo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Calendarios manuales para la programación de partidos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Añadir canchas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "• Plan Premium:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                        )

                        Text(
                            text = "o Creación ilimitada de torneos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Equipos ilimitados en cada torneo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Calendarios automáticos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Añadir canchas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Gestión de pagos para ligas y torneos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Generación de reportes detallados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Soporte técnico 24/7.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "o Costo: $99 mensuales.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                        )

                        Text(
                            text = "El usuario puede optar por cualquiera de los planes mencionados. El Plan Gratis tiene limitaciones en cuanto al número de torneos y equipos que pueden crearse, mientras que el Plan Premium desbloquea todas las funcionalidades avanzadas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth()
                        )



                    Text(
                        text = "4. MÉTODO DE PAGO Y PROCESO DE SUSCRIPCIÓN",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 18.sp, // Ajusta este valor según lo necesites
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "La contratación del Plan Premium requiere el pago mediante una pasarela de pagos que acepta tarjetas de crédito y débito. El usuario deberá ingresar los datos de la tarjeta (número, fecha de vencimiento, CVV, etc.) de manera segura para completar la transacción.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify // Justificación del texto
                    )

                    Text(
                        text = "Las suscripciones son de carácter recurrente y se renovarán automáticamente cada mes, salvo que el usuario cancele la suscripción antes de la fecha de renovación. La cancelación de la suscripción no implica la devolución de importes ya pagados, salvo en las condiciones descritas en el apartado de devoluciones.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify // Justificación del texto
                    )

                    Text(
                        text = "5. POLÍTICA DE DEVOLUCIONES",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "Los usuarios tienen un plazo de 7 días a partir de la compra de la suscripción Premium para solicitar la devolución de su dinero. Para hacerlo, deben enviar un correo electrónico a webwizstudio@hotmail.com con el comprobante de pago y una breve descripción del motivo de la solicitud.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify // Justificación del texto
                    )

                    Text(
                        text = "Si la devolución es aprobada, el reembolso se procesará mediante el mismo método de pago utilizado en la compra, y el acceso al Plan Premium será revocado. No se aceptarán devoluciones después del plazo de 7 días.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify // Justificación del texto
                    )

                    Text(
                        text = "6. USO DE LA PLATAFORMA",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "El usuario se compromete a utilizar Liga Smart App de manera responsable, respetando las leyes aplicables y los derechos de terceros. Está prohibido utilizar la plataforma para realizar actividades ilícitas o fraudulentas. La empresa se reserva el derecho de suspender o eliminar cualquier cuenta que viole estos términos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify // Justificación del texto
                    )

                    Text(
                        text = "7. PERMISOS REQUERIDOS",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "Para el funcionamiento completo de Liga Smart App, la plataforma puede solicitar acceso a ciertos permisos del dispositivo, tales como:\n" +
                                "• Notificaciones: Para alertas sobre partidos, ligas y torneos.\n" +
                                "• Cámara y almacenamiento: Para subir imágenes de equipos o jugadores.\n" +
                                "• GPS: Para localizar canchas y ubicaciones de eventos.\n" +
                                "• Contactos: Para invitar a otros usuarios o jugadores.\n" +
                                "Al aceptar estos términos, el usuario autoriza a la plataforma a solicitar y utilizar dichos permisos conforme a la finalidad para la que fueron habilitados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify // Justificación del texto
                    )

                    Text(
                        text = "8. RESPONSABILIDAD DEL USUARIO",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "El usuario es responsable de mantener la confidencialidad de su cuenta y de todas las actividades que se realicen en ella. WebWiz Studio no se hace responsable por accesos no autorizados debido a la negligencia del usuario en el manejo de sus credenciales de acceso.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify // Justificación del texto
                    )

                    Text(
                        text = "9. PROPIEDAD INTELECTUAL",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "Todos los derechos sobre el software, diseño, código, gráficos, logotipos y cualquier otro material proporcionado por Liga Smart App son propiedad de WebWiz Studio. El usuario se compromete a no reproducir, distribuir o explotar comercialmente cualquier contenido de la plataforma sin el consentimiento expreso de la empresa.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify // Justificación del texto
                    )
                    Text(
                        text = "10. EXCLUSIÓN DE GARANTÍAS",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "WebWiz Studio no garantiza que la plataforma funcione sin interrupciones o errores. La empresa se reserva el derecho de realizar actualizaciones o mantenimiento de la plataforma sin previo aviso, y no se responsabiliza por la pérdida de datos o interrupciones del servicio.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        textAlign = TextAlign.Justify
                    )

                    Text(
                        text = "11. LIMITACIÓN DE RESPONSABILIDAD",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "WebWiz Studio no será responsable por daños directos, indirectos, incidentales, consecuentes o punitivos relacionados con el uso de Liga Smart App, incluyendo, pero no limitándose a la pérdida de datos, pérdida de ingresos, o daños a la reputación del usuario.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        textAlign = TextAlign.Justify
                    )

                    Text(
                        text = "12. MODIFICACIÓN DE LOS TÉRMINOS",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "WebWiz Studio se reserva el derecho de modificar estos Términos y Condiciones en cualquier momento. Las modificaciones serán publicadas en la plataforma, y el uso continuado de la misma después de dichas modificaciones implicará la aceptación de los nuevos términos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        textAlign = TextAlign.Justify
                    )

                    Text(
                        text = "13. CANCELACIÓN Y TERMINACIÓN",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "El usuario puede cancelar su suscripción en cualquier momento desde su cuenta, pero no tendrá derecho a la devolución de importes ya pagados salvo en los casos mencionados en la política de devoluciones.\n" +
                                "WebWiz Studio se reserva el derecho de suspender o eliminar cuentas que incumplan estos Términos y Condiciones, sin necesidad de previo aviso.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        textAlign = TextAlign.Justify
                    )

                    Text(
                        text = "14. JURISDICCIÓN Y LEY APLICABLE",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "Este contrato se rige por las leyes de México, y cualquier controversia que surja en relación con el uso de Liga Smart App será sometida a los tribunales competentes de México.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        textAlign = TextAlign.Justify
                    )

                    Text(
                        text = "15. LICENCIA DEL SITIO WEB Y APLICACIÓN",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "Liga Smart App, tanto en su versión web como móvil, está bajo una licencia de software propietario. Esto significa que todos los derechos de propiedad intelectual sobre el software, el diseño, el código, y cualquier otro material proporcionado a través de la plataforma son propiedad exclusiva de WebWiz Studio.\n" +
                                "• Liga Smart App otorga a los usuarios finales una licencia limitada, no exclusiva, no transferible y revocable para utilizar el sitio web y la aplicación en conformidad con los Términos y Condiciones establecidos aquí.\n" +
                                "• Esta licencia permite el uso de la plataforma únicamente para los fines descritos (gestión de torneos, ligas, equipos de fútbol, etc.) y bajo los términos de los planes de suscripción (Gratis o Premium). Queda estrictamente prohibido modificar, distribuir, copiar, revender o explotar de cualquier otra manera los contenidos y funcionalidades de Liga Smart App sin la autorización previa de WebWiz Studio.\n" +
                                "• WebWiz Studio se reserva el derecho de revocar la licencia de uso en cualquier momento si el usuario incumple con las disposiciones de este contrato.",

                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        textAlign = TextAlign.Justify
                    )

                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

                    }
                }
            }
            Button(
                onClick = {navController.navigate(Routes.Login.route)
                    menuExpanded = !menuExpanded},
                modifier = Modifier
                    .padding(8.dp)
                    .height(40.dp)
                    .width(180.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3461FF),
                    contentColor = Color.White
                )
            ) {
                androidx.compose.material3.Text("Aceptar Terminos")
            }
        }

    }
}

