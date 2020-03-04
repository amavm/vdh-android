package app.vdh.org.vdhapp.feature.report.presentation.settings.preferences

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import app.vdh.org.vdhapp.R
import app.vdh.org.vdhapp.feature.report.domain.common.model.UserModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class UserProfilePreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    private var user: UserModel? = null

    fun setUser(userModel: UserModel) {
        user = userModel
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        val picture = holder?.itemView?.findViewById<ImageView>(R.id.pref_user_picture)
        val name = holder?.itemView?.findViewById<TextView>(R.id.pref_user_name)
        val email = holder?.itemView?.findViewById<TextView>(R.id.pref_user_mail)

        name?.text = user?.fullName
        email?.text = user?.mail
        picture?.let {
            user?.let {

                Glide.with(context)
                        .load(it.pictureUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(picture)
            }
        }
    }
}