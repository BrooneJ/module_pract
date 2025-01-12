package com.example.search.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.use_case.GetAllRecipeFromLocalDbUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteViewModel @Inject constructor(private val getAllRecipeFromLocalDbUseCase: GetAllRecipeFromLocalDbUseCase) :
    ViewModel() {

    private var originalList = mutableListOf<Recipe>()

    private val _uiState = MutableStateFlow(FavoriteScreen.UiState())
    val uiState: StateFlow<FavoriteScreen.UiState> get() = _uiState.asStateFlow()

    private val _navigation = Channel<FavoriteScreen.Navigation>()
    val navigation: Flow<FavoriteScreen.Navigation> = _navigation.receiveAsFlow()

    init {
        getRecipeList()
    }

    fun onEvent(event: FavoriteScreen.Event) {
        when (event) {
            FavoriteScreen.Event.AlphabeticalSort -> alphabeticallySort()
            FavoriteScreen.Event.LessIngredientsSort -> lessIngredientsSort()
            FavoriteScreen.Event.ResetSort -> resetSort()
            is FavoriteScreen.Event.ShowDetails -> viewModelScope.launch {
                _navigation.send(FavoriteScreen.Navigation.GoToRecipeDetailsScreen(event.id))
            }
        }
    }

    private fun getRecipeList() = viewModelScope.launch {
        getAllRecipeFromLocalDbUseCase.invoke().collectLatest { list ->
            originalList = list.toMutableList()
            _uiState.update { FavoriteScreen.UiState(data = list) }
        }
    }

    private fun alphabeticallySort() =
        _uiState.update { FavoriteScreen.UiState(data = originalList.sortedBy { it.strMeal }) }

    private fun lessIngredientsSort() =
        _uiState.update { FavoriteScreen.UiState(data = originalList.sortedBy { it.strInstructions.length }) }

    private fun resetSort() {
        _uiState.update { FavoriteScreen.UiState(data = originalList) }
    }
}

object FavoriteScreen {
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: List<Recipe>? = null
    )

    sealed interface Navigation {
        data class GoToRecipeDetailsScreen(val id: String) : Navigation
    }

    sealed interface Event {
        data object AlphabeticalSort : Event
        data object LessIngredientsSort : Event
        data object ResetSort : Event
        data class ShowDetails(val id: String) : Event
    }
}