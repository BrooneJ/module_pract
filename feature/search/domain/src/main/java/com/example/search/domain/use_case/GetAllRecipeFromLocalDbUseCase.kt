package com.example.search.domain.use_case

import com.example.search.domain.repository.SearchRepository
import javax.inject.Inject

class GetAllRecipeFromLocalDbUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    operator fun invoke() = searchRepository.getAllRecipes()
}