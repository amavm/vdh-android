package app.vdh.org.vdhapp.feature.report.data.common.remote.dto

import com.google.gson.annotations.SerializedName

data class ObservationListDto(
    @SerializedName("items") val observationList: List<ObservationDto>,
    @SerializedName("nextToken") val nextToken: String
)