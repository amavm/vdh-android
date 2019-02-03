package app.vdh.org.vdhapp.data.events

import app.vdh.org.vdhapp.data.models.Status

sealed class StatusFilterEvent {
    data class PickStatusFilter(val status: Status?) : StatusFilterEvent()
}