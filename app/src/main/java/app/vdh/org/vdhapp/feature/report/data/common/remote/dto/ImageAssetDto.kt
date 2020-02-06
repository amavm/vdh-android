package app.vdh.org.vdhapp.feature.report.data.common.remote.dto

import com.google.gson.annotations.SerializedName

data class ImageAssetDto(
    @SerializedName("contentType") val type: String = "image/jpeg",
    @SerializedName("url") val imageUrl: String? = null,
    @SerializedName("data") val data: String? = null
)