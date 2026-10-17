package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.ShivaarRepository
import com.example.data.model.ResearchSource
import com.example.service.GeminiMentorService
import com.example.ui.AppDestination
import com.example.ui.ShivaarViewModel
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GeminiGroundingAndNavigationTest {

    private lateinit var application: Application
    private lateinit var repository: ShivaarRepository
    private lateinit var viewModel: ShivaarViewModel
    private val mentorService = GeminiMentorService()

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        application.getSharedPreferences("shivaar_storage_prefs", Application.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        repository = ShivaarRepository(application)
        viewModel = ShivaarViewModel(application, repository)
    }

    @Test
    fun testParseGroundingMetadataExtractsRealSourcesAndCitations() {
        // Construct a realistic Google Search Grounding metadata JSON payload
        val metadataJson = JSONObject().apply {
            // 1. Search Queries
            put("webSearchQueries", JSONArray().apply {
                put("Kotlin 2.0 K2 compiler release notes kotlinlang.org")
            })

            // 2. Grounding Chunks (Web Sources)
            put("groundingChunks", JSONArray().apply {
                put(JSONObject().apply {
                    put("web", JSONObject().apply {
                        put("uri", "https://kotlinlang.org/docs/whatsnew20.html")
                        put("title", "What's new in Kotlin 2.0.0 | Kotlin Documentation")
                    })
                })
                put(JSONObject().apply {
                    put("web", JSONObject().apply {
                        put("uri", "https://aktu.ac.in/syllabus.html")
                        put("title", "AKTU Official Syllabus B.Tech CSE 2024")
                    })
                })
            })

            // 3. Grounding Supports (Citations linking claims to chunks)
            put("groundingSupports", JSONArray().apply {
                put(JSONObject().apply {
                    put("segment", JSONObject().apply {
                        put("startIndex", 0)
                        put("endIndex", 75)
                        put("text", "The K2 compiler is now default in Kotlin 2.0 providing faster build speeds.")
                    })
                    put("groundingChunkIndices", JSONArray().apply {
                        put(0)
                    })
                })
            })
        }

        val (sources, citations, queries) = mentorService.parseGroundingMetadata(metadataJson)

        // Verify Queries
        assertEquals(1, queries.size)
        assertEquals("Kotlin 2.0 K2 compiler release notes kotlinlang.org", queries[0])

        // Verify Sources
        assertEquals(2, sources.size)
        val kotlinSource = sources.first { it.domain == "kotlinlang.org" }
        assertEquals("https://kotlinlang.org/docs/whatsnew20.html", kotlinSource.url)
        assertEquals("What's new in Kotlin 2.0.0 | Kotlin Documentation", kotlinSource.title)
        assertTrue("kotlinlang.org should be recognized as official", kotlinSource.isOfficial)

        val aktuSource = sources.first { it.domain == "aktu.ac.in" }
        assertEquals("https://aktu.ac.in/syllabus.html", aktuSource.url)
        assertTrue("aktu.ac.in should be recognized as official university domain", aktuSource.isOfficial)

        // Verify Citations
        assertEquals(1, citations.size)
        val citation = citations[0]
        assertEquals(0, citation.chunkIndex)
        assertEquals("https://kotlinlang.org/docs/whatsnew20.html", citation.url)
        assertEquals("The K2 compiler is now default in Kotlin 2.0 providing faster build speeds.", citation.segmentText)
    }

    @Test
    fun testDomainCategorizationPrioritizesOfficialAndUniversityResources() {
        assertEquals("University / Syllabus", mentorService.categorizeDomain("aktu.ac.in"))
        assertEquals("University / Syllabus", mentorService.categorizeDomain("iitk.ac.in"))
        assertEquals("Official Documentation", mentorService.categorizeDomain("kotlinlang.org"))
        assertEquals("Official Documentation", mentorService.categorizeDomain("en.cppreference.com"))
        assertEquals("Official Documentation", mentorService.categorizeDomain("docs.python.org"))
        assertEquals("Official Documentation", mentorService.categorizeDomain("developer.android.com"))
        assertEquals("Placement & Careers", mentorService.categorizeDomain("takeuforward.org"))
        assertEquals("Placement & Careers", mentorService.categorizeDomain("leetcode.com"))
    }

    @Test
    fun testNullOrEmptyMetadataProducesZeroFakeCitations() {
        val (sources, citations, queries) = mentorService.parseGroundingMetadata(null)
        assertTrue("Sources must be empty when metadata is null", sources.isEmpty())
        assertTrue("Citations must be empty when metadata is null (no fake citations)", citations.isEmpty())
        assertTrue("Queries must be empty when metadata is null", queries.isEmpty())

        val emptyJson = JSONObject()
        val (sourcesEmpty, citationsEmpty, queriesEmpty) = mentorService.parseGroundingMetadata(emptyJson)
        assertTrue("Sources must be empty for empty JSON", sourcesEmpty.isEmpty())
        assertTrue("Citations must be empty for empty JSON (no fake citations)", citationsEmpty.isEmpty())
        assertTrue("Queries must be empty for empty JSON", queriesEmpty.isEmpty())
    }

    @Test
    fun testResearchLibrarySourceAdditionAndBookmarkToggle() {
        val initialSources = viewModel.researchSources.value
        assertTrue("Initial research sources should be seeded", initialSources.isNotEmpty())

        val newSource = ResearchSource(
            id = "src_live_kotlin_k2",
            title = "Kotlin 2.0.0 Language Specification & Compiler Frontend",
            category = "Documentation",
            url = "https://kotlinlang.org/spec/k2.html",
            domain = "kotlinlang.org",
            summary = "Specification of the K2 compiler frontend and FIR architecture.",
            isBookmarked = false,
            isOfficial = true
        )

        repository.addResearchSources(listOf(newSource))

        val updatedSources = viewModel.researchSources.value
        assertTrue("New research source should be added", updatedSources.any { it.id == "src_live_kotlin_k2" })

        // Test bookmark toggle
        viewModel.toggleResearchBookmark("src_live_kotlin_k2")
        val bookmarked = viewModel.researchSources.value.first { it.id == "src_live_kotlin_k2" }
        assertTrue("Source should now be bookmarked", bookmarked.isBookmarked)

        viewModel.toggleResearchBookmark("src_live_kotlin_k2")
        val unbookmarked = viewModel.researchSources.value.first { it.id == "src_live_kotlin_k2" }
        assertFalse("Source should now be unbookmarked", unbookmarked.isBookmarked)
    }

    @Test
    fun testNavigationDestinationTransitions() {
        assertEquals(AppDestination.TODAY, viewModel.currentTab.value)

        viewModel.navigateTo(AppDestination.RESEARCH)
        assertEquals(AppDestination.RESEARCH, viewModel.currentTab.value)

        viewModel.navigateTo(AppDestination.MENTOR)
        assertEquals(AppDestination.MENTOR, viewModel.currentTab.value)

        viewModel.navigateTo(AppDestination.ROADMAP)
        assertEquals(AppDestination.ROADMAP, viewModel.currentTab.value)

        viewModel.navigateTo(AppDestination.TODAY)
        assertEquals(AppDestination.TODAY, viewModel.currentTab.value)
    }
}
