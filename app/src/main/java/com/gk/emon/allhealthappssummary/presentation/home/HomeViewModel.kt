package com.gk.emon.allhealthappssummary.presentation.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.emon.allhealthappssummary.domain.CheckIsGoogleSignInUseCase
import com.gk.emon.allhealthappssummary.domain.GetPairDevicesUseCase
import com.gk.emon.allhealthappssummary.utils.Async
import com.gk.emon.allhealthappssummary.utils.WhileUiSubscribed
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


data class HomeUiState(
    var isGoogleFitConnected: Boolean = false,
    val isGoogleSignInSuccess: Boolean = false,
    val isGoogleSignInFailed: Boolean = false,
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val upPairedDevices: List<BluetoothDevice> = emptyList(),
    var errorMsg: String? = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    googleAccSignInCheck: CheckIsGoogleSignInUseCase,
    private val getPairDevicesUseCase: GetPairDevicesUseCase,
) :
    ViewModel() {

    private var pairedDevices = MutableStateFlow<Result<List<BluetoothDevice>>>(
        Result.success(
            emptyList()
        )
    )
    val uiState: StateFlow<HomeUiState> = combine(
        googleAccSignInCheck.isGoogleFitConnected(),
        pairedDevices
    ) { isConnected, result ->
        when (result.isSuccess) {
            true -> HomeUiState(
                isGoogleFitConnected = isConnected,
                pairedDevices = result.getOrNull() ?: emptyList()
            )
            false -> HomeUiState(
                isGoogleFitConnected = isConnected,
                errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
            )
        }
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
            .map { data -> produceUiState(data) }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = HomeUiState(pairedDevices = emptyList())
            ).collect()

    }

    private suspend fun produceUiState(dataLoad: Async<Result<List<BluetoothDevice>>>) {
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
