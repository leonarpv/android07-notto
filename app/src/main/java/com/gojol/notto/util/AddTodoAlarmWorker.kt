package com.gojol.notto.util

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gojol.notto.common.TodoState
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.lang.Exception
import java.time.ZoneId
import java.util.*

@HiltWorker
class AddTodoAlarmWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: TodoLabelRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            recreateAlarm()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun recreateAlarm() {
        val currentDate =
            Date(System.currentTimeMillis()).toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate()
        repository.getTodosWithTodayDailyTodos(currentDate).forEach {
            val todo = it.todo
            val dailyTodo = it.todayDailyTodo
            if (todo.hasAlarm && dailyTodo.todoState != TodoState.SUCCESS) {
                repository.addAlarm(todo)
            }
        }
    }
}
