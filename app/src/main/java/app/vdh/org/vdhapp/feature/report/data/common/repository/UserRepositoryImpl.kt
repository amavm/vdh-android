package app.vdh.org.vdhapp.feature.report.data.common.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.vdh.org.vdhapp.feature.report.data.common.remote.client.user.UserApiClient
import app.vdh.org.vdhapp.feature.report.domain.common.model.UserModel
import app.vdh.org.vdhapp.feature.report.domain.common.repository.UserRepository

class UserRepositoryImpl(private val userClient: UserApiClient) : UserRepository {

    override suspend fun getCurrentUser(): LiveData<UserModel?> {
        return userClient.getCurrentUser().map { it?.toUserModel() }
    }

    override fun deleteUserProfile() {
        userClient.deleteUserProfile()
    }
}