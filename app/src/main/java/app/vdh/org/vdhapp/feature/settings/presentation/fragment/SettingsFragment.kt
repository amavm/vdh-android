package app.vdh.org.vdhapp.feature.settings.presentation.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import app.vdh.org.vdhapp.BuildConfig
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.feature.report.data.common.remote.mock.RetrofitMockClient
import org.koin.android.ext.android.inject

class SettingsFragment : PreferenceFragmentCompat() {

    val retrofitMockClient by inject<RetrofitMockClient>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        findPreference<PreferenceCategory>(getString(R.string.pref_key_debug_category))?.isVisible = BuildConfig.isMockDataEnabled
        findPreference<Preference>(getString(R.string.pref_key_mock_data))?.setOnPreferenceClickListener {
            retrofitMockClient.generateMockData()
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDivider(ColorDrawable(Color.TRANSPARENT))
        setDividerHeight(0)
    }
}