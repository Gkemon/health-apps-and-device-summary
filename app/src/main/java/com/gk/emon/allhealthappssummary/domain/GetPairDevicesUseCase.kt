package com.gk.emon.allhealthappssummary.domain

import android.bluetooth.BluetoothDevice
import androidx.activity.ComponentActivity
import com.gk.emon.allhealthappssummary.data.base.BluetoothBaseRepository
import com.gk.emon.allhealthappssummary.domain.baseUseCase.FlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPairDevicesUseCase @Inject constructor(
    private val bluetoothRepository: BluetoothBaseRepository,
) :
    FlowUseCase<ComponentActivity, List<BluetoothDevice>>(Dispatchers.Default) {
    public override fun execute(parameters: ComponentActivity): Flow<Result<List<BluetoothDevice>>> {
        return bluetoothRepository.getPairedDevices(ComponentActivity() )
    }
}