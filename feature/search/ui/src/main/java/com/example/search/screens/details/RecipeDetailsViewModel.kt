package com.example.search.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.use_case.DeleteRecipeUseCase
import com.example.search.domain.use_case.GetRecipeDetailsUseCase
import com.example.search.domain.use_case.InsertRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val insertRecipeUseCase: InsertRecipeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetails.UiState())
    val uiState: StateFlow<RecipeDetails.UiState> get() = _uiState.asStateFlow()

    private val _navigation = Channel<RecipeDetails.Navigation>()
    val navigation: Flow<RecipeDetails.Navigation> get() = _navigation.receiveAsFlow()

    fun onEvent(event: RecipeDetails.Event) {
        when (event) {
            is RecipeDetails.Event.FetchRecipeDetails -> {
                recipeDetails(event.id)
            }

            RecipeDetails.Event.GoToRecipeListScreen -> viewModelScope.launch {
                _navigation.send(RecipeDetails.Navigation.GoToRecipeListScreen)
            }

            is RecipeDetails.Event.DeleteRecipe -> {
                deleteRecipeUseCase.invoke(event.recipeDetails.toRecipe())
                    .launchIn(viewModelScope)
            }

            is RecipeDetails.Event.InsertRecipe -> {
                insertRecipeUseCase.invoke(event.recipeDetails.toRecipe())
                    .launchIn(viewModelScope)
            }

            is RecipeDetails.Event.GoToMediaPlayer -> {
                viewModelScope.launch {
                    _navigation.send(RecipeDetails.Navigation.GoToMediaPlayer(event.youtubeUrl))
                }
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

fun com.example.search.domain.model.RecipeDetails.toRecipe(): Recipe {
    return Recipe(
        idMeal,
        strMeal,
        strCategory,
        strArea,
        strInstructions,
        strMealThumb,
        strYoutube,
        strInstructions
    )
}

object RecipeDetails {

    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: com.example.search.domain.model.RecipeDetails? = null
    )

    sealed interface Navigation {
        data object GoToRecipeListScreen : Navigation
        data class GoToMediaPlayer(val youtubeUrl: String) : Navigation
    }

    sealed interface Event {
        data class FetchRecipeDetails(val id: String) : Event

        data class InsertRecipe(val recipeDetails: com.example.search.domain.model.RecipeDetails) :
            Event

        data class DeleteRecipe(val recipeDetails: com.example.search.domain.model.RecipeDetails) :
            Event

        data object GoToRecipeListScreen : Event

        data class GoToMediaPlayer(val youtubeUrl: String) : Event
    }
}