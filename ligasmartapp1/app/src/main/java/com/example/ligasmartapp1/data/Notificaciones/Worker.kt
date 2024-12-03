package com.example.ligasmartapp1

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.ligasmartapp1.data.Notificaciones.Notificaciones
import com.example.ligasmartapp1.data.UserPreferencesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PartidosNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Obtener el UID del usuario desde UserPreferencesStore
        val userPreferencesStore = UserPreferencesStore(context)
        CoroutineScope(Dispatchers.IO).launch {
            val userUid = userPreferencesStore.getUserUid()
            userUid?.let { uid ->
                // Configurar y empezar a escuchar notificaciones para los partidos del usuario
                val notificaciones = Notificaciones(context, uid)
                notificaciones.createNotificationChannel()
                notificaciones.startPartidosListener()
            }
        }
        return Result.success()
    }
}
