package app.vdh.org.vdhapp.feature.report.domain.common.usecase

import app.vdh.org.vdhapp.feature.report.domain.common.repository.UserRepository

class DeleteUserProfileUseCase(private val repository: UserRepository) {

    fun execute() {
        repository.deleteUserProfile()
    }
}