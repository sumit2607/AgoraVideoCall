package com.example.agoravideocall

import android.app.PictureInPictureParams
import android.app.PictureInPictureUiState
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.renderer.FloatingParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.RealtimeConnection
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiKey = "mmhfdzb5evj2"
        val userToken =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL0JyYWtpc3MiLCJ1c2VyX2lkIjoiQnJha2lzcyIsInZhbGlkaXR5X2luX3NlY29uZHMiOjYwNDgwMCwiaWF0IjoxNzI1NjE3MzM3LCJleHAiOjE3MjYyMjIxMzd9.g2_PgB4TXetMlGm1bnOygsEkTpST168txDuWcZqoeB8"
        val userId = "Brakiss"
        val callId = "Xi6ATXihket4"

        // Create a user.
        val user = User(
            id = userId,
            name = "Tutorial",
            image = "https://bit.ly/2TIt8NR",
        )

        // Initialize StreamVideo
        val client = StreamVideoBuilder(
            context = applicationContext,
            apiKey = apiKey,
            geo = GEO.GlobalEdgeNetwork,
            user = user,
            token = userToken,
        ).build()

        setContent {
            val call = client.call(type = "default", id = callId)
            LaunchCallPermissions(
                call = call,
                onAllPermissionsGranted = {
                    val result = call.join(create = true)
                    result.onError {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            )

            // Apply VideoTheme and display UI
            VideoTheme {
                val remoteParticipants by call.state.remoteParticipants.collectAsState()
                val remoteParticipant = remoteParticipants.firstOrNull()
                val me by call.state.me.collectAsState()
                val connection by call.state.connection.collectAsState()
                var parentSize: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VideoTheme.colors.baseSenary)
                        .onSizeChanged { parentSize = it }
                ) {
                    if (remoteParticipant != null) {
                        ParticipantVideo(
                            modifier = Modifier.fillMaxSize(),
                            call = call,
                            participant = remoteParticipant
                        )
                    } else {
                        if (connection != RealtimeConnection.Connected) {
                            Text(
                                text = "waiting for a remote participant...",
                                fontSize = 30.sp,
                                color = VideoTheme.colors.basePrimary
                            )
                        } else {
                            Text(
                                modifier = Modifier.padding(30.dp),
                                text = "Join call ${call.id} in your browser to see the video here",
                                fontSize = 30.sp,
                                color = VideoTheme.colors.basePrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Floating video UI for the local video participant
                    me?.let { localVideo ->
                        FloatingParticipantVideo(
                            modifier = Modifier.align(Alignment.TopEnd),
                            call = call,
                            participant = localVideo,
                            parentBounds = parentSize
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBackPressed() {
        onUserLeaveHint()
    }
    // Enter PIP mode when the home button is pressed
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPipMode()
        }
        super.onUserLeaveHint()
    }

    // Method to enter PIP mode
    @RequiresApi(Build.VERSION_CODES.S)
    private fun enterPipMode() {
        //Log.d("TAG", "enterPipMode: " + "here in line no 151")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val paramsBuilder = PictureInPictureParams.Builder()
            val aspectRatio = Rational(9, 16) // Set the desired aspect ratio
            paramsBuilder.setAspectRatio(aspectRatio)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                paramsBuilder.setSeamlessResizeEnabled(false)
            }
            enterPictureInPictureMode(paramsBuilder.build())

        }
    }


    override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
        super.onPictureInPictureUiStateChanged(pipState)

       // Log.d("TAG", "onPictureInPictureUiStateChanged: " + "here in line no 168")
    }
}

