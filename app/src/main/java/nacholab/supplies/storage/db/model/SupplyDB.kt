package nacholab.supplies.storage.db.model

import kotlinx.serialization.Serializable

@Serializable
data class SupplyDB(
    val name: String,
    val requiredCount: Int,
    val stock: Int,
    val homeLocation: String,
    val marketLocation: String
)