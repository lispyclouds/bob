/**
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

package bob.core

import bob.core.blocks.Env
import bob.core.blocks.RunWhen
import bob.core.blocks.Task
import bob.core.blocks.TaskType
import kotlinx.collections.immutable.toImmutableMap
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


private object Envs : Table() {
    val id = varchar("id", 36).primaryKey()
}

private object EnvVars : Table() {
    val id = varchar("id", 36).references(
            Envs.id, onDelete = CASCADE
    )
    val key = varchar("key", 30)
    val value = varchar("value", 50)
}

private object Tasks : Table() {
    val id = varchar("id", 36).primaryKey()
    val type = varchar("type", 5)
    val command = varchar("command", 500)
    val runWhen = varchar("runWhen", 6)
    val workingDirectory = varchar("workingDirectory", 100)
}

fun initStorage(url: String, driver: String) {
    Database.connect(url, driver)

    transaction {
        createMissingTablesAndColumns(
                Envs,
                EnvVars,
                Tasks
        )
    }
}

fun putEnv(env: Env) = transaction {
    // TODO: Optimize
    delEnv(env.id)

    Envs.insert { it[id] = env.id }

    env.vars.forEach { envVar ->
        EnvVars.insert {
            it[id] = env.id
            it[key] = envVar.key
            it[value] = envVar.value
        }
    }
}

fun getEnv(id: String) = transaction {
    val results = EnvVars.select { EnvVars.id eq id }

    if (results.empty()) {
        null
    } else {
        Env(id, results.map {
            Pair(it[EnvVars.key], it[EnvVars.value])
        }.toMap().toImmutableMap())
    }
}

fun delEnv(id: String) = transaction {
    Envs.deleteWhere { Envs.id eq id }
}

fun putTask(task: Task): Unit = transaction {
    if (Tasks.select { Tasks.id eq task.id }.empty()) {
        Tasks.insert {
            it[id] = task.id
            it[type] = task.type.name
            it[command] = task.command
            it[runWhen] = task.runWhen.name
            it[workingDirectory] = task.workingDirectory
        }
    } else {
        Tasks.update({ Tasks.id eq task.id }) {
            it[type] = task.type.name
            it[command] = task.command
            it[runWhen] = task.runWhen.name
            it[workingDirectory] = task.workingDirectory
        }
    }
}

fun getTask(id: String) = transaction {
    val result = Tasks.select { Tasks.id eq id }

    if (result.empty()) {
        null
    } else {
        val type = TaskType.valueOf(result.first()[Tasks.type])
        val runWhen = RunWhen.valueOf(result.first()[Tasks.runWhen])

        Task(
                id,
                type,
                result.first()[Tasks.command],
                runWhen,
                result.first()[Tasks.workingDirectory]
        )
    }
}

fun delTask(id: String) = transaction {
    Tasks.deleteWhere { Tasks.id eq id }
}
