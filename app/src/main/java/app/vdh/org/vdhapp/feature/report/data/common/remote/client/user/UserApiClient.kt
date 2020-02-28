package app.vdh.org.vdhapp.feature.report.data.common.remote.client.user

import androidx.lifecycle.LiveData
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.UserDto

interface UserApiClient {

    fun getCurrentUser(): LiveData<UserDto?>

    fun deleteUserProfile()

}