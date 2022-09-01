package com.gk.emon.allhealthappssummary.di.module

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.gk.emon.allhealthappssummary.data.AppBluetoothRepository
import com.gk.emon.allhealthappssummary.data.base.BluetoothBaseDataSource
import com.gk.emon.allhealthappssummary.data.base.BluetoothBaseRepository
import com.gk.emon.allhealthappssummary.data.local.BluetoothLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalBluetoothDataSource

@Module
@InstallIn(SingletonComponent::class)
object BluetoothManagerModule {
    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager {
        return context.getSystemService(BluetoothManager::class.java)
    }

    @Provides
    @Singleton
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager): BluetoothAdapter {
        return bluetoothManager.adapter
    }
}

@Module
@InstallIn(SingletonComponent::class)
object BluetoothDataSourceModule {
    @Provides
    @Singleton
    @LocalBluetoothDataSource
    fun provideBluetoothLocalDataSource(
        bluetoothAdapter: BluetoothAdapter
    ): BluetoothBaseDataSource =
        BluetoothLocalDataSource(bluetoothAdapter)
}

@Module
@InstallIn(SingletonComponent::class)
object BluetoothRepositoryModule {
    @Singleton
    @Provides
    fun provideBluetoothRepository(
        @LocalBluetoothDataSource
        localBluetoothDataSource: BluetoothBaseDataSource,
    ): BluetoothBaseRepository {
        return AppBluetoothRepository(localBluetoothDataSource)
    }
}
