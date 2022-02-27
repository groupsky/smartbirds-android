package org.bspb.smartbirds.pro.tools.rule

import androidx.test.core.app.ApplicationProvider
import com.google.gson.reflect.TypeToken
import org.bspb.smartbirds.pro.db.SmartBirdsDatabase
import org.bspb.smartbirds.pro.tools.SBGsonParser
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
                SmartBirdsDatabase.init(ApplicationProvider.getApplicationContext())
                db = SmartBirdsDatabase.getInstance()
                base?.evaluate()
            }
        }
    }

    fun getForms(): List<Map<String, String>> {
        var forms = db.formDao().findAll()
        return forms.map { form ->
            var rowValues = mutableMapOf<String, String>()
            val typeToken = object : TypeToken<HashMap<String, String>>() {}.type
            var json = JSONObject(form.data)
            rowValues.putAll(
                SBGsonParser.createParser().fromJson(
                    json.getString("data"),
                    typeToken
                )
            )
            rowValues["data"] = form.data
            rowValues["id"] = form.id.toString()
            rowValues["code"] = form.code
            rowValues["type"] = form.type
            rowValues["latitude"] = form.latitude.toString()
            rowValues["longitude"] = form.longitude.toString()

            return@map rowValues
        }
    }
}