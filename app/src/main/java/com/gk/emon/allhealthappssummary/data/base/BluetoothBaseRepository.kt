package com.gk.emon.allhealthappssummary.data.base

import android.bluetooth.BluetoothDevice
import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.Flow

interface BluetoothBaseRepository {
    fun getPairedDevices(activity: ComponentActivity): Flow<Result<List<BluetoothDevice>>>
    fun getUnpairDevice(activity: ComponentActivity): Flow<Result<BluetoothDevice>>
}