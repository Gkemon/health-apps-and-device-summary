package com.gk.emon.allhealthappssummary.presentation.googleFit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.emon.allhealthappssummary.domain.GetGoogleFitDataLastYearUseCase
import com.gk.emon.allhealthappssummary.utils.Async
import com.gk.emon.allhealthappssummary.utils.WhileUiSubscribed
import com.google.android.gms.fitness.result.DataReadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class GoogleFitDataState(
    var isLoading: Boolean = false,
    val item: DataReadResponse? = null,
    val isEmpty: Boolean = false,
)

@HiltViewModel
class GoogleFitDataViewModel @Inject constructor(getGoogleFitDataLastYearUseCase: GetGoogleFitDataLastYearUseCase) :
    ViewModel() {
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<GoogleFitDataState> =
            getGoogleFitDataLastYearUseCase.execute(null)
                .map { Async.Success(it) }
                .onStart<Async<Result<DataReadResponse>>> { emit(Async.Loading) }
                .map { dataAsync -> produceUiState(dataAsync) }
                .stateIn(
                    scope = viewModelScope,
                    started = WhileUiSubscribed,
                    initialValue = GoogleFitDataState(isLoading = true)
                )



    private fun produceUiState(dataLoad: Async<Result<DataReadResponse>>) =
        when (dataLoad) {
            Async.Loading -> {
                GoogleFitDataState(isLoading = true)
            }
            is Async.Success -> {
                when (dataLoad.data.isSuccess) {
                    true -> {
                        GoogleFitDataState(
                            isLoading = false,
                            item = dataLoad.data.getOrNull()
                        )
                    }
                    false -> GoogleFitDataState(isLoading = false)
                }
            }
        }

    fun refresh() {
        _isLoading.value = true
        viewModelScope.launch {
            uiState.collect()
            _isLoading.value = false
        }
    }


}

