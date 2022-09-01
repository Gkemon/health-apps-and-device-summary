package com.gk.emon.allhealthappssummary.data.local

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.gk.emon.allhealthappssummary.data.base.BluetoothBaseDataSource
import com.gk.emon.allhealthappssummary.utils.parcelable
import com.gk.emon.allhealthappssummary.utils.tryOffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BluetoothLocalDataSource @Inject constructor(
    private var bluetoothAdapter: BluetoothAdapter,
    private val refreshIntervalMs: Long = 5000
) :
    BluetoothBaseDataSource {

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun getPairedDevices(activity: ComponentActivity): Flow<Result<List<BluetoothDevice>>> {
        return (callbackFlow<Result<List<BluetoothDevice>>> {
            bluetoothAdapter.startDiscovery()
            tryOffer(Result.success(bluetoothAdapter.bondedDevices.toList()))
            awaitClose {
                bluetoothAdapter.cancelDiscovery()
            }
        }).catch {
            emit(Result.failure(it))
        }.flowOn(Dispatchers.IO)
    }


    override fun getUnpairDevice(activity: ComponentActivity): Flow<Result<BluetoothDevice>> {
        return (callbackFlow<Result<BluetoothDevice>> {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent) {
                    when (intent.action) {
                        BluetoothDevice.ACTION_FOUND -> {
                            val device: BluetoothDevice? =
                                intent.parcelable(BluetoothDevice.EXTRA_DEVICE)
                            device?.let {
                                tryOffer(Result.success(it))
                            }
                                ?: tryOffer(Result.failure(Exception("Device found but can't detach")))
                        }
                    }
                }
            }
            val intentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            activity.registerReceiver(receiver, intentFilter)
            awaitClose {
                activity.unregisterReceiver(receiver)
            }
        }).catch {
            emit(Result.failure(it))
        }.flowOn(Dispatchers.Main)
    }


}