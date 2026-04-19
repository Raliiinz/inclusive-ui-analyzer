package com.example.inclusiveuianalyzer.core.rules.audio

data class AudioAnalysisState(
    var audioCalls: MutableList<String> = mutableListOf(),
    var hasHaptic: Boolean = false,
    var hasVisual: Boolean = false
) {
    fun hasAudio(): Boolean = audioCalls.isNotEmpty()

    fun hasAlternative(): Boolean = hasHaptic || hasVisual

    fun merge(other: AudioAnalysisState) {
        audioCalls.addAll(other.audioCalls)
        hasHaptic = hasHaptic || other.hasHaptic
        hasVisual = hasVisual || other.hasVisual
    }
}
