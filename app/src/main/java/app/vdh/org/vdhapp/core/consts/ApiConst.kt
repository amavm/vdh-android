package app.vdh.org.vdhapp.core.consts

object ApiConst {
    const val BASE_URL = "https://ohp6vrr7xd.execute-api.ca-central-1.amazonaws.com/dev/api/v1/"

    // VDH Api

    const val MODERATION_STATUS_PENDING = "pending"
    const val MODERATION_STATUS_VALID = "valid"
    const val MODERATION_STATUS_REJECTED = "rejected"

    // FireStore

    const val USER_COLLECTION_NAME = "users"
    const val USER_ROLE_FIELD_NAME = "role"
    const val USER_ROLE_ADMIN = "admin"
    const val USER_ROLE_NOT_DEFINED = "not_defined"
}