package nacholab.supplies.domain

interface RAMRepository {
    fun getAllSupplies(): List<Supply>
    fun storeAllSupplies(supplies: List<Supply>)
}