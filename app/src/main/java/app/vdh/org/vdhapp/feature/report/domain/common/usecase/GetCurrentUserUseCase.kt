package app.vdh.org.vdhapp.feature.report.domain.common.usecase

import androidx.lifecycle.LiveData
import app.vdh.org.vdhapp.feature.report.domain.common.model.UserModel
import app.vdh.org.vdhapp.feature.report.domain.common.repository.UserRepository

class GetCurrentUserUseCase(private val userRepository: UserRepository) {

    suspend fun execute(): LiveData<UserModel?> {
        return userRepository.getCurrentUser()
    }
}