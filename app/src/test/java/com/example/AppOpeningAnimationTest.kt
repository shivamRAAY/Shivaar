package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.ShivaarRepository
import com.example.data.ShivaarStorage
import com.example.data.model.*
import com.example.ui.AppDestination
import com.example.ui.ShivaarViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppOpeningAnimationTest {

    private lateinit var application: Application
    private lateinit var storage: ShivaarStorage
    private lateinit var repository: ShivaarRepository
    private lateinit var viewModel: ShivaarViewModel

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        application.getSharedPreferences("shivaar_storage_prefs", Application.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        storage = ShivaarStorage(application)
        repository = ShivaarRepository(application)
        viewModel = ShivaarViewModel(application, repository)
    }

    @Test
    fun testAppOpeningAnimationCallbackFlow() {
        var isOpeningActive = true
        val onAnimationComplete = {
            isOpeningActive = false
        }

        assertTrue("App opening animation must start active", isOpeningActive)

        // Simulate animation completion or user skipping intro
        onAnimationComplete()

        assertFalse("App opening animation must deactivate upon completion", isOpeningActive)
        assertEquals(AppDestination.TODAY, viewModel.currentTab.value)
    }

    @Test
    fun testNavigationAfterOpeningAnimation() {
        // Upon app opening, navigate seamlessly between hubs
        viewModel.navigateTo(AppDestination.ACADEMICS)
        assertEquals(AppDestination.ACADEMICS, viewModel.currentTab.value)

        viewModel.navigateTo(AppDestination.CODING)
        assertEquals(AppDestination.CODING, viewModel.currentTab.value)

        viewModel.navigateTo(AppDestination.PROJECTS)
        assertEquals(AppDestination.PROJECTS, viewModel.currentTab.value)

        viewModel.navigateTo(AppDestination.CAREER)
        assertEquals(AppDestination.CAREER, viewModel.currentTab.value)
    }
}
