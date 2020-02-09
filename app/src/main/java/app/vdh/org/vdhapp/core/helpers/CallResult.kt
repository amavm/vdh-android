package app.vdh.org.vdhapp.core.helpers

sealed class CallResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : CallResult<T>()
    data class Error(val exception: Exception) : CallResult<Nothing>()
}

suspend fun <T : Any> safeCall(call: suspend () -> CallResult<T>, errorMessage: String): CallResult<T> = try {
    call.invoke()
} catch (e: Exception) {
    CallResult.Error(Exception(errorMessage, e))
}