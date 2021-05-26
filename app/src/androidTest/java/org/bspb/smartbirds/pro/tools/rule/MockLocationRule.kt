package org.bspb.smartbirds.pro.tools.rule

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockLocationRule : TestRule {

    companion object {
        private val providers = arrayListOf("gps", "network", "fused")
    }

    private lateinit var locationManager: LocationManager

    constructor() {
        var context: Context = ApplicationProvider.getApplicationContext()
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    override fun apply(base: Statement?, description: Description?) = statement(base)

    private fun statement(base: Statement?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {


                providers.forEach {
                    locationManager.removeTestProvider(it)
                    locationManager.addTestProvider(
                        it,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        Criteria.POWER_LOW,
                        Criteria.ACCURACY_FINE
                    )
                    locationManager.setTestProviderEnabled(it, true)
                }

                base?.evaluate()

                providers.forEach {
                    locationManager.removeTestProvider(it)
                }


            }
        }
    }

    fun updateLocation() {
        providers.forEach {
            val location = Location(it)
            location.latitude = 42.1531389
            location.longitude = 24.7496996
            location.altitude = 0.0
            location.time = System.currentTimeMillis()
            location.accuracy = 0f
            location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            locationManager.setTestProviderLocation(it, location)
        }

    }

}