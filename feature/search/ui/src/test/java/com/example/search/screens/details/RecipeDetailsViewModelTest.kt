package com.example.search.screens.details

import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.use_case.DeleteRecipeUseCase
import com.example.search.domain.use_case.GetRecipeDetailsUseCase
import com.example.search.domain.use_case.InsertRecipeUseCase
import com.example.search.screens.recipe_list.MainDispatcherRule
import com.example.search.screens.recipe_list.toRecipe
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RecipeDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase = mock()
    private val deleteRecipeUseCase: DeleteRecipeUseCase = mock()
    private val insertRecipeUseCase: InsertRecipeUseCase = mock()

    @Test
    fun test_success() = runTest {
        `when`(getRecipeDetailsUseCase.invoke("id"))
            .thenReturn(flowOf(NetworkResult.Success(getRecipeDetails())))

        val viewModel = RecipeDetailsViewModel(
            getRecipeDetailsUseCase = getRecipeDetailsUseCase,
            deleteRecipeUseCase = deleteRecipeUseCase,
            insertRecipeUseCase = insertRecipeUseCase
        )

        viewModel.onEvent(
            com.example.search.screens.details.RecipeDetails.Event.FetchRecipeDetails(
                "id"
            )
        )
        assertEquals(getRecipeDetails(), viewModel.uiState.value.data)
    }

    @Test
    fun test_failure() = runTest {

        `when`(getRecipeDetailsUseCase.invoke("id"))
            .thenReturn(flowOf(NetworkResult.Error("error")))

        val viewModel = RecipeDetailsViewModel(
            getRecipeDetailsUseCase = getRecipeDetailsUseCase,
            deleteRecipeUseCase = deleteRecipeUseCase,
            insertRecipeUseCase = insertRecipeUseCase
        )

        viewModel.onEvent(
            com.example.search.screens.details.RecipeDetails.Event.FetchRecipeDetails(
                "id"
            )
        )
        assertEquals(UiText.RemoteString("error"), viewModel.uiState.value.error)
    }

    @Test
    fun test_navigate_recipe_list_screen() = runTest {
        val viewModel = RecipeDetailsViewModel(
            getRecipeDetailsUseCase = getRecipeDetailsUseCase,
            deleteRecipeUseCase = deleteRecipeUseCase,
            insertRecipeUseCase = insertRecipeUseCase
        )

        viewModel.onEvent(
            com.example.search.screens.details.RecipeDetails.Event.GoToRecipeListScreen
        )

        val list = mutableListOf<com.example.search.screens.details.RecipeDetails.Navigation>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.navigation.collectLatest {
                list.add(it)
            }
        }
        assert(list.first() is com.example.search.screens.details.RecipeDetails.Navigation.GoToRecipeListScreen)
    }

    @Test
    fun test_navigate_goToMediaPlayer() = runTest {
        val viewModel = RecipeDetailsViewModel(
            getRecipeDetailsUseCase = getRecipeDetailsUseCase,
            deleteRecipeUseCase = deleteRecipeUseCase,
            insertRecipeUseCase = insertRecipeUseCase
        )

        viewModel.onEvent(
            com.example.search.screens.details.RecipeDetails.Event.GoToMediaPlayer("youtubeUrl")
        )

        val list = mutableListOf<com.example.search.screens.details.RecipeDetails.Navigation>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.navigation.collectLatest {
                list.add(it)
            }
        }
        assert(list.first() is com.example.search.screens.details.RecipeDetails.Navigation.GoToMediaPlayer)
    }

    @Test
    fun test_insert() = runTest {

        val recipeDb = mutableListOf<Recipe>()
        val recipe = getRecipeDetails().toRecipe()
        `when`(insertRecipeUseCase.invoke(recipe))
            .then {
                recipeDb.add(recipe)
                flowOf(Unit)
            }

        val viewModel = RecipeDetailsViewModel(
            getRecipeDetailsUseCase = getRecipeDetailsUseCase,
            deleteRecipeUseCase = deleteRecipeUseCase,
            insertRecipeUseCase = insertRecipeUseCase
        )
        viewModel.onEvent(
            com.example.search.screens.details.RecipeDetails.Event.InsertRecipe(getRecipeDetails())
        )

        assert(recipeDb.contains(recipe))
    }

    @Test
    fun test_delete() = runTest {

        val recipeDb = mutableListOf<Recipe>()
        val recipe = getRecipeDetails().toRecipe()
        `when`(insertRecipeUseCase.invoke(recipe))
            .then {
                recipeDb.add(recipe)
                flowOf(Unit)
            }

        `when`(deleteRecipeUseCase.invoke(recipe))
            .then {
                recipeDb.remove(recipe)
                flowOf(Unit)
            }

        val viewModel = RecipeDetailsViewModel(
            getRecipeDetailsUseCase = getRecipeDetailsUseCase,
            deleteRecipeUseCase = deleteRecipeUseCase,
            insertRecipeUseCase = insertRecipeUseCase
        )
        viewModel.onEvent(
            com.example.search.screens.details.RecipeDetails.Event.InsertRecipe(getRecipeDetails())
        )
        viewModel.onEvent(
            com.example.search.screens.details.RecipeDetails.Event.DeleteRecipe(getRecipeDetails())
        )

        assert(recipeDb.isEmpty())
    }
}


private fun getRecipeResponse(): List<Recipe> {
    return listOf(
        Recipe(
            idMeal = "idMeal",
            strArea = "India",
            strCategory = "category",
            strYoutube = "strYoutube",
            strTags = "tag1,tag2",
            strMeal = "Chicken",
            strMealThumb = "strMealThumb",
            strInstructions = "12",
        ),
        Recipe(
            idMeal = "idMeal",
            strArea = "India",
            strCategory = "category",
            strYoutube = "strYoutube",
            strTags = "tag1,tag2",
            strMeal = "Chicken",
            strMealThumb = "strMealThumb",
            strInstructions = "123",
        )
    )

}

private fun getRecipeDetails(): RecipeDetails {
    return RecipeDetails(
        idMeal = "idMeal",
        strArea = "India",
        strCategory = "category",
        strYoutube = "strYoutube",
        strTags = "tag1,tag2",
        strMeal = "Chicken",
        strMealThumb = "strMealThumb",
        strInstructions = "strInstructions",
        ingredientsPair = listOf(Pair("Ingredients", "Measure"))
    )
}