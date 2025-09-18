package nacholab.supplies.domain

import nacholab.supplies.network.model.SessionAPI
import nacholab.supplies.network.model.SupplyAPI

interface NetworkRespository {
    suspend fun getCurrentSession(): SessionAPI
    suspend fun getMySupplies(): List<SupplyAPI>
    suspend fun saveMySupplies(supplies: List<SupplyAPI>)
    suspend fun authenticateUser(email: String): Boolean
    suspend fun createSession(email: String, verificationCode: String): SessionAPI?
}