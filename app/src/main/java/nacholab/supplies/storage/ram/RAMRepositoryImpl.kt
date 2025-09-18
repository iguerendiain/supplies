package nacholab.supplies.storage.ram

import nacholab.supplies.domain.RAMRepository
import nacholab.supplies.domain.Supply

class RAMRepositoryImpl: RAMRepository {

    override fun getAllSupplies(): List<Supply> {
        return RAMStorage.supplies
    }

    override fun storeAllSupplies(supplies: List<Supply>) {
        RAMStorage.supplies = supplies
    }

    override fun getSessionId(): String {
        return RAMStorage.sessionId
    }

    override fun storeSessionId(sessionId: String) {
        RAMStorage.sessionId = sessionId
    }

}