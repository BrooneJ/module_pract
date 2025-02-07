package com.example.search.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.common.navigation.FeatureApi
import com.example.common.navigation.NavigationRoute
import com.example.common.navigation.NavigationSubGraphRoute
import com.example.search.screens.details.RecipeDetails
import com.example.search.screens.details.RecipeDetailsScreen
import com.example.search.screens.details.RecipeDetailsViewModel
import com.example.search.screens.favorite.FavoriteScreen
import com.example.search.screens.favorite.FavoriteViewModel
import com.example.search.screens.recipe_list.RecipeList
import com.example.search.screens.recipe_list.RecipeListScreen
import com.example.search.screens.recipe_list.RecipeListViewModel

interface SearchFeatureApi : FeatureApi

class SearchFeatureApiImpl : SearchFeatureApi {
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navHostController: androidx.navigation.NavHostController
    ) {
        navGraphBuilder.navigation(
            route = NavigationSubGraphRoute.Search.route,
            startDestination = NavigationRoute.RecipeList.route
        ) {

            composable(route = NavigationRoute.RecipeList.route) {
                val viewModel = hiltViewModel<RecipeListViewModel>()
                RecipeListScreen(
                    modifier = Modifier,
                    viewModel = viewModel,
                    navHostController = navHostController
                ) { mealId ->
                    viewModel.onEvent(RecipeList.Event.GoToRecipeDetails(mealId))
                }
            }

            composable(route = NavigationRoute.RecipeDetails.route) {
                val viewModel = hiltViewModel<RecipeDetailsViewModel>()
                val meaId = it.arguments?.getString("id")
                LaunchedEffect(key1 = meaId) {
                    meaId?.let {
                        viewModel.onEvent(RecipeDetails.Event.FetchRecipeDetails(meaId))
                    }
                }
                RecipeDetailsScreen(
                    viewModel = viewModel,
                    navHostController = navHostController,
                    onNavigationClick = { viewModel.onEvent(RecipeDetails.Event.GoToRecipeListScreen) },
                    onDelete = { viewModel.onEvent(RecipeDetails.Event.DeleteRecipe(it)) },
                    onFavoriteClick = { viewModel.onEvent(RecipeDetails.Event.InsertRecipe(it)) })
            }

            composable(route = NavigationRoute.FavoriteScreen.route) {
                val viewModel = hiltViewModel<FavoriteViewModel>()
                FavoriteScreen(
                    navHostController = navHostController,
                    viewModel = viewModel,
                    onClick = { mealId ->
                        viewModel.onEvent(FavoriteScreen.Event.GoToDetails(mealId))
                    })
            }
        }
    }
}