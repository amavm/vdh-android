package app.vdh.org.vdhapp.feature.report.data.common.remote.dto

import com.google.gson.annotations.SerializedName

data class ModerationStatus(
    @SerializedName("status") val adminStatus: String
)