package com.bitmovin.analytics.conviva

import android.content.Context
import android.os.Handler
import android.util.Log
import com.bitmovin.analytics.conviva.fixtures.MockPlayer
import com.bitmovin.analytics.conviva.ssai.DefaultSsaiApi
import com.bitmovin.player.api.Player
import com.conviva.sdk.ConvivaAdAnalytics
import com.conviva.sdk.ConvivaSdkConstants
import com.conviva.sdk.ConvivaVideoAnalytics
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkConstructor
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class LatePlayerAttachingTest {
    private val mockedPlayer: Player = mockk(relaxed = true)
    private val player: MockPlayer = MockPlayer(mockedPlayer)
    private val videoAnalytics: ConvivaVideoAnalytics = mockk(relaxed = true)
    private val adAnalytics: ConvivaAdAnalytics = mockk(relaxed = true)
    private val ssaiApi: DefaultSsaiApi = mockk(relaxed = true)
    private val context: Context = mockk()

    private lateinit var convivaAnalyticsIntegration: ConvivaAnalyticsIntegration

    @Before
    fun beforeTest() {
        convivaAnalyticsIntegration = ConvivaAnalyticsIntegration(
            null,
            "",
            context,
            ConvivaConfig(),
            videoAnalytics,
            adAnalytics,
            ssaiApi,
        )
    }

    @After
    fun afterTest() {
        clearMocks(mockedPlayer, ssaiApi, videoAnalytics, adAnalytics)
    }

    @Test
    fun `initialize session without player initializes the session with the externally provided asset name`() {
        val metadataOverride = MetadataOverrides()
        metadataOverride.assetName = "MyAsset"
        convivaAnalyticsIntegration.updateContentMetadata(metadataOverride)
        convivaAnalyticsIntegration.initializeSession()

        verify { videoAnalytics.reportPlaybackRequested(any()) }
    }

    @Test
    fun `initialize session without player and attaching the player while the session is already active`() {
        val metadataOverride = MetadataOverrides()
        metadataOverride.assetName = "MyAsset"
        convivaAnalyticsIntegration.updateContentMetadata(metadataOverride)
        convivaAnalyticsIntegration.initializeSession()

        convivaAnalyticsIntegration.attachPlayer(player)

        verify { videoAnalytics.setContentInfo(any()) }
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            mockkStatic(Log::class)
            every { Log.v(any(), any()) } returns 0
            every { Log.d(any(), any()) } returns 0
            every { Log.i(any(), any()) } returns 0
            every { Log.e(any(), any()) } returns 0
            every { Log.w(any(), any<String>()) } returns 0

            mockkConstructor(Handler::class)
            every { anyConstructed<Handler>().postDelayed(any(), any()) } answers {
                firstArg<Runnable>().run()
                true
            }
        }

        @JvmStatic
        @AfterClass
        fun afterClass() {
            unmockkStatic(Log::class)
            unmockkConstructor(Handler::class)
        }
    }
}