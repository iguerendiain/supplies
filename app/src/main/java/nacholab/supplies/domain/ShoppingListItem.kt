package nacholab.supplies.domain

data class ShoppingListItem(
    val consumable: Supply,
    val picked: Boolean
)