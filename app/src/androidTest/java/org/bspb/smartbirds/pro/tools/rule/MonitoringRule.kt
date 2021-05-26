package org.bspb.smartbirds.pro.tools.rule

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MonitoringRule : TestRule {

    override fun apply(base: Statement?, description: Description?) = statement(base)

    private fun statement(base: Statement?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {

            }
        }
    }
}