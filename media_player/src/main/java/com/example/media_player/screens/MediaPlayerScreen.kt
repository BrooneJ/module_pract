package com.example.media_player.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun MediaPlayerScreen(modifier: Modifier = Modifier, videoId: String) {

    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(modifier = modifier.fillMaxSize(), factory = {
        YouTubePlayerView(it).apply {
            lifecycleOwner.lifecycle.addObserver(this)
            addYouTubePlayerListener(object : YouTubePlayerListener {
                override fun onApiChange(youTubePlayer: YouTubePlayer) {
                    TODO("Not yet implemented")
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    TODO("Not yet implemented")
                }

                override fun onError(
                    youTubePlayer: YouTubePlayer,
                    error: PlayerConstants.PlayerError
                ) {
                    TODO("Not yet implemented")
                }

                override fun onPlaybackQualityChange(
                    youTubePlayer: YouTubePlayer,
                    playbackQuality: PlayerConstants.PlaybackQuality
                ) {
                }

                override fun onPlaybackRateChange(
                    youTubePlayer: YouTubePlayer,
                    playbackRate: PlayerConstants.PlaybackRate
                ) {

                }

                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0f)
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {

                }

                override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {

                }

                override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {

                }

                override fun onVideoLoadedFraction(
                    youTubePlayer: YouTubePlayer,
                    loadedFraction: Float
                ) {

                }
            })
        }
    })
}