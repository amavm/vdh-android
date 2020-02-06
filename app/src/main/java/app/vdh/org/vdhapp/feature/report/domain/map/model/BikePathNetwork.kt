package app.vdh.org.vdhapp.feature.report.domain.map.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import app.vdh.org.vdhapp.R

enum class BikePathNetwork(val value: String, @ColorRes val color: Int, @StringRes val label: Int) {
    THREE_SEASONS("3-seasons", R.color.three_seasons, R.string.three_seasons),
    FOUR_SEASONS("4-seasons", R.color.four_seasons, R.string.four_seasons);

    fun next(): BikePathNetwork =
        if (this == FOUR_SEASONS) THREE_SEASONS else FOUR_SEASONS

    override fun toString(): String = value
}