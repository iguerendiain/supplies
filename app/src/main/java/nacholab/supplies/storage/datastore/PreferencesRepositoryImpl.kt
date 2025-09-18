package nacholab.supplies.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last
import nacholab.supplies.domain.PreferencesRepository

class PreferencesRepositoryImpl(private val datastore : DataStore<Preferences>): PreferencesRepository {

    companion object{
        val SESSIONID = stringPreferencesKey("session_id")
    }


    override suspend fun getSessionId() = datastore.data.firstOrNull()?.get(SESSIONID)?:""

    override suspend fun storeSessionId(sessionId: String) {
        datastore.edit {
            it[SESSIONID] = sessionId
        }
    }
}