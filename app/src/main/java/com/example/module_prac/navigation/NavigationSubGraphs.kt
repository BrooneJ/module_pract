package com.example.module_prac.navigation

import com.example.media_player.navigation.MediaPlayerFeatureApi
import com.example.search.navigation.SearchFeatureApi

data class NavigationSubGraphs(
    val searchFeatureApi: SearchFeatureApi,
    val mediaPlayerApi: MediaPlayerFeatureApi
)