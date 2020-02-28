package app.vdh.org.vdhapp.feature.report.data.common.remote.dto

import app.vdh.org.vdhapp.core.consts.ApiConst
import app.vdh.org.vdhapp.feature.report.domain.common.model.UserModel

data class UserDto(
        val uid: String,
        val role: String = ApiConst.USER_ROLE_NOT_DEFINED,
        val email: String?,
        val fullName: String?,
        val pictureUrl: String,
        val isAnonymous: Boolean
) {
    fun toUserModel() = UserModel(uid, role, email, fullName, pictureUrl, isAdmin, isAnonymous)

    private val isAdmin = role == ApiConst.USER_ROLE_ADMIN
}