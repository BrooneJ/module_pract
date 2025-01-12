package com.example.search.data.repository

import com.example.search.data.local.RecipeDao
import com.example.search.data.mappers.toDomain
import com.example.search.data.remote.SearchApiService
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchRepoImpl(
    private val searchApiService: SearchApiService,
    private val recipeDao: RecipeDao
) : SearchRepository {
    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        return try {
            val response = searchApiService.getRecipes(s)
            if (response.isSuccessful) {
                response.body()?.meals?.let {
                    Result.success(it.toDomain())
                } ?: run {
                    Result.failure(Exception("error occurred"))
                }
            } else {
                Result.failure(Exception("error occurred"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecipeDetails(i: String): Result<RecipeDetails> {
        return try {
            val response = searchApiService.getRecipeDetails(i)

            if (response.isSuccessful) {
                response.body()?.meals?.let {
                    if (it.isNotEmpty()) {
                        Result.success(it.first().toDomain())
                    } else {
                        Result.failure(Exception("error occurred"))
                    }
                } ?: run {
                    Result.failure(Exception("error occurred"))
                }
            } else {
                Result.failure(Exception("error occurred"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }
}