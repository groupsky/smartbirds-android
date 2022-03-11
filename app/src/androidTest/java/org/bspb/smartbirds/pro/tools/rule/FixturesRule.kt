package org.bspb.smartbirds.pro.tools.rule

import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.bspb.smartbirds.pro.db.SmartBirdsDatabase
import org.bspb.smartbirds.pro.utils.NomenclaturesManager
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

                SmartBirdsDatabase.init(ApplicationProvider.getApplicationContext())
                var db = SmartBirdsDatabase.getInstance().openHelper.writableDatabase
                db.execSQL(fixturesSql)
                fixturesSql.split(";").forEach {
                    if (it.isEmpty()) {
                        return@forEach
                    }
                    try {
                        db.execSQL(it)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        debugLog("Error ${t.message}")
                    }

                }
                NomenclaturesManager.getInstance().loadNomenclatures()
                base?.evaluate()
            }
        }
    }
}