package com.gk.emon.allhealthappssummary.di.module

import android.content.Context
import com.gk.emon.allhealthappssummary.data.AppGoogleFitRepository
import com.gk.emon.allhealthappssummary.data.base.GoogleFitBaseDataSource
import com.gk.emon.allhealthappssummary.data.base.GoogleFitBaseRepository
import com.gk.emon.allhealthappssummary.data.remote.GoogleFitRemoteDataSource
import com.gk.emon.allhealthappssummary.di.IoDispatcher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.result.DataReadResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteGoogleFitDataSource

@Module
@InstallIn(SingletonComponent::class)
object GoogleFitConfigModule {
    @Provides
    fun provideGoogleFitnessOptions(): FitnessOptions {
        return FitnessOptions.builder()
            /*Steps related data*/
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_CADENCE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            /*Steps related data*/
            /*Heart related data*/
            .addDataType(DataType.AGGREGATE_HEART_POINTS, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
            /*Heart related data*/
            .build()
    }

    @Provides
    fun provideGoogleAccount(
        @ApplicationContext context: Context,
        fitnessOptions: FitnessOptions
    ) = GoogleSignIn.getAccountForExtension(context, fitnessOptions)
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    @RemoteGoogleFitDataSource
    fun provideGoogleFitRemoteDataSource(
        @ApplicationContext applicationContext: Context,
        googleSignInAccount: GoogleSignInAccount
    )
            : GoogleFitBaseDataSource<DataReadResponse> =
        GoogleFitRemoteDataSource(applicationContext, googleSignInAccount)
}


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideGoogleFitRepository(
        @RemoteGoogleFitDataSource remoteDataSource: GoogleFitBaseDataSource<DataReadResponse>,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): GoogleFitBaseRepository<DataReadResponse> {
        return AppGoogleFitRepository(remoteDataSource, ioDispatcher)
    }
}