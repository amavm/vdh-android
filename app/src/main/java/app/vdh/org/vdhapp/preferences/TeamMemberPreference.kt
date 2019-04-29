package app.vdh.org.vdhapp.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import app.vdh.org.vdhapp.R
import kotlinx.android.synthetic.main.preference_team_member.view.*

class TeamMemberPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        holder?.let {
            when (this.key) {
                context.getString(R.string.pref_key_karim) -> {
                    onBind(R.string.settings_team_karim, R.string.settings_team_karim_role, holder)
                }
                context.getString(R.string.pref_key_yo) -> {
                    onBind(R.string.settings_team_yo, R.string.settings_team_yo_role, holder)
                }
                context.getString(R.string.pref_key_tom) -> {
                    onBind(R.string.settings_team_tom, R.string.settings_team_tom_role, holder)
                }
                context.getString(R.string.pref_key_ju) -> {
                    onBind(R.string.settings_team_julien, R.string.settings_team_julien_role, holder)
                }
            }
        }
    }

    private fun onBind(@StringRes name: Int, @StringRes role: Int, holder: PreferenceViewHolder) {
        holder.itemView.pref_team_member_name.text = context.getString(name)
        holder.itemView.pref_team_member_role.text = context.getString(role)
    }
}