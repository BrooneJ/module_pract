package com.example.search.data.mappers

import com.example.search.data.model.RecipeDTO
import com.example.search.domain.model.Recipe

fun List<RecipeDTO>.toDomain(): List<Recipe> = map {
    Recipe(
        idMeal = it.idMeal,
        strArea = it.strArea,
        strMeal = it.strMeal,
        strMealThumb = it.strMealThumb,
        strCategory = it.strCategory,
        strTags = it.strTags ?: "",
        strYoutube = it.strYoutube ?: "",
        strInstructions = it.strInstructions
    )
}