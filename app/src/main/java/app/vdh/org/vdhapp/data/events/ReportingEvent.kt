package app.vdh.org.vdhapp.data.events

sealed class ReportingEvent {
    object PickPlace : ReportingEvent()
    object PickPhoto : ReportingEvent()
    object TakePhoto : ReportingEvent()
    object DeleteReport : ReportingEvent()
}