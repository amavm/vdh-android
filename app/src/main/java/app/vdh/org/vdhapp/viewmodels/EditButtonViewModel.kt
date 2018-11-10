package app.vdh.org.vdhapp.viewmodels

class EditButtonViewModel(val visible: Boolean, val onClick: () -> Unit) {

    fun onEditButtonClick() = onClick()
}