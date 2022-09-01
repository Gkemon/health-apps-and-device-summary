package com.gk.emon.allhealthappssummary.presentation.home

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import com.gk.emon.allhealthappssummary.domain.CheckIsGoogleSignInUseCase
import com.gk.emon.allhealthappssummary.domain.GetPairDevicesUseCase
import com.gk.emon.allhealthappssummary.domain.GetUnPairedDeviceUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


data class HomeUiState(
    var isGoogleFitConnected: Boolean = false,
    val isGoogleSignInSuccess: Boolean = false,
    val isGoogleSignInFailed: Boolean = false,
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val upPairedDevices: List<BluetoothDevice> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    googleAccSignInCheck: CheckIsGoogleSignInUseCase
) :
    ViewModel() {
    val uiState: StateFlow<HomeUiState> = MutableStateFlow(
        HomeUiState(
            isGoogleFitConnected =
            googleAccSignInCheck.isGoogleFitConnected()
        )
    )

    @Inject
    lateinit var  getUnPairedDeviceUseCase: GetUnPairedDeviceUseCase
    @Inject
    lateinit var  getPairDevicesUseCase: GetPairDevicesUseCase

    @Inject
    lateinit var getGoogleAccount: GoogleSignInAccount

    @Inject
    lateinit var fitnessOptions: FitnessOptions


}
