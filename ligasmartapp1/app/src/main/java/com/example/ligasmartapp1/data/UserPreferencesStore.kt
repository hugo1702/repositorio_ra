package com.example.ligasmartapp1.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferencesStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")
        private val USER_UID_KEY = stringPreferencesKey("user_uid")
    }

    // Guardar el UID del usuario
    suspend fun saveUserUid(uid: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_UID_KEY] = uid
        }
    }

    // Obtener el UID del usuario de forma suspendida
    suspend fun getUserUid(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_UID_KEY]
    }

    // Limpiar el UID (útil para logout)
    suspend fun clearUserUid() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_UID_KEY)
        }
    }
}
