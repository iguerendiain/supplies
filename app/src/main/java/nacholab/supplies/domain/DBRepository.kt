package nacholab.supplies.domain

import nacholab.supplies.storage.db.model.SupplyDB
import java.io.File

interface DBRepository {
    suspend fun loadCurrentDB(): List<SupplyDB>
    suspend fun clearDB()
    suspend fun updateDB(consumables: List<SupplyDB>)
    suspend fun exportToJSON(): String
    suspend fun importFromFile(db: File)
}