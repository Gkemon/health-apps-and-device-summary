package com.gk.emon.allhealthappssummary.presentation.googleFit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gk.emon.allhealthappssummary.domain.GetGoogleFitDataLastYearUseCase
import com.gk.emon.allhealthappssummary.utils.Async
import com.gk.emon.allhealthappssummary.utils.WhileUiSubscribed
import com.gk.emon.allhealthappssummary.utils.getEndTimeString
import com.gk.emon.allhealthappssummary.utils.getStartTimeString
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.result.DataReadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


data class GoogleFitDataState(
    var isLoading: Boolean = false,
    val items: List<String> = emptyList(),
    var isEmpty: Boolean = items.isEmpty(),
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


    private suspend fun produceUiState(dataLoad: Async<Result<DataReadResponse>>) =
        when (dataLoad) {
            Async.Loading -> {
                GoogleFitDataState(isLoading = true)
            }
            is Async.Success -> {
                when (dataLoad.data.isSuccess) {
                    true -> {
                        (dataLoad.data.getOrNull()?.let { generateDataTotalItems(it) }?.let {
                            GoogleFitDataState(
                                isLoading = false,
                                items = it,
                                isEmpty = dataLoad.data.getOrNull() == null
                            )
                        }) ?: GoogleFitDataState(
                            isLoading = false,
                            isEmpty = true
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


    private fun generateDataItem(result: DataSet): Collection<String> {
        val views = arrayListOf<String>()
        for (dp in result.dataPoints) {
            var result =
                "\n\n<b>${
                    dp.dataType.name
                        .replace("com.google.", "")
                        .replace(".delta", "")
                        .replace(".summary", "")
                        .replace("_", " ").uppercase(Locale.ENGLISH)
                        .plus(
                            if (dp.dataType.name.lowercase().contains("step"))
                                " \uD83D\uDC63" else " ❤️"
                        )
                }</b>" +
                        "\nStart: ${dp.getStartTimeString()}" +
                        "\nEnd: ${dp.getEndTimeString()}"
            dp.dataType.fields.forEach {
                result += "\n<b>${it.name} </b> -  ${dp.getValue(it)}"
            }
            views.add(result)
        }
        return views
    }

    private suspend fun generateDataTotalItems(
        dataReadResult: DataReadResponse
    ): ArrayList<String> {
        val views = arrayListOf<String>()
        withContext(Dispatchers.Default) {
            if (dataReadResult.buckets.isNotEmpty()) {
                for (bucket in dataReadResult.buckets) {
                    bucket.dataSets.forEach {
                        it?.let { result ->
                            views.addAll(generateDataItem(result))
                        }
                    }
                }
            } else if (dataReadResult.dataSets.isNotEmpty()) {
                dataReadResult.dataSets.forEach {
                    it?.let { result ->
                        views.addAll(generateDataItem(result))
                    }
                }
            }
        }

        return views;

    }


}

