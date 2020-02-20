package app.vdh.org.vdhapp.feature.report.presentation.settings.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.feature.report.presentation.settings.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_container, SettingsFragment())
                .commit()
    }
}
