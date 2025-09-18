package nacholab.supplies.domain

interface PreferencesRepository{

    suspend fun getSessionId(): String
    suspend fun storeSessionId(sessionId: String)

}