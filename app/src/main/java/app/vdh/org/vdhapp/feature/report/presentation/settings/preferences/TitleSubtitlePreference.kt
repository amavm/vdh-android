package app.vdh.org.vdhapp.feature.report.presentation.settings.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import app.vdh.org.vdhapp.R
import kotlinx.android.synthetic.main.preference_title_subtitle.view.*

class TitleSubtitlePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        holder?.let {
            when (this.key) {
                context.getString(R.string.pref_key_feedback) -> {
                    onBind(R.string.settings_feedback_title, R.string.settings_feedback_summary, holder)
                }
                context.getString(R.string.pref_sign_in) -> {
                    onBind(R.string.settings_sign_in_title, R.string.settings_sign_in_subtitle, holder)
                }
                context.getString(R.string.pref_sign_out) -> {
                    onBind(R.string.settings_sign_out_title, null, holder)
                }
                context.getString(R.string.pref_key_admin) -> {
                    onBind(R.string.settings_admin_title, R.string.settings_admin_subtitle, holder)
                }
            }
        }
    }

    private fun onBind(@StringRes title: Int, @StringRes subtitle: Int?, holder: PreferenceViewHolder) {
        holder.itemView.pref_contact_title.text = context.getString(title)
        if (subtitle != null) {
            holder.itemView.pref_contact_subtitle.visibility = View.VISIBLE
            holder.itemView.pref_contact_subtitle.text = context.getString(subtitle)
        } else {
            holder.itemView.pref_contact_subtitle.visibility = View.GONE
        }
    }
}