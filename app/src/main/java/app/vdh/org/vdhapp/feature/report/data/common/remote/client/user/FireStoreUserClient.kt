package app.vdh.org.vdhapp.feature.report.data.common.remote.client.user

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.vdh.org.vdhapp.core.consts.ApiConst
import app.vdh.org.vdhapp.core.consts.PrefConst
import app.vdh.org.vdhapp.feature.report.data.common.remote.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreUserClient(
    private val sharedPreferences: SharedPreferences
) : UserApiClient {

    private val db = FirebaseFirestore.getInstance()

    private fun getUser(firebaseUser: FirebaseUser?): LiveData<UserDto?> {
        val result = MutableLiveData<UserDto?>()
        val uid = firebaseUser?.uid

        if (uid == null) {
            result.postValue(null)
            return result
        }

        val user = getUserDto(firebaseUser)
        result.postValue(user)
        db.collection(ApiConst.USER_COLLECTION_NAME)
                .document(uid)
                .get()
                .addOnSuccessListener {
                    val role = it.data?.get(ApiConst.USER_ROLE_FIELD_NAME) as? String
                    result.postValue(user.copy(role = role ?: user.role))
                }
                .addOnFailureListener {
                    result.postValue(null)
                }
        return result
    }

    override fun getCurrentUser(): LiveData<UserDto?> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return getUser(currentUser)
    }

    override fun deleteUserProfile() {
        sharedPreferences.edit()
                .remove(PrefConst.USER_EMAIL_PREFS_KEY)
                .remove(PrefConst.USER_NAME_PREFS_KEY)
                .remove(PrefConst.USER_PHOTO_URL_PREFS_KEY)
                .apply()
    }

    private fun getUserDto(firebaseUser: FirebaseUser): UserDto {
        val prefEdit = sharedPreferences.edit()

        val email = if (firebaseUser.email != null) {
            prefEdit.putString(PrefConst.USER_EMAIL_PREFS_KEY, firebaseUser.email)
            firebaseUser.email
        } else {
            sharedPreferences.getString(PrefConst.USER_EMAIL_PREFS_KEY, null)
        }

        val name = if (firebaseUser.displayName != null) {
            prefEdit.putString(PrefConst.USER_NAME_PREFS_KEY, firebaseUser.displayName)
            firebaseUser.displayName
        } else {
            sharedPreferences.getString(PrefConst.USER_NAME_PREFS_KEY, null)
        }

        val picture = if (firebaseUser.photoUrl != null) {
            prefEdit.putString(PrefConst.USER_PHOTO_URL_PREFS_KEY, firebaseUser.photoUrl.toString())
            firebaseUser.photoUrl.toString()
        } else {
            sharedPreferences.getString(PrefConst.USER_PHOTO_URL_PREFS_KEY, "") ?: ""
        }

        prefEdit.apply()

        return UserDto(
                uid = firebaseUser.uid,
                isAnonymous = firebaseUser.isAnonymous,
                email = email,
                fullName = name,
                pictureUrl = picture

        )
    }
}