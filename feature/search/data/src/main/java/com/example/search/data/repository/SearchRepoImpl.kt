package com.example.search.data.repository

import com.example.search.data.mappers.toDomain
import com.example.search.data.remote.SearchApiService
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository

class SearchRepoImpl(
    private val searchApiService: SearchApiService
) : SearchRepository {
    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        val response = searchApiService.getRecipes(s)
        return if (response.isSuccessful) {
            response.body()?.meals?.let {
                Result.success(it.toDomain())
            } ?: run {
                Result.failure(Exception("error occurred"))
            }
        } else {
            Result.failure(Exception("error occurred"))
        }
    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {
        TODO("Not yet implemented")
    }
}