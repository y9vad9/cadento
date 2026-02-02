package timemate.tasks.infrastructure

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import timemate.tasks.application.CreateTaskUseCase
import timemate.tasks.application.DeleteTaskUseCase
import timemate.tasks.application.DeleteTasksUseCase
import timemate.tasks.application.EditTaskUseCase
import timemate.tasks.application.GetSoonDueTasksUseCase
import timemate.tasks.application.GetTasksByTagAndStatusUseCase
import timemate.tasks.application.TaskRepository
import timemate.tasks.application.TimeZoneProvider
import timemate.tasks.database.SqlDelightQueryMapper
import timemate.tasks.database.SqlDelightTaskMapper
import timemate.tasks.database.TasksDatabaseSource
import timemate.tasks.sqldelight.TaskDatabase
import kotlin.time.Clock

@Module
class TaskFeatureModule {
    @Singleton
    fun createTaskUseCase(repository: TaskRepository): CreateTaskUseCase {
        return CreateTaskUseCase(repository)
    }

    @Singleton
    fun editTaskUseCase(repository: TaskRepository): EditTaskUseCase {
        return EditTaskUseCase(repository)
    }

    @Singleton
    fun getSoonDueTasksUseCase(
        repository: TaskRepository,
        timeZoneProvider: TimeZoneProvider,
        clock: Clock,
    ): GetSoonDueTasksUseCase {
        return GetSoonDueTasksUseCase(repository, timeZoneProvider, clock)
    }

    @Singleton
    fun getTasksByTagAndStatusUseCase(
        repository: TaskRepository,
        clock: Clock,
    ): GetTasksByTagAndStatusUseCase {
        return GetTasksByTagAndStatusUseCase(repository, clock)
    }

    @Singleton
    fun deleteTaskUseCase(repository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(repository)
    }

    @Singleton
    fun deleteTasksUseCase(repository: TaskRepository): DeleteTasksUseCase {
        return DeleteTasksUseCase(repository)
    }

    @Singleton
    fun taskRepository(sqlDriver: SqlDriver): TaskRepository {
        return TaskRepositoryImpl(
            databaseSource = TasksDatabaseSource(
                database = TaskDatabase(sqlDriver),
                taskMapper = SqlDelightTaskMapper(),
                queryMapper = SqlDelightQueryMapper(),
            ),
            dbTaskMapper = DbTaskMapper(),
        )
    }
}
