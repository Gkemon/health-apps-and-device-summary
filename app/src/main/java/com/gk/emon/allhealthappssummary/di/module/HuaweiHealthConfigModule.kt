package com.gk.emon.allhealthappssummary.di.module

import android.content.Context
import com.huawei.hms.hihealth.ActivityRecordsController
import com.huawei.hms.hihealth.HuaweiHiHealth
import com.huawei.hms.hihealth.SettingController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteHuaweiDataSource

@Module
@InstallIn(SingletonComponent::class)
object HuaweiHealthConfigModule {
    @Provides
    @Singleton
    fun provideHuaweiSettingController(@ApplicationContext context: Context): SettingController =
        HuaweiHiHealth.getSettingController(context)

    @Provides
    @Singleton
    fun provideActivityRecordsController(@ApplicationContext context: Context): ActivityRecordsController =
        HuaweiHiHealth.getActivityRecordsController(context)
}


