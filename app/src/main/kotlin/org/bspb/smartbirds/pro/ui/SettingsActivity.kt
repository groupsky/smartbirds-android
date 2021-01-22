package org.bspb.smartbirds.pro.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.bspb.smartbirds.pro.R
import org.bspb.smartbirds.pro.ui.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_fragment)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .commit()
    }
}