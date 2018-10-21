package app.vdh.org.vdhapp.data.states

sealed class ReportingActionState {
    object PickPlace : ReportingActionState()
    object PickPhoto : ReportingActionState()
}