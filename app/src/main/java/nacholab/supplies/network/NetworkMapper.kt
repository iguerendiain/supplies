package nacholab.supplies.network

import nacholab.supplies.domain.Supply
import nacholab.supplies.network.model.SupplyAPI

object NetworkMapper {

    fun buildFrom(supply: Supply) = SupplyAPI(
        id = supply.id,
        name = supply.name,
        requiredStock = supply.requiredStock,
        currentStock = supply.currentStock,
        homeLocation = supply.locationAtHome,
        marketLocation = supply.locationAtMarket,
        userId = ""
    )

}