package nacholab.supplies.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupplyAPI(
    val id: String,
    val name: String,
    @SerialName("required_stock") val requiredStock: Int,
    @SerialName("current_stock") val currentStock: Int,
    @SerialName("home_location") val homeLocation: String,
    @SerialName("market_location") val marketLocation: String,
    @SerialName("user_id") val userId: String,
)