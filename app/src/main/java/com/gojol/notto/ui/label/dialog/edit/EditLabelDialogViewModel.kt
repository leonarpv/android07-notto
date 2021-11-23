package com.gojol.notto.ui.label.dialog.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gojol.notto.common.LabelEditType
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import com.gojol.notto.ui.DialogViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLabelDialogViewModel @Inject constructor(private val repository: TodoLabelRepository) :
    DialogViewModel() {

    private val _type = MutableLiveData<LabelEditType>()
    val type: LiveData<LabelEditType> = _type

    private val _label = MutableLiveData<Label>()
    val label: LiveData<Label> = _label

    val title = MutableLiveData<String>()
    val name = MutableLiveData<String>()

    fun clickOkay() {
        val labelName = name.value ?: return
        // TODO: 에러 메시지 출력

        when (type.value) {
            LabelEditType.CREATE -> createLabel(Label(1000, labelName))
            LabelEditType.UPDATE -> {
                val updatedLabel = label.value ?: return
                updateLabel(updatedLabel.copy(name = labelName))
            }
            else -> return
        }
    }

    private fun createLabel(label: Label) {
        viewModelScope.launch {
            repository.insertLabel(label)
        }
    }

    private fun updateLabel(label: Label) {
        viewModelScope.launch {
            repository.updateLabel(label)
        }
    }

    fun setEditType(label: Label?) {
        if (label == null) {
            _type.value = LabelEditType.CREATE
            title.value = LabelEditType.CREATE.text
        } else {
            _type.value = LabelEditType.UPDATE
            title.value = LabelEditType.UPDATE.text
            _label.value = label!!
            name.value = label.name
        }
    }
}
