package app.vdh.org.vdhapp.data.models

enum class BikePathNetwork(val value: String) {
    THREE_SEASONS("3-seasons"),
    FOUR_SEASONS("4-seasons");

    fun next(): BikePathNetwork =
        if (this == FOUR_SEASONS) THREE_SEASONS else FOUR_SEASONS


    override fun toString(): String = value
}