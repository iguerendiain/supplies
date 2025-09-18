package nacholab.supplies.storage

import nacholab.supplies.domain.Supply
import nacholab.supplies.network.model.SupplyAPI
import nacholab.supplies.storage.db.model.SupplyDB
import java.util.UUID

object SupplyMapper {

    fun buildFrom(supplyDB: SupplyDB) = Supply(
        id = UUID.randomUUID().toString(),
        name = supplyDB.name,
        requiredStock = supplyDB.requiredCount,
        currentStock = supplyDB.stock,
        locationAtHome = supplyDB.homeLocation,
        locationAtMarket = supplyDB.marketLocation
    )

    fun buildFrom(supply: Supply) = SupplyDB(
        name = supply.name,
        requiredCount = supply.requiredStock,
        stock = supply.currentStock,
        homeLocation = supply.locationAtHome,
        marketLocation = supply.locationAtMarket
    )

    fun buildFrom(supply: SupplyAPI) = Supply(
        id = supply.id,
        name = supply.name,
        requiredStock = supply.requiredStock,
        currentStock = supply.currentStock,
        locationAtHome = supply.homeLocation,
        locationAtMarket = supply.marketLocation,
    )
}