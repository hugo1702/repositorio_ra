package com.example.ligasmartapp1.data.Notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ligasmartapp1.R
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class Notificaciones(val context: Context, val userUid: String) {
    private val existingPartidosIds = mutableSetOf<String>() // Conjunto para almacenar los IDs de partidos existentes
    private val userTorneosIds = mutableSetOf<String>() // Conjunto para almacenar los IDs de torneos del usuario

    fun startPartidosListener() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("torneos")

        // Primero obtenemos los torneos que pertenecen al usuario
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = databaseReference.orderByChild("user_id").equalTo(userUid).get().await()
                for (childSnapshot in snapshot.children) {
                    val torneoId = childSnapshot.key
                    torneoId?.let { userTorneosIds.add(it) } // Almacena el ID del torneo
                }

                // Solo procede si hay torneos del usuario
                if (userTorneosIds.isNotEmpty()) {
                    loadExistingPartidos() // Carga los partidos existentes al inicio
                    listenForNewPartidos() // Escucha solo nuevos partidos
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Cargar los partidos actuales del usuario para evitar notificaciones duplicadas
    private suspend fun loadExistingPartidos() {
        val partidosRef = FirebaseDatabase.getInstance().getReference("partidos")
        try {
            val snapshot = partidosRef.get().await()
            for (childSnapshot in snapshot.children) {
                val partido = childSnapshot.getValue(Partido::class.java)
                partido?.let {
                    if (it.torneo_id in userTorneosIds) {
                        existingPartidosIds.add(it.id) // Agrega el ID del partido actual
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Función para escuchar solo nuevos partidos del usuario
    private fun listenForNewPartidos() {
        val partidosRef = FirebaseDatabase.getInstance().getReference("partidos")

        partidosRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val partido = snapshot.getValue(Partido::class.java)
                partido?.let {
                    // Verificar si el partido pertenece a los torneos del usuario y es nuevo
                    if (it.torneo_id in userTorneosIds && it.id !in existingPartidosIds) {
                        showNotification(it)
                        existingPartidosIds.add(it.id) // Marcar el partido como notificado
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showNotification(partido: Partido) {
        val equiposRef = FirebaseDatabase.getInstance().getReference("equipos")

        equiposRef.child(partido.equipo_local).child("nombre_equipo").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(localSnapshot: DataSnapshot) {
                val nombreEquipoLocal = localSnapshot.getValue(String::class.java) ?: "Equipo Local"

                equiposRef.child(partido.equipo_visitante).child("nombre_equipo").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(visitanteSnapshot: DataSnapshot) {
                        val nombreEquipoVisitante = visitanteSnapshot.getValue(String::class.java) ?: "Equipo Visitante"
                        displayNotification(partido, nombreEquipoLocal, nombreEquipoVisitante)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun displayNotification(partido: Partido, nombreEquipoLocal: String, nombreEquipoVisitante: String) {
        val notificationId = partido.id.hashCode()

        val numeroJornada = partido.numero_jornada?.toString() ?: "N/A"

        val builder = NotificationCompat.Builder(context, "PartidoChannel")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Nuevo partido: Jornada $numeroJornada")
            .setContentText("$nombreEquipoLocal vs $nombreEquipoVisitante\n" +
                    "Fecha: ${partido.fecha} - Hora: ${partido.hora}\n" +
                    "Dirección: ${partido.direccion}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "$nombreEquipoLocal vs $nombreEquipoVisitante\n" +
                        "Fecha: ${partido.fecha} - Hora: ${partido.hora}\n" +
                        "Dirección: ${partido.direccion}"
            ))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(notificationId, builder.build())
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Partidos Channel"
            val descriptionText = "Canal para notificaciones de nuevos partidos"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("PartidoChannel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

// Definición de la data class Partido
data class Partido(
    val id: String = "",
    val cancha_id: String = "",
    val direccion: String = "",
    val equipo_local: String = "",
    val equipo_visitante: String = "",
    val fecha: String = "",
    val hora: String = "",
    val numero_jornada: Any? = null,
    val torneo_id: String = ""
)
