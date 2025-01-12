package com.example.search.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.use_case.GetRecipeDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetails.UiState())
    val uiState: StateFlow<RecipeDetails.UiState> get() = _uiState.asStateFlow()

    fun onEvent(event: RecipeDetails.Event) {
        when (event) {
            is RecipeDetails.Event.FetchRecipeDetails -> {
                recipeDetails(event.id)
            }
        }
    }

    private fun recipeDetails(id: String) = getRecipeDetailsUseCase.invoke(id)
        .onEach { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    _uiState.update {
                        RecipeDetails.UiState(isLoading = true)
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update {
                        RecipeDetails.UiState(error = UiText.RemoteString(message = result.message.toString()))
                    }
                }

                is NetworkResult.Success -> {
                    _uiState.update { RecipeDetails.UiState(data = result.data) }
                }
            }
        }.launchIn(viewModelScope)
}

object RecipeDetails {

    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: com.example.search.domain.model.RecipeDetails? = null
    )

    sealed interface Navigation

    sealed interface Event {
        data class FetchRecipeDetails(val id: String) : Event
    }
}