package com.gk.emon.allhealthappssummary.presentation.home

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gk.emon.allhealthappssummary.presentation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestCodeBL: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavGraph()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
           // setupBluetooth()
        }
    }



//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun setupBluetooth() {
//        val bluetoothManager: BluetoothManager =
//            getSystemService(BluetoothManager::class.java)
//        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
//        if (bluetoothAdapter != null) {
//
//            if (ActivityCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    permissionsBL,
//                    1
//                )
//                return
//            }
//
//            if (!bluetoothAdapter.isEnabled) {
//                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                if (ActivityCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.BLUETOOTH_CONNECT
//                    ) == PackageManager.PERMISSION_GRANTED
//                ) {
//                    startActivityForResult(enableBtIntent, requestCodeBL)
//                }
//            } else {
//                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
//                pairedDevices?.forEach { device ->
//                    val deviceName = device.name
//                    val deviceHardwareAddress = device.address // MAC address
//                }
//                bluetoothAdapter.startDiscovery()
//            }
//        }
//    }




}

