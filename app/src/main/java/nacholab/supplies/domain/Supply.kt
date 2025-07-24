package nacholab.supplies.domain

data class Supply(
    val id: String,
    val name: String,
    val requiredStock: Int,
    val currentStock: Int,
    val locationAtHome: String,
    val locationAtMarket: String
)