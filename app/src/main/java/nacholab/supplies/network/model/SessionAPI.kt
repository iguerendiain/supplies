package nacholab.supplies.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionAPI(
    val id: String,
    @SerialName("device_type") val deviceType: String,
    @SerialName("device_id") val deviceId: String,
    @SerialName("last_usage") val lastUsage: String,
    @SerialName("user_id") val userId: String,
)