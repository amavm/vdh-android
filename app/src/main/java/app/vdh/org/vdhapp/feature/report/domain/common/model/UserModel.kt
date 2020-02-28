package app.vdh.org.vdhapp.feature.report.domain.common.model

data class UserModel(
        val uid: String,
        val role: String,
        val mail: String?,
        val fullName: String?,
        val pictureUrl: String,
        val isAdmin: Boolean,
        val isAnonymous: Boolean
)