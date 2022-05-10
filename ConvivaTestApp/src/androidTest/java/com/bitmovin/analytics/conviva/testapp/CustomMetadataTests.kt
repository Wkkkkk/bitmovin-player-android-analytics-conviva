package com.bitmovin.analytics.conviva.testapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.conviva.sdk.ConvivaSdkConstants
import com.conviva.sdk.ConvivaSdkConstants.*
import io.mockk.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class CustomMetadataTests : TestBase() {
    @Test
    fun metadataOverrideVodExplicitInitialize() {
        val customInternTags: MutableMap<String, String> = HashMap()
        customInternTags["contentType"] = "Episode"
        val customMetadata = customMetadataOverrides(
            source = DEFAULT_DASH_VOD_SOURCE,
            streamType = StreamType.VOD,
            duration = DEFAULT_DASH_VOD_SOURCE_DURATION,
            customTags = customInternTags)

        // launch player with autoPlay enabled
        activityScenario = setupPlayerActivityForTest(autoPlay = true, customMetadata)

        // initialize session and verify
        initializeSession(activityScenario)

        // verify session initialization
        verifySessionInitialization(activityScenario)

        // load source and verify
        loadSource(activityScenario, DEFAULT_DASH_VOD_SOURCE)
        verifyPlayingWithMetadata(
            activityScenario = activityScenario,
            source = DEFAULT_DASH_VOD_SOURCE,
            streamType = StreamType.VOD,
            streamDuration = DEFAULT_DASH_VOD_SOURCE_DURATION,
            metadata = customMetadata,
            overrideCustom = true
        )
    }

    @Test
    fun metadataOverrideLiveExplicitInitalize() {
        val customInternTags: MutableMap<String, String> = HashMap()
        customInternTags["contentType"] = "Episode"
        val customMetadata = customMetadataOverrides(
            source = DEFAULT_DASH_LIVE_SOURCE,
            streamType = StreamType.LIVE,
            duration = DEFAULT_DASH_LIVE_SOURCE_DURATION,
            customTags = customInternTags)

        // launch player with autoPlay enabled
        activityScenario = setupPlayerActivityForTest(autoPlay = true, customMetadata)

        // initialize session and verify
        initializeSession(activityScenario)

        // verify session initialization
        verifySessionInitialization(activityScenario)

        // load source and verify
        loadSource(activityScenario, DEFAULT_DASH_LIVE_SOURCE)
        verifyPlayingWithMetadata(
            activityScenario = activityScenario,
            source = DEFAULT_DASH_LIVE_SOURCE,
            streamType = StreamType.LIVE,
            streamDuration = DEFAULT_DASH_LIVE_SOURCE_DURATION,
            metadata = customMetadata,
            overrideCustom = true
        )
    }

    @Test
    fun metadataOverrideCustomInternTags() {
        // Set default metadata overrides
        val customInternTags: MutableMap<String, String> = HashMap()
        customInternTags["contentType"] = "Episode"
        customInternTags["streamType"] = "CUSTOM_STREAM_TYPE"
        customInternTags["integrationVersion"] = "CUSTOM_INTEGRATION_VERSION"
        val customMetadata = customMetadataOverrides(
            source = DEFAULT_DASH_VOD_SOURCE,
            streamType = StreamType.VOD,
            duration = DEFAULT_DASH_VOD_SOURCE_DURATION,
            customTags = customInternTags)

        // launch player with autoPlay enabled
        activityScenario = setupPlayerActivityForTest(autoPlay = true, customMetadata)

        // initialize session and verify
        initializeSession(activityScenario)

        // verify session initialization
        verifySessionInitialization(activityScenario)

        // load source and verify
        loadSource(activityScenario, DEFAULT_DASH_VOD_SOURCE)
        verifyPlayingWithMetadata(
            activityScenario = activityScenario,
            source = DEFAULT_DASH_VOD_SOURCE,
            streamType = StreamType.VOD,
            streamDuration = DEFAULT_DASH_VOD_SOURCE_DURATION,
            metadata = customMetadata,
            overrideCustom = true
        )

//        activityScenario.onActivity { activity: MainActivity ->
//            verify {
//                clientMock?.updateContentMetadata(
//                    CONVIVA_SESSION_ID,
//                    metadataEq(expectedContentMetadata(
//                        source = DEFAULT_DASH_VOD_SOURCE,
//                        streamType = StreamType.VOD,
//                        duration = DEFAULT_DASH_VOD_SOURCE_DURATION,
//                        overrideMetadata = customMetadata,
//                        overrideCustom = true))
//                )
//            }
//        }
    }
}
