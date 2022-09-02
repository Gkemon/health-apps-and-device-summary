package com.gk.emon.allhealthappssummary.presentation.huaweiHealth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.emon.allhealthappssummary.domain.GetHuaweiDataLastYearUseCase
import com.gk.emon.allhealthappssummary.utils.Async
import com.gk.emon.allhealthappssummary.utils.WhileUiSubscribed
import com.huawei.hms.hihealth.data.ActivityRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


data class HuaweiDataState(
    var isLoading: Boolean = false,
    val items: List<String> = emptyList(),
    var isEmpty: Boolean = false,
)

@HiltViewModel
class HuaweiViewModel @Inject constructor(getHuaweiDataLastYearUseCase: GetHuaweiDataLastYearUseCase) :
    ViewModel() {
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<HuaweiDataState> =
        getHuaweiDataLastYearUseCase.execute(null)
            .map { Async.Success(it) }
            .onStart<Async<Result<List<ActivityRecord>>>> { emit(Async.Loading) }
            .map { dataAsync -> produceUiState(dataAsync) }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = HuaweiDataState(isLoading = true)
            )


    private suspend fun produceUiState(dataLoad: Async<Result<List<ActivityRecord>>>) =
        when (dataLoad) {
            Async.Loading -> {
                HuaweiDataState(isLoading = true)
            }
            is Async.Success -> {
                when (dataLoad.data.isSuccess) {
                    true -> {
                        (dataLoad.data.getOrNull()?.let { generateDataTotalItems(it) }?.let {
                            HuaweiDataState(
                                isLoading = false,
                                items = it,
                                isEmpty = dataLoad.data.getOrNull() == null
                            )
                        }) ?: HuaweiDataState(
                            isLoading = false,
                            isEmpty = true
                        )
                    }
                    false -> HuaweiDataState(isLoading = false)
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

    private suspend fun generateDataTotalItems(
        dataReadResult: List<ActivityRecord>
    ): ArrayList<String> {
        val views = arrayListOf<String>()
        withContext(Dispatchers.Default) {
            if (dataReadResult.isNotEmpty()) {
                for (data in dataReadResult) {
                    data.name?.let { views.add(it) }
                }
            }
        }
        return views;

    }


}

