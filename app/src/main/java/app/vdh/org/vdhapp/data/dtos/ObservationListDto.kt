package app.vdh.org.vdhapp.data.dtos

import com.google.gson.annotations.SerializedName

data class ObservationListDto(@SerializedName("items") val observationList: List<ObservationDto>,
                              @SerializedName("nextToken") val nextToken: String)