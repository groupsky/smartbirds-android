package org.bspb.smartbirds.pro.tools.rule

import androidx.test.core.app.ApplicationProvider
import com.google.gson.reflect.TypeToken
import org.bspb.smartbirds.pro.db.generated.SmartBirdsDatabase
import org.bspb.smartbirds.pro.tools.SBGsonParser
import org.bspb.smartbirds.pro.utils.debugLog
import org.json.JSONObject
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DbRule : TestRule {

    private lateinit var db: SmartBirdsDatabase

    override fun apply(base: Statement?, description: Description?) = statement(base)

    private fun statement(base: Statement?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                db = SmartBirdsDatabase.getInstance(ApplicationProvider.getApplicationContext())
                base?.evaluate()
            }
        }
    }

    fun getForms(): List<Map<String, String>> {
        var cursor = db.readableDatabase.rawQuery("SELECT * FROM forms", null)
        return generateSequence { if (cursor.moveToNext()) cursor else null }.map {
            var rowValues = mutableMapOf<String, String>()
            it.columnNames?.forEach { columnName ->
                if ("data" == columnName) {
                    debugLog("Data: ${it.getString(it.getColumnIndex(columnName))}")
                    val typeToken = object : TypeToken<HashMap<String, String>>() {}.type
                    var json = JSONObject(it.getString(it.getColumnIndex(columnName)))
                    rowValues.putAll(
                        SBGsonParser.createParser().fromJson(
                            json.getString("data"),
                            typeToken
                        )
                    )
                }
                rowValues[columnName] = it.getString(it.getColumnIndex(columnName))
            }
            return@map rowValues
        }.toList()
    }
}