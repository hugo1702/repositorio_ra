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

data class Resultado(
    val partido_id: String = "",
    val cancha_id: String = "",
    val direccion: String = "",
    val equipo_local: String = "",
    val equipo_visitante: String = "",
    val fecha: String = "",
    val hora: String = "",
    val numero_jornada: Any? = null,
    val torneo_id: String = "",
    val goles_local: Any? = null,
    val goles_visitante: Any? = null
)

class NotificacionResultados(val context: Context, val userUid: String) {
    private val existingResultadosIds = mutableSetOf<String>()
    private val userTorneosIds = mutableSetOf<String>()

    fun startResultadosListener() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("torneos")

        createResultadosNotificationChannel()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = databaseReference.orderByChild("user_id").equalTo(userUid).get().await()
                for (childSnapshot in snapshot.children) {
                    val torneoId = childSnapshot.key
                    torneoId?.let {
                        userTorneosIds.add(it)
                        Log.d("DatabaseDebug", "Torneo encontrado y añadido a userTorneosIds: $it")
                    }
                }

                if (userTorneosIds.isNotEmpty()) {
                    Log.d("DatabaseDebug", "Torneos del usuario cargados: $userTorneosIds")
                    loadExistingResultados()
                    listenForNewResultados()
                } else {
                    Log.d("DatabaseDebug", "No se encontraron torneos para el usuario")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun loadExistingResultados() {
        val resultadosRef = FirebaseDatabase.getInstance().getReference("resultados")
        try {
            val snapshot = resultadosRef.get().await()
            for (childSnapshot in snapshot.children) {
                val resultado = childSnapshot.getValue(Resultado::class.java)
                resultado?.let {
                    existingResultadosIds.add(it.partido_id)
                    Log.d("DatabaseDebug", "Resultado existente añadido a existingResultadosIds: ${it.partido_id}")
                }
            }
            Log.d("DatabaseDebug", "Resultados cargados: ${existingResultadosIds.size}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun listenForNewResultados() {
        val resultadosRef = FirebaseDatabase.getInstance().getReference("resultados")

        resultadosRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val resultado = snapshot.getValue(Resultado::class.java)
                resultado?.let {
                    Log.d("DatabaseDebug", "Resultado detectado: ${it.partido_id}")

                    if (it.partido_id.isBlank()) {
                        Log.d("DatabaseDebug", "Resultado ignorado por falta de partido_id")
                        return
                    }

                    // Consultar el torneo_id correspondiente al partido_id en la colección "partidos"
                    FirebaseDatabase.getInstance().getReference("partidos/${it.partido_id}/torneo_id")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val torneoId = dataSnapshot.getValue(String::class.java)
                                if (torneoId.isNullOrBlank()) {
                                    Log.d("DatabaseDebug", "Resultado ignorado por falta de torneo_id en partido: ${it.partido_id}")
                                    return
                                }

                                // Verificación adicional: ¿está el torneo_id en userTorneosIds?
                                if (torneoId !in userTorneosIds) {
                                    Log.d("DatabaseDebug", "Resultado ignorado: torneo_id $torneoId no pertenece al usuario con userUid $userUid")
                                    return
                                }

                                Log.d("DatabaseDebug", "Torneo ID del partido obtenido: $torneoId")
                                Log.d("DatabaseDebug", "IDs de torneos del usuario: $userTorneosIds")
                                Log.d("DatabaseDebug", "IDs de resultados existentes: $existingResultadosIds")

                                // Verificar si el torneo_id está en la lista de torneos del usuario
                                if (torneoId in userTorneosIds && it.partido_id !in existingResultadosIds) {
                                    Log.d("DatabaseDebug", "Nuevo resultado válido detectado: ${it.partido_id}")
                                    showResultadoNotification(it)
                                    existingResultadosIds.add(it.partido_id)
                                } else {
                                    Log.d("DatabaseDebug", "Resultado ignorado: ${it.partido_id} - Ya existente o fuera de los torneos del usuario")
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.e("DatabaseDebug", "Error obteniendo torneo_id: ${databaseError.message}")
                            }
                        })
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val resultado = snapshot.getValue(Resultado::class.java)
                resultado?.let {
                    if (it.partido_id.isNotBlank()) {
                        FirebaseDatabase.getInstance().getReference("partidos/${it.partido_id}/torneo_id")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val torneoId = dataSnapshot.getValue(String::class.java)
                                    if (torneoId != null && torneoId in userTorneosIds) {
                                        Log.d("DatabaseDebug", "Resultado modificado detectado: ${it.partido_id}")
                                        showResultadoNotification(it)
                                    } else {
                                        Log.d("DatabaseDebug", "Resultado ignorado: ${it.partido_id} - El torneo no pertenece al usuario")
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.e("DatabaseDebug", "Error obteniendo torneo_id: ${databaseError.message}")
                                }
                            })
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseDebug", "Error en el listener de Firebase: ${error.message}")
            }
        })
    }


    private fun showResultadoNotification(resultado: Resultado) {
        val notificationId = resultado.partido_id.hashCode()
        val numeroJornada = resultado.numero_jornada?.toString() ?: "N/A"
        val golesLocal = (resultado.goles_local as? Int) ?: resultado.goles_local.toString().toIntOrNull() ?: 0
        val golesVisitante = (resultado.goles_visitante as? Int) ?: resultado.goles_visitante.toString().toIntOrNull() ?: 0

        // Referencia para obtener el partido con el ID `partido_id`
        val partidoRef = FirebaseDatabase.getInstance().getReference("partidos/${resultado.partido_id}")

        partidoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(partidoSnapshot: DataSnapshot) {
                val equipoLocalId = partidoSnapshot.child("equipo_local").getValue(String::class.java)
                val equipoVisitanteId = partidoSnapshot.child("equipo_visitante").getValue(String::class.java)

                if (equipoLocalId.isNullOrBlank() || equipoVisitanteId.isNullOrBlank()) {
                    Log.e("DatabaseDebug", "Los IDs de equipo_local o equipo_visitante están vacíos.")
                    return
                }

                // Variables para almacenar los nombres de los equipos
                var nombreEquipoLocal = "Equipo Local"
                var nombreEquipoVisitante = "Equipo Visitante"

                // Referencias para los nombres de los equipos en la colección "equipos"
                val equipoLocalRef = FirebaseDatabase.getInstance().getReference("equipos/$equipoLocalId/nombre_equipo")
                val equipoVisitanteRef = FirebaseDatabase.getInstance().getReference("equipos/$equipoVisitanteId/nombre_equipo")

                Log.d("DatabaseDebug", "Referencia para equipo local: equipos/$equipoLocalId/nombre_equipo")
                Log.d("DatabaseDebug", "Referencia para equipo visitante: equipos/$equipoVisitanteId/nombre_equipo")

                equipoLocalRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(equipoLocalSnapshot: DataSnapshot) {
                        nombreEquipoLocal = equipoLocalSnapshot.getValue(String::class.java) ?: "Equipo Local"
                        Log.d("DatabaseDebug", "Nombre obtenido para equipo local: $nombreEquipoLocal")

                        equipoVisitanteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(equipoVisitanteSnapshot: DataSnapshot) {
                                nombreEquipoVisitante = equipoVisitanteSnapshot.getValue(String::class.java) ?: "Equipo Visitante"
                                Log.d("DatabaseDebug", "Nombre obtenido para equipo visitante: $nombreEquipoVisitante")

                                // Construir y mostrar la notificación con los nombres de los equipos
                                val builder = NotificationCompat.Builder(context, "ResultadosChannel")
                                    .setSmallIcon(R.drawable.notificationres)
                                    .setContentTitle("$nombreEquipoLocal $golesLocal - $golesVisitante $nombreEquipoVisitante\n")
                                    .setContentText("Revisa el nuevo resultado del partido")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setAutoCancel(true)

                                val notificationManager = NotificationManagerCompat.from(context)
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    Log.e("NotificationDebug", "Permiso de notificaciones no concedido")
                                    return
                                }
                                notificationManager.notify(notificationId, builder.build())
                                Log.d("NotificationDebug", "Notificación de resultado enviada para el partido ID: ${resultado.partido_id}")
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.e("DatabaseDebug", "Error obteniendo nombre del equipo visitante: ${databaseError.message}")
                            }
                        })
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("DatabaseDebug", "Error obteniendo nombre del equipo local: ${databaseError.message}")
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("DatabaseDebug", "Error obteniendo detalles del partido: ${databaseError.message}")
            }
        })
    }





    fun createResultadosNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Resultados Channel"
            val descriptionText = "Canal para notificaciones de resultados de partidos"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("ResultadosChannel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationDebug", "Canal de notificación creado")
        }
    }
}