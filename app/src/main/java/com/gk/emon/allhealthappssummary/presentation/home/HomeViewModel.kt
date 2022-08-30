package com.gk.emon.allhealthappssummary.presentation.home

import androidx.lifecycle.ViewModel
import com.gk.emon.allhealthappssummary.domain.CheckIsGoogleSignInUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


data class HomeUiState(
    val isGoogleFitConnected: Boolean = false,
    val isGoogleSignInSuccess: Boolean = false,
    val isGoogleSignInFailed:Boolean = false,
)

@HiltViewModel
class HomeViewModel  @Inject constructor(googleAccSignInCheck: CheckIsGoogleSignInUseCase) : ViewModel() {
    val uiState:StateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    @Inject lateinit var  getGoogleAccount: GoogleSignInAccount
    @Inject lateinit var  fitnessOptions: FitnessOptions
}

enum class FitActionRequestCode {
    READ_DATA,
    WRITE_DATA
}
