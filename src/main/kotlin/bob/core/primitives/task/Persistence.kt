/*
 * This file is part of Bob.
 *
 * Bob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bob. If not, see <http://www.gnu.org/licenses/>.
 */

package bob.core.primitives.task

import bob.core.Tasks
import org.h2.jdbc.JdbcSQLException
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun putTask(task: Task) = transaction {
    try {
        when {
            bob.core.Tasks.select { Tasks.id eq task.id }.empty() -> bob.core.Tasks.insert {
                it[id] = task.id
                it[jobId] = task.jobId
                it[type] = task.type.name
                it[command] = task.command
                it[runWhen] = task.runWhen.name
                it[workingDirectory] = task.workingDirectory
            }
            else -> bob.core.Tasks.update({ Tasks.id eq task.id }) {
                it[jobId] = task.jobId
                it[type] = task.type.name
                it[command] = task.command
                it[runWhen] = task.runWhen.name
                it[workingDirectory] = task.workingDirectory
            }
        }

        true
    } catch (_: JdbcSQLException) {
        false
    }
}

fun getTask(id: String) = transaction {
    val result = bob.core.Tasks.select { Tasks.id eq id }

    when {
        result.empty() -> null
        else -> {
            val type = TaskType.valueOf(result.first()[Tasks.type])
            val runWhen = RunWhen.valueOf(result.first()[Tasks.runWhen])

            Task(
                id,
                result.first()[Tasks.jobId],
                type,
                result.first()[Tasks.command],
                runWhen,
                result.first()[Tasks.workingDirectory]
            )
        }
    }
}

fun delTask(id: String) = transaction {
    bob.core.Tasks.deleteWhere { Tasks.id eq id }
}
