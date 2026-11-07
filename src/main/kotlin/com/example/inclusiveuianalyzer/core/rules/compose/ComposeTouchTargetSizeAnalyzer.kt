package com.example.inclusiveuianalyzer.core.rules.compose

import com.example.inclusiveuianalyzer.core.rules.xml.TouchTargetSizeAnalyzer.MinTouchTargetDp
import com.example.inclusiveuianalyzer.core.utils.compose.ComposeUtils
import org.jetbrains.kotlin.psi.KtCallExpression

object ComposeTouchTargetSizeAnalyzer {

    private val interactiveComponents = setOf(
        "Button",
        "TextButton",
        "OutlinedButton",
        "ElevatedButton",
        "FilledTonalButton",
        "IconButton",
        "IconToggleButton",
        "Checkbox",
        "RadioButton",
        "Switch",
        "Slider",
        "RangeSlider",
        "FloatingActionButton",
        "SmallFloatingActionButton",
        "LargeFloatingActionButton",
        "ExtendedFloatingActionButton",
        "DropdownMenuItem",
        "NavigationBarItem",
        "NavigationRailItem",
        "Tab"
    )

    private val uniformSize = Regex("""\.(?:size|requiredSize)\(\s*([0-9]+(?:\.[0-9]+)?)\.dp\s*\)""")
    private val widthSize = Regex("""\.(?:width|requiredWidth)\(\s*([0-9]+(?:\.[0-9]+)?)\.dp\s*\)""")
    private val heightSize = Regex("""\.(?:height|requiredHeight)\(\s*([0-9]+(?:\.[0-9]+)?)\.dp\s*\)""")
    private val minWidthSize = Regex("""minWidth\s*=\s*([0-9]+(?:\.[0-9]+)?)\.dp""")
    private val minHeightSize = Regex("""minHeight\s*=\s*([0-9]+(?:\.[0-9]+)?)\.dp""")

    fun shouldReport(call: KtCallExpression): Boolean {
        val modifier = ComposeUtils.findModifierChain(call) ?: return false
        if (hasInteractiveMinimum(modifier)) return false

        val componentName = ComposeUtils.getCallName(call)
        val isInteractive = componentName in interactiveComponents ||
                ComposeUtils.containsClickableModifier(modifier) ||
                ComposeUtils.containsInteractiveModifier(modifier)
        if (!isInteractive) return false

        val width = uniformSize.find(modifier)?.groupValues?.get(1)?.toFloatOrNull()
            ?: widthSize.find(modifier)?.groupValues?.get(1)?.toFloatOrNull()
        val height = uniformSize.find(modifier)?.groupValues?.get(1)?.toFloatOrNull()
            ?: heightSize.find(modifier)?.groupValues?.get(1)?.toFloatOrNull()

        return isTooSmall(width) || isTooSmall(height)
    }

    private fun hasInteractiveMinimum(modifier: String): Boolean {
        if (modifier.contains("minimumInteractiveComponentSize()") ||
            modifier.contains("minimumTouchTargetSize()")
        ) {
            return true
        }

        val minWidth = minWidthSize.find(modifier)?.groupValues?.get(1)?.toFloatOrNull()
        val minHeight = minHeightSize.find(modifier)?.groupValues?.get(1)?.toFloatOrNull()
        return minWidth != null && minWidth >= MinTouchTargetDp &&
                minHeight != null && minHeight >= MinTouchTargetDp
    }

    private fun isTooSmall(value: Float?): Boolean {
        return value != null && value > 0f && value < MinTouchTargetDp
    }
}
