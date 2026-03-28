package com.example.inclusiveuianalyzer.core.engine

object AnalyzerEngineHolder {
    val instance: AnalyzerEngine by lazy {
        AnalyzerEngine()
    }
}
