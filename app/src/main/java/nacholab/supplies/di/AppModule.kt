package nacholab.supplies.di

import nacholab.supplies.domain.DBRepository
import nacholab.supplies.domain.RAMRepository
import nacholab.supplies.ui.MainViewModel
import nacholab.supplies.storage.db.DBRepositoryImpl
import nacholab.supplies.storage.ram.RAMRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val diAppModule = module {
    singleOf(::DBRepositoryImpl) { bind<DBRepository>() }
    singleOf(::RAMRepositoryImpl) { bind<RAMRepository>() }
    viewModelOf(::MainViewModel)
}
