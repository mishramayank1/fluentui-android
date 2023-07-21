package com.microsoft.fluentuidemo.demos

import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.microsoft.fluentui.tokenized.APP_BAR
import com.microsoft.fluentui.tokenized.APP_BAR_BOTTOM_BAR
import com.microsoft.fluentui.tokenized.APP_BAR_SEARCH_BAR
import com.microsoft.fluentui.tokenized.APP_BAR_SUBTITLE
import com.microsoft.fluentuidemo.BaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class V2AppBarLayoutActivityUITest : BaseTest() {

    @Before
    fun initialize() {
        launchActivity(V2AppBarLayoutActivity::class.java)
    }

    private val modifiableParametersButton =
        composeTestRule.onAllNodesWithTag(APP_BAR_MODIFIABLE_PARAMETER_SECTION)[0]

    @Test
    fun testAppBarDisplay() {
        composeTestRule.onNodeWithTag(APP_BAR).assertExists()
    }

    @Test
    fun testAppBarSubtitleToggle() {
        modifiableParametersButton.performClick()
        val control = composeTestRule.onNodeWithTag(APP_BAR_SUBTITLE_PARAM)
        val component = composeTestRule.onNodeWithTag(APP_BAR_SUBTITLE)
        assertExistsAfterToggleOnly(control, component, "Subtitle is not displayed")
    }

    @Test
    fun testAppBarBottomBarToggle() {
        modifiableParametersButton.performClick()
        val control = composeTestRule.onNodeWithTag(APP_BAR_BUTTONBAR_PARAM)
        val component = composeTestRule.onNodeWithTag(APP_BAR_BOTTOM_BAR)
        assertExistsAfterToggleOnly(control, component, "Bottom bar is not displayed")
    }

    @Test
    fun testAppBarSearchBarToggle() {
        modifiableParametersButton.performClick()
        val control = composeTestRule.onNodeWithTag(APP_BAR_SEARCHBAR_PARAM)
        val component = composeTestRule.onNodeWithTag(APP_BAR_SEARCH_BAR)
        assertExistsAfterToggleOnly(control, component, "Search bar is not displayed")
    }

    @Test
    fun testAppBarStyleToggle() {
        modifiableParametersButton.performClick()
        val control = composeTestRule.onNodeWithTag(APP_BAR_STYLE_PARAM)
        val component = composeTestRule.onNodeWithTag(APP_BAR)
        toggleControlToValue(control, true)
        component.assertExists("App bar is not displayed in accent")
    }
}