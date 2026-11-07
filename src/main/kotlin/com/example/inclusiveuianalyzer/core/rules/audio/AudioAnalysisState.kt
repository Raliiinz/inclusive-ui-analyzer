package com.example.inclusiveuianalyzer.core.rules.audio

data class AudioAnalysisState(
    var audioCalls: MutableSet<String> = linkedSetOf(),
    var hasHaptic: Boolean = false,
    var hasVisual: Boolean = false,
    var hasOutputOperation: Boolean = false
) {
    fun hasAudio(): Boolean = audioCalls.isNotEmpty() && hasOutputOperation

    fun hasAlternative(): Boolean = hasHaptic || hasVisual

    fun merge(other: AudioAnalysisState) {
        audioCalls.addAll(other.audioCalls)
        hasHaptic = hasHaptic || other.hasHaptic
        hasVisual = hasVisual || other.hasVisual
        hasOutputOperation = hasOutputOperation || other.hasOutputOperation
    }
}
