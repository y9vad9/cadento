package cadento.tasks.database

import cadento.tasks.sqldelight.SelectDueTasksAsc
import cadento.tasks.sqldelight.SelectDueTasksDesc
import cadento.tasks.sqldelight.SelectTaskById
import cadento.tasks.sqldelight.SelectTasksByIds
import cadento.tasks.sqldelight.SelectTasksDueInRangeAsc
import cadento.tasks.sqldelight.SelectTasksFiltered

/**
 * Mappers for SQLDelight generated query types.
 * These are separated to ensure they are testable and reusable.
 */
class SqlDelightQueryMapper {
    fun fromSelectTaskById(row: SelectTaskById): TaskRow = row.run {
        TaskRow(id, name, description, createdAt, due, statusId, status)
    }

    fun fromSelectTasksByIds(row: SelectTasksByIds): TaskRow = row.run {
        TaskRow(id, name, description, createdAt, due, statusId, status)
    }

    fun fromSelectDueTasksAsc(row: SelectDueTasksAsc): TaskRow = row.run {
        TaskRow(id, name, description, createdAt, due, statusId, status)
    }

    fun fromSelectDueTasksDesc(row: SelectDueTasksDesc): TaskRow = row.run {
        TaskRow(id, name, description, createdAt, due, statusId, status)
    }

    fun fromSelectTasksDueInRangeAsc(row: SelectTasksDueInRangeAsc): TaskRow = row.run {
        TaskRow(id, name, description, createdAt, due, statusId, status)
    }

    fun fromSelectTasksFiltered(row: SelectTasksFiltered): TaskRow = row.run {
        TaskRow(id, name, description, createdAt, due, statusId, status)
    }
}
