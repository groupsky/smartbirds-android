package org.bspb.smartbirds.pro.tools.rule

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.bspb.smartbirds.pro.db.generated.SmartBirdsDatabase
import org.bspb.smartbirds.pro.utils.debugLog
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

class FixturesRule : TestRule {
    override fun apply(base: Statement?, description: Description?): Statement = statement(base)

    private fun statement(base: Statement?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                val inStream =
                    InstrumentationRegistry.getInstrumentation().context.assets.open("fixtures.sql")

                val fixturesSql: String = BufferedReader(InputStreamReader(inStream))
                    .lines().collect(Collectors.joining("\n"))

                var db =
                    SmartBirdsDatabase.getInstance(ApplicationProvider.getApplicationContext()).writableDatabase

                fixturesSql?.split(";")?.forEach {
                    db.execSQL(it)
                }
                base?.evaluate()
            }
        }
    }
}