package nacholab.supplies.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.ktor.client.HttpClient
import nacholab.supplies.domain.DBRepository
import nacholab.supplies.domain.NetworkRespository
import nacholab.supplies.domain.RAMRepository
import nacholab.supplies.domain.PreferencesRepository
import nacholab.supplies.network.KtorClientBuilder
import nacholab.supplies.network.NetworkRepositoryImpl
import nacholab.supplies.storage.datastore.DataStoreBuilder
import nacholab.supplies.storage.datastore.PreferencesRepositoryImpl
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
    single<DataStore<Preferences>> { DataStoreBuilder.createDataStore(get()) }
    singleOf(::PreferencesRepositoryImpl) { bind<PreferencesRepository>() }
    singleOf(::NetworkRepositoryImpl) { bind<NetworkRespository>() }
    single<HttpClient> { KtorClientBuilder.buildKtorClient() }
    viewModelOf(::MainViewModel)
}
