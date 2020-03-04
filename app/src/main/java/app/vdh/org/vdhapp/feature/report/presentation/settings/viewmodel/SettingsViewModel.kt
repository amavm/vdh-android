package app.vdh.org.vdhapp.feature.report.presentation.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import app.vdh.org.vdhapp.feature.report.domain.common.usecase.DeleteUserProfileUseCase
import app.vdh.org.vdhapp.feature.report.domain.common.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.Dispatchers

class SettingsViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val deleteUserProfileUseCase: DeleteUserProfileUseCase
) : ViewModel() {

    private val fetchUser: MutableLiveData<Boolean> = MutableLiveData()

    init {
        fetchUser.postValue(true)
    }

    val currentUser = fetchUser.switchMap {
        liveData(context = viewModelScope.coroutineContext + Dispatchers.Main) {
            val user = getCurrentUserUseCase.execute()
            emitSource(user)
        }
    }

    fun onUserAuthenticated() {
        fetchUser.value = true
    }

    fun deleteUserProfile() {
        deleteUserProfileUseCase.execute()
    }
}