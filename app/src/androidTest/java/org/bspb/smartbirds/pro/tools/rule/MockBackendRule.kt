package org.bspb.smartbirds.pro.tools.rule

import okhttp3.mockwebserver.MockWebServer
import org.bspb.smartbirds.pro.backend.Backend
import org.bspb.smartbirds.pro.backend.SmartBirdsApi
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement


class MockBackendRule : TestRule {

    lateinit var api: SmartBirdsApi
    lateinit var server: MockWebServer

    override fun apply(base: Statement?, description: Description?): Statement = statement(base)

    private fun statement(base: Statement?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                server = MockWebServer()
                server.start()

                // replace retrofit proxy with our mock
                Backend.Companion.backendBaseUrl =
                    "http://" + server.hostName + ":" + server.port
                base?.evaluate()
            }
        }
    }
}