package com.bitmovin.analytics.conviva.testapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.conviva.api.*
import io.mockk.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class CustomEventsTests: TestBase() {
    @Test
    fun customApplicationEvent() {
        // launch player with autoPlay enabled
        activityScenario = setupPlayerActivityForTest(autoPlay = true, defaultMetadataOverrides())

        // verify that custom events are not sent before Conviva session is initialized
        activityScenario.onActivity { activity: MainActivity ->
            convivaAnalytics?.sendCustomApplicationEvent(CUSTOM_EVENT_NAME)
            convivaAnalytics?.sendCustomApplicationEvent(CUSTOM_EVENT_NAME, CUSTOM_EVENT_ATTRIBUTES)
            verifyAll(inverse = true) {
                clientMock?.sendCustomEvent(
                    Client.NO_SESSION_KEY, CUSTOM_EVENT_NAME,
                    HashMap()
                )
                clientMock?.sendCustomEvent(
                    Client.NO_SESSION_KEY, CUSTOM_EVENT_NAME,
                    CUSTOM_EVENT_ATTRIBUTES
                )
            }
        }

        // initialize and verify session
        initializeSession(activityScenario)
        verifySessionIntialization(activityScenario)

        // verify that custom events are sent after Conviva session is initialized
        activityScenario.onActivity { activity: MainActivity ->
            convivaAnalytics?.sendCustomApplicationEvent(CUSTOM_EVENT_NAME)
            convivaAnalytics?.sendCustomApplicationEvent(CUSTOM_EVENT_NAME, CUSTOM_EVENT_ATTRIBUTES)
            verifyOrder() {
                clientMock?.sendCustomEvent(
                    Client.NO_SESSION_KEY,
                    CUSTOM_EVENT_NAME,
                    HashMap())
                clientMock?.sendCustomEvent(
                    Client.NO_SESSION_KEY,
                    CUSTOM_EVENT_NAME,
                    CUSTOM_EVENT_ATTRIBUTES)
            }
        }
    }

    @Test
    fun customPlaybackEvent() {
        // launch player with autoPlay enabled
        activityScenario = setupPlayerActivityForTest(autoPlay = true, defaultMetadataOverrides())

        // Verify that custom events are not sent before Conviva session is initialized
        activityScenario.onActivity { activity: MainActivity ->
            convivaAnalytics?.sendCustomPlaybackEvent(CUSTOM_EVENT_NAME)
            convivaAnalytics?.sendCustomPlaybackEvent(CUSTOM_EVENT_NAME, CUSTOM_EVENT_ATTRIBUTES)
            verifyOrder(inverse = true) {
                clientMock?.sendCustomEvent(
                    CONVIVA_SESSION_ID,
                    CUSTOM_EVENT_NAME,
                    HashMap()
                )
                clientMock?.sendCustomEvent(
                    CONVIVA_SESSION_ID,
                    CUSTOM_EVENT_NAME,
                    CUSTOM_EVENT_ATTRIBUTES
                )
            }
        }

        // initialize and verify session
        initializeSession(activityScenario)
        verifySessionIntialization(activityScenario)

        // verify that custom events are sent after Conviva session is initialized
        activityScenario.onActivity { activity: MainActivity ->
            convivaAnalytics?.sendCustomPlaybackEvent(CUSTOM_EVENT_NAME)
            convivaAnalytics?.sendCustomPlaybackEvent(CUSTOM_EVENT_NAME, CUSTOM_EVENT_ATTRIBUTES)
            verifyOrder() {
                clientMock?.sendCustomEvent(
                    CONVIVA_SESSION_ID,
                    CUSTOM_EVENT_NAME,
                    HashMap())
                clientMock?.sendCustomEvent(
                    CONVIVA_SESSION_ID,
                    CUSTOM_EVENT_NAME,
                    CUSTOM_EVENT_ATTRIBUTES)
            }
        }
    }

    @Test
    fun customErrorEvent() {
        // launch player with autoPlay enabled
        activityScenario = setupPlayerActivityForTest(autoPlay = true, defaultMetadataOverrides())

        // verify that custom error events are not sent before Conviva session is initialized
        activityScenario.onActivity { activity: MainActivity ->
            convivaAnalytics?.reportPlaybackDeficiency(
                CUSTOM_ERROR_MESSAGE,
                Client.ErrorSeverity.WARNING,
                false
            )
            verify(inverse = true) {
                clientMock?.reportError(any(), any(), any())
            }
            convivaAnalytics?.reportPlaybackDeficiency(
                CUSTOM_ERROR_MESSAGE,
                Client.ErrorSeverity.FATAL,
                true
            )
            verify(inverse = true) {
                clientMock?.reportError(any(), any(), any())
            }
        }

        // initialize and verify session
        initializeSession(activityScenario)
        verifySessionIntialization(activityScenario)

        // verify that custom error events are sent after Conviva session is initialized
        // but Conviva session is not ended when endSession argument is not passed as true
        activityScenario.onActivity { activity: MainActivity ->
            clearMocks(clientMock!!)
            convivaAnalytics?.reportPlaybackDeficiency(
                CUSTOM_ERROR_MESSAGE,
                Client.ErrorSeverity.WARNING,
                false
            )
            verify(exactly = 1) {
                clientMock?.reportError(
                    CONVIVA_SESSION_ID,
                    CUSTOM_ERROR_MESSAGE,
                    Client.ErrorSeverity.WARNING
                )
            }
            verifyAll(inverse = true) {
                clientMock?.detachPlayer(CONVIVA_SESSION_ID)
                clientMock?.cleanupSession(CONVIVA_SESSION_ID)
                clientMock?.releasePlayerStateManager(playerStateManagerMock)
            }
        }

        // verify that custom error events are sent after Conviva session is initialized
        // and Conviva session is also ended when endSession argument is passed as true
        activityScenario.onActivity { activity: MainActivity ->
            clearMocks(clientMock!!)
            clearMocks(playerStateManagerMock!!)
            convivaAnalytics?.reportPlaybackDeficiency(CUSTOM_ERROR_MESSAGE, Client.ErrorSeverity.FATAL, true)
            verifyOrder() {
                clientMock?.reportError(CONVIVA_SESSION_ID, CUSTOM_ERROR_MESSAGE, Client.ErrorSeverity.FATAL)
                clientMock?.detachPlayer(CONVIVA_SESSION_ID)
                clientMock?.cleanupSession(CONVIVA_SESSION_ID)
                clientMock?.releasePlayerStateManager(playerStateManagerMock)
            }
        }
    }
}
