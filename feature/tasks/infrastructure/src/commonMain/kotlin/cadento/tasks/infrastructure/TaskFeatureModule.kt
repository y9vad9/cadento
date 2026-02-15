package cadento.tasks.infrastructure

import app.cash.sqldelight.db.SqlDriver
import cadento.tasks.application.CreateTaskUseCase
import cadento.tasks.application.DeleteTaskUseCase
import cadento.tasks.application.DeleteTasksUseCase
import cadento.tasks.application.EditTaskUseCase
import cadento.tasks.application.GetSoonDueTasksUseCase
import cadento.tasks.application.GetTasksByTagAndStatusUseCase
import cadento.tasks.application.TaskRepository
import cadento.tasks.application.TimeZoneProvider
import cadento.tasks.database.SqlDelightQueryMapper
import cadento.tasks.database.SqlDelightTaskMapper
import cadento.tasks.database.TasksDatabaseSource
import cadento.tasks.sqldelight.TaskDatabase
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import kotlin.time.Clock

@Module
class TaskFeatureModule {
    @Single
    fun createTaskUseCase(repository: TaskRepository): CreateTaskUseCase {
        return CreateTaskUseCase(repository)
    }

    @Single
    fun editTaskUseCase(repository: TaskRepository): EditTaskUseCase {
        return EditTaskUseCase(repository)
    }

    @Single
    fun getSoonDueTasksUseCase(
        repository: TaskRepository,
        timeZoneProvider: TimeZoneProvider,
        clock: Clock,
    ): GetSoonDueTasksUseCase {
        return GetSoonDueTasksUseCase(repository, timeZoneProvider, clock)
    }

    @Single
    fun getTasksByTagAndStatusUseCase(
        repository: TaskRepository,
        clock: Clock,
    ): GetTasksByTagAndStatusUseCase {
        return GetTasksByTagAndStatusUseCase(repository, clock)
    }

    @Single
    fun deleteTaskUseCase(repository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(repository)
    }

    @Single
    fun deleteTasksUseCase(repository: TaskRepository): DeleteTasksUseCase {
        return DeleteTasksUseCase(repository)
    }

    @Single
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
