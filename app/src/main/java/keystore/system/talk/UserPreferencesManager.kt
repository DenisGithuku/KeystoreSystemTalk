package keystore.system.talk

import android.content.Context
import android.preference.Preference
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class UserPreferencesManager(
    private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATA)

    companion object {
        private const val USER_DATA: String = "user_data"
    }

   suspend fun <T>update(key: Preferences.Key<T>, value: T) {
       context.dataStore.edit { it[key] = value }
   }

    suspend fun <T>remove(key: Preferences.Key<T>) {
        context.dataStore.edit { it.remove(key) }
    }

    val accessToken = context.dataStore.data.mapLatest {
        it[PreferenceKey.accessToken]
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val apiKey = context.dataStore.data.mapLatest {
        it[PreferenceKey.apiKey]
    }

    val serverUrl = context.dataStore.data.mapLatest {
        it[PreferenceKey.serverUrl]
    }
}

object PreferenceKey {
    val email = stringPreferencesKey("email")
    val accessToken = stringPreferencesKey("access_token")
    val userId = intPreferencesKey("user_id")
    val serverUrl = stringPreferencesKey("server_url")
    val apiKey = stringPreferencesKey("api_key")
}