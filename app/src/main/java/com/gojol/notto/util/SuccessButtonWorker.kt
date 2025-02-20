package com.gojol.notto.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gojol.notto.common.NOTIFICATION_TODO
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.datasource.todo.TodoAlarmManager
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.ZoneId
import java.util.*

@HiltWorker
class SuccessButtonWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: TodoLabelRepository,
    private val alarmManager: TodoAlarmManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = runCatching {
        onButtonClick()
        Result.success()
    }.getOrElse {
        Log.d(this.javaClass.name, it.toString())
        Result.failure()
    }

    private suspend fun onButtonClick() {
        val currentDate =
            Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate()
        val todoId = inputData.getInt(NOTIFICATION_TODO, -1)

        repository.getTodosWithTodayDailyTodos(currentDate).filter {
            it.todo.todoId == todoId
        }.forEach {
            repository.updateDailyTodo(it.todayDailyTodo.copy(todoState = TodoState.SUCCESS))
            alarmManager.deleteAlarm(it.todo, TodoState.SUCCESS)
        }
    }
}
