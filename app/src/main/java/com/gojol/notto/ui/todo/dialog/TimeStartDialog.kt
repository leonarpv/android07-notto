package com.gojol.notto.ui.todo.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.SET_TIME_START
import com.gojol.notto.databinding.DialogTodoTimeStartBinding
import com.gojol.notto.ui.BaseDialog
import com.gojol.notto.ui.todo.dialog.util.TimeStartDialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
class TimeStartDialog : BaseDialog<DialogTodoTimeStartBinding, TimeStartDialogViewModel>() {

    override val viewModel: TimeStartDialogViewModel by viewModels()
    override var layoutId: Int = R.layout.dialog_todo_time_start

    var setTimeCallback: ((LocalTime) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        setTimeCallback?.let { viewModel.setTimeStartCallback(it) }
        initDate()
        setClickListener()
    }

    override fun onStart() {
        super.onStart()
        resetDialogSize()
    }

    private fun resetDialogSize() {
        val deviceWidth = resources.displayMetrics.widthPixels
        val deviceHeight = resources.displayMetrics.heightPixels

        if(deviceWidth > deviceHeight) {
            val dialogWidth = deviceWidth * 0.8
            val dialogHeight = deviceHeight * 0.8
            dialog?.window?.setLayout(dialogWidth.toInt(), dialogHeight.toInt())
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog))

        val deviceWidth = resources.displayMetrics.widthPixels
        val deviceHeight = resources.displayMetrics.heightPixels
        // 가로 모드
        if (deviceWidth > deviceHeight) {
            dialog.window?.attributes?.width = (resources.displayMetrics.widthPixels * 0.8).toInt()
            dialog.window?.attributes?.height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        }
        return dialog
    }

    private fun initDate() {
        arguments?.let { arg ->
            (arg.getSerializable(SET_TIME_START) as LocalTime?)?.let {
                viewModel.setTimeStart(it)
                arg.remove(SET_TIME_START)
            }
        }
    }

    override fun initObserver() {
        viewModel.timeStart.observe(this) {
            setTime(it.hour, it.minute)
        }
    }

    private fun setTime(hour: Int, minute: Int) {
        with(binding.tpTimeStart) {
            if (Build.VERSION.SDK_INT >= 23) {
                setHour(hour)
                setMinute(minute)
            } else {
                currentHour = hour
                currentMinute = minute
            }
        }
    }

    private fun setClickListener() {
        binding.tpTimeStart.setOnTimeChangedListener { timePicker, hour, minute ->
            viewModel.setTimeStart(LocalTime.of(hour, minute))
        }
    }

    override fun confirmClick() {
        with(binding.tpTimeStart) {
            if (Build.VERSION.SDK_INT >= 23) {
                viewModel.setTimeStartCallback.value?.let { it(LocalTime.of(hour, minute)) }
            } else {
                viewModel.setTimeStartCallback.value?.let { it(LocalTime.of(currentHour, currentMinute)) }
            }
        }
        this.dismiss()
    }

    override fun dismissClick() {
        this.dismiss()
    }

    companion object {
        fun newInstance(bundle: Bundle, callback: (LocalTime) -> Unit): TimeStartDialog {
            return TimeStartDialog().apply {
                arguments = bundle
                setTimeCallback = callback
            }
        }
    }
}