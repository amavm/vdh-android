package app.vdh.org.vdhapp.feature.report.presentation.settings.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import app.vdh.org.vdhapp.BuildConfig
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.core.extenstion.navigateTo
import app.vdh.org.vdhapp.core.helpers.AuthHelper
import app.vdh.org.vdhapp.core.helpers.AuthHelper.Companion.onAuthResult
import app.vdh.org.vdhapp.core.helpers.AuthHelper.Companion.signIn
import app.vdh.org.vdhapp.core.helpers.AuthHelper.Companion.signOut
import app.vdh.org.vdhapp.feature.report.data.common.remote.mock.RetrofitMockClient
import app.vdh.org.vdhapp.feature.report.domain.common.model.UserModel
import app.vdh.org.vdhapp.feature.report.presentation.moderation.activity.ReportModerationActivity
import app.vdh.org.vdhapp.feature.report.presentation.settings.preferences.TitleSubtitlePreference
import app.vdh.org.vdhapp.feature.report.presentation.settings.preferences.UserProfilePreference
import app.vdh.org.vdhapp.feature.report.presentation.settings.viewmodel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        private const val AUTH_REQUEST_CODE = 123
    }

    private val retrofitMockClient by inject<RetrofitMockClient>()
    private val viewModel: SettingsViewModel by viewModel()

    private var adminPref: Preference? = null
    private var signInPref: Preference? = null
    private var signOutPref: Preference? = null
    private var userPref: UserProfilePreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.currentUser.observe(this, Observer { user ->
            if (user != null && !user.isAnonymous) {
                onLoggedIn(user)
            } else {
                onUserLoggedOut()
            }
        })
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<PreferenceCategory>(getString(R.string.pref_key_debug_category))?.isVisible = BuildConfig.isMockDataEnabled
        findPreference<Preference>(getString(R.string.pref_key_mock_data))?.setOnPreferenceClickListener {
            retrofitMockClient.generateMockData()
            true
        }

        adminPref = findPreference(getString(R.string.pref_key_admin))
        adminPref?.isVisible = false
        adminPref?.setOnPreferenceClickListener {
            navigateTo(ReportModerationActivity::class.java)
            true
        }

        signInPref = findPreference<TitleSubtitlePreference>(getString(R.string.pref_sign_in))
        signInPref?.isVisible = false
        signInPref?.setOnPreferenceClickListener {
            signIn(AUTH_REQUEST_CODE)
            true
        }

        signOutPref = findPreference<TitleSubtitlePreference>(getString(R.string.pref_sign_out))
        signOutPref?.isVisible = false
        signOutPref?.setOnPreferenceClickListener {
            signOut {
                onUserLoggedOut()
                viewModel.deleteUserProfile()
            }
            true
        }

        userPref = findPreference<UserProfilePreference>(getString(R.string.pref_key_user_profile))
        userPref?.isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDivider(ColorDrawable(Color.TRANSPARENT))
        setDividerHeight(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTH_REQUEST_CODE) {
            onAuthResult(data = data, resultCode = resultCode) {
                viewModel.onUserAuthenticated()
            }
        }
    }

    private fun onLoggedIn(user: UserModel) {
        adminPref?.isVisible = user.isAdmin
        signInPref?.isVisible = false
        signOutPref?.isVisible = true
        userPref?.isVisible = true
        userPref?.setUser(user)
    }

    private fun onUserLoggedOut() {
        adminPref?.isVisible = false
        signInPref?.isVisible = true
        signOutPref?.isVisible = false
        userPref?.isVisible = false
    }
}