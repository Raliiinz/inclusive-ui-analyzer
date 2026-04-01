package com.example.inclusiveuianalyzer.core.rules

import com.example.inclusiveuianalyzer.core.context.AnalysisContext
import com.example.inclusiveuianalyzer.core.model.Issue
import com.example.inclusiveuianalyzer.core.model.Profile

interface Rule {
    val profile: Profile
    val target: AnalysisTarget

    fun check(context: AnalysisContext): List<Issue>
}
