package com.gk.emon.allhealthappssummary.data.local

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.gk.emon.allhealthappssummary.data.base.BluetoothBaseDataSource
import com.gk.emon.allhealthappssummary.utils.parcelable
import com.gk.emon.allhealthappssummary.utils.tryOffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
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

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun getPairedDevices(activity: ComponentActivity): Flow<Result<List<BluetoothDevice>>> {
        return (callbackFlow<Result<List<BluetoothDevice>>> {
            while (true) {
                if (isBluetoothActive(activity)) {
                    bluetoothAdapter.startDiscovery()
                    delay(refreshIntervalMs)
                    tryOffer(Result.success(bluetoothAdapter.bondedDevices.toList()))
                    delay(refreshIntervalMs)
                } else {
                    val scan =
                        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
                        { result: ActivityResult ->
                            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                                tryOffer(Result.success(bluetoothAdapter.bondedDevices.toList()))
                            } else {
                                tryOffer(Result.failure(Exception("No permission given for bluetooth")))
                            }
                        }
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    scan.launch(enableBtIntent)
                }
                awaitClose { }
            }
        }).catch {
            Result.failure<Throwable>(it)
        }.flowOn(Dispatchers.Main)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun isBluetoothActive(activity: Activity) = (ActivityCompat.checkSelfPermission(
        activity,
        Manifest.permission.BLUETOOTH_CONNECT
    ) == PackageManager.PERMISSION_GRANTED
            && bluetoothAdapter.isEnabled)

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
            Result.failure<Throwable>(it)
        }.flowOn(Dispatchers.Main)
    }


}