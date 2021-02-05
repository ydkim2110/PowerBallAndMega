package com.reachfree.powerballandmega.di

import android.content.Context
import androidx.room.Room
import com.reachfree.powerballandmega.data.local.LocalDatabase
import com.reachfree.powerballandmega.data.local.PowerMegaDao
import com.reachfree.powerballandmega.data.local.repository.DefaultLocalRepository
import com.reachfree.powerballandmega.data.local.repository.LocalRepository
import com.reachfree.powerballandmega.data.remote.api.AdviceApi
import com.reachfree.powerballandmega.data.remote.api.MegaBallApi
import com.reachfree.powerballandmega.data.remote.api.PowerBallApi
import com.reachfree.powerballandmega.data.remote.repository.*
import com.reachfree.powerballandmega.utils.Constants.ADVICE_BASE_URL
import com.reachfree.powerballandmega.utils.Constants.MEGA_BALL_BASE_URL
import com.reachfree.powerballandmega.utils.Constants.POWER_BALL_BASE_URL
import com.reachfree.powerballandmega.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, LocalDatabase::class.java, "power_mega.db").build()

    @Singleton
    @Provides
    fun providePowerMegaDao(localDatabase: LocalDatabase) = localDatabase.getPowerMegaDao()

    @Singleton
    @Provides
    fun provideLocalRepository(dao: PowerMegaDao): LocalRepository =
        DefaultLocalRepository(dao)

    @Singleton
    @Provides
    fun provideAdviceApi(): AdviceApi = Retrofit.Builder()
        .baseUrl(ADVICE_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AdviceApi::class.java)

    @Singleton
    @Provides
    fun provideAdviceRepository(api: AdviceApi): AdviceRepository =
        DefaultAdviceRepository(api)

    @Singleton
    @Provides
    fun providePowerBallApi(): PowerBallApi = Retrofit.Builder()
        .baseUrl(POWER_BALL_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PowerBallApi::class.java)

    @Singleton
    @Provides
    fun providePowerBallRepository(api: PowerBallApi): PowerBallRepository =
        DefaultPowerBallRepository(api)

    @Singleton
    @Provides
    fun provideMegaBallApi(): MegaBallApi = Retrofit.Builder()
        .baseUrl(MEGA_BALL_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MegaBallApi::class.java)

    @Singleton
    @Provides
    fun provideMegaBallRepository(api: MegaBallApi): MegaBallRepository =
        DefaultMegaBallRepository(api)

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}