package nacholab.supplies.storage.ram

import nacholab.supplies.domain.Supply

object RAMStorage {
    var supplies = listOf<Supply>()
    var sessionId: String = ""
}