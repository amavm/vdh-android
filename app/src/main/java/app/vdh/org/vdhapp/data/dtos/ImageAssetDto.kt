package app.vdh.org.vdhapp.data.dtos

import com.google.gson.annotations.SerializedName

data class ImageAssetDto(@SerializedName("contentType") val type: String = "image/jpeg",
                         @SerializedName("data") val data: String)