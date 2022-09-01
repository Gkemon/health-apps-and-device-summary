package com.gk.emon.allhealthappssummary.data

import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.gk.emon.allhealthappssummary.data.base.BluetoothBaseDataSource
import com.gk.emon.allhealthappssummary.data.base.BluetoothBaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppBluetoothRepository @Inject constructor(
    private val bluetoothBaseRepository:
    BluetoothBaseDataSource
) :
    BluetoothBaseRepository {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun getPairedDevices(activity: ComponentActivity): Flow<Result<List<BluetoothDevice>>> {
        return bluetoothBaseRepository.getPairedDevices(activity)
    }

    override fun getUnpairDevice(activity: ComponentActivity): Flow<Result<BluetoothDevice>> {
        return bluetoothBaseRepository.getUnpairDevice(activity)
    }

}