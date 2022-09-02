package com.gk.emon.allhealthappssummary.presentation.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.emon.allhealthappssummary.domain.CheckIsGoogleSignInUseCase
import com.gk.emon.allhealthappssummary.domain.GetPairDevicesUseCase
import com.gk.emon.allhealthappssummary.domain.GetUnPairedDeviceUseCase
import com.gk.emon.allhealthappssummary.utils.Async
import com.gk.emon.allhealthappssummary.utils.WhileUiSubscribed
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    var isGoogleFitConnected: Boolean = false,
    val isGoogleSignInSuccess: Boolean = false,
    val isGoogleSignInFailed: Boolean = false,
    val pairedDevices: MutableList<BluetoothDevice> = mutableListOf(),
    val unPairedDevices: MutableList<BluetoothDevice> = mutableListOf(),
    var errorMsg: String? = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    googleAccSignInCheck: CheckIsGoogleSignInUseCase,
    private val getPairDevicesUseCase: GetPairDevicesUseCase,
    private val getUnPairedDeviceUseCase: GetUnPairedDeviceUseCase
) :
    ViewModel() {

    private var pairedDevices = MutableStateFlow<Result<List<BluetoothDevice>>>(
        Result.success(
            emptyList()
        )
    )
    private var unPairedDevices = MutableStateFlow<Result<MutableList<BluetoothDevice>>>(
        Result.success(
            mutableListOf()
        )
    )
    var isGoogleFitConnected = MutableStateFlow(googleAccSignInCheck.isGoogleFitConnected())
    val uiState: StateFlow<HomeUiState> = combine(
        isGoogleFitConnected,
        pairedDevices,
        unPairedDevices
    ) { isConnected, pairedDevices, unPairedDevices ->
        HomeUiState(
            isGoogleFitConnected = isConnected,
            pairedDevices = pairedDevices.getOrNull()?.toMutableList() ?: mutableListOf(),
            unPairedDevices = unPairedDevices.getOrNull()?.toMutableList() ?: mutableListOf(),
        )
    }.catch {
        HomeUiState(errorMsg = it.localizedMessage)
    }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = HomeUiState()
        )


    @Inject
    lateinit var getGoogleAccount: GoogleSignInAccount

    @Inject
    lateinit var bluetoothAdapter: BluetoothAdapter

    @Inject
    lateinit var fitnessOptions: FitnessOptions

    suspend fun fetchPairedDevices(activity: ComponentActivity) {
        getPairDevicesUseCase.execute(activity)
            .map {
                Async.Success(it)
            }
            .onStart<Async<Result<List<BluetoothDevice>>>> { emit(Async.Loading) }
            .map { data -> producePairedDeviceUiState(data) }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = HomeUiState(pairedDevices = mutableListOf())
            ).collect()
    }

    suspend fun fetchUnPairedDevices(activity: ComponentActivity) {
        getUnPairedDeviceUseCase.execute(activity)
            .map {
                Async.Success(it)
            }
            .onStart<Async<Result<BluetoothDevice>>> { emit(Async.Loading) }
            .map { data -> produceUnpairedDeviceUiState(data) }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = HomeUiState(unPairedDevices = mutableListOf())
            ).collect()
    }

    fun refresh() {
        viewModelScope.launch {
            uiState.collect()
        }
    }

    private suspend fun produceUnpairedDeviceUiState(dataLoad: Async<Result<BluetoothDevice>>) {
        if (dataLoad is Async.Success) {
            when (dataLoad.data.isSuccess) {
                true -> {
                    val result = unPairedDevices.value.getOrNull() ?: mutableListOf()
                    dataLoad.data.getOrNull()?.let {
                        result.add(it)
                        unPairedDevices.emit(Result.success(result))
                    }
                }
                false -> unPairedDevices.emit(
                    Result.failure(
                        dataLoad.data.exceptionOrNull() ?: Throwable("Unknown error")
                    )
                )
            }
        }
    }

    private suspend fun producePairedDeviceUiState(dataLoad: Async<Result<List<BluetoothDevice>>>) {
        if (dataLoad is Async.Success) {
            when (dataLoad.data.isSuccess) {
                true -> {
                    pairedDevices.emit(Result.success(dataLoad.data.getOrNull() ?: emptyList()))
                }
                false -> pairedDevices.emit(
                    Result.failure(
                        dataLoad.data.exceptionOrNull() ?: Throwable("Unknown error")
                    )
                )
            }
        }
    }


}
