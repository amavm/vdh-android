package app.vdh.org.vdhapp.feature.report.domain.common.repository

import androidx.lifecycle.LiveData
import app.vdh.org.vdhapp.feature.report.domain.common.model.UserModel

interface UserRepository {

    suspend fun getCurrentUser(): LiveData<UserModel?>

    fun deleteUserProfile()
}
