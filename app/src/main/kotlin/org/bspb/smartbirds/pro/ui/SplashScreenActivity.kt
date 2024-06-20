package org.bspb.smartbirds.pro.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.app.TaskStackBuilder
import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.Fullscreen
import org.androidannotations.annotations.ViewById
import org.androidannotations.annotations.WindowFeature
import org.androidannotations.annotations.sharedpreferences.Pref
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.prefs.SmartBirdsPrefs_
import org.bspb.smartbirds.pro.service.DataService

@WindowFeature(Window.FEATURE_NO_TITLE)
@Fullscreen
@EActivity(R.layout.activity_splash_screen)
open class SplashScreenActivity : BaseActivity(), Runnable {

    @ViewById(android.R.id.content)
    protected lateinit var content: View

    @Pref
    protected lateinit var prefs: SmartBirdsPrefs_

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        content!!.postDelayed(this, SPLASH_DURATION.toLong())
    }

    override fun onPause() {
        content!!.removeCallbacks(this)
        super.onPause()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun run() {
        if (prefs!!.runningMonitoring().get()) {
            DataService.intent(this).start()
            TaskStackBuilder.create(this@SplashScreenActivity)
                .addNextIntentWithParentStack(
                    MonitoringActivity_.intent(this@SplashScreenActivity).get()
                )
                .startActivities()
        } else {
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        }
        finish()
    }

    companion object {
        // Duration in milliseconds
        private const val SPLASH_DURATION = 3000
    }
}