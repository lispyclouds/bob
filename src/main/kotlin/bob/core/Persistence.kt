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

package bob.core

import bob.core.blocks.Env
import bob.core.blocks.Job
import bob.core.blocks.RunWhen
import bob.core.blocks.Tag
import bob.core.blocks.Task
import bob.core.blocks.TaskType
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import org.h2.jdbc.JdbcSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.ReferenceOption.RESTRICT
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
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

private object Jobs : Table() {
    val id = varchar("id", 36).primaryKey()
    val envId = varchar("envId", 36).references(
        Envs.id, onDelete = RESTRICT
    ).nullable()
    val name = varchar("name", 50)
}

private object Tags : Table() {
    val name = varchar("name", 50).primaryKey()
}

private object Tasks : Table() {
    val id = varchar("id", 36).primaryKey()
    val jobId = varchar("jobId", 36).references(
        Jobs.id, onDelete = CASCADE
    )
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
            Jobs,
            Tasks,
            Tags
        )
    }
}

fun putEnv(env: Env) = transaction {
    // TODO: 2: Optimize
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

    when {
        results.empty() -> null
        else -> {
            Env(id, results.map {
                Pair(it[EnvVars.key], it[EnvVars.value])
            }.toMap().toImmutableMap())
        }
    }
}

fun delEnv(id: String) = transaction {
    Envs.deleteWhere { Envs.id eq id }
}

fun putTask(task: Task) = transaction {
    try {
        when {
            Tasks.select { Tasks.id eq task.id }.empty() -> Tasks.insert {
                it[id] = task.id
                it[jobId] = task.jobId
                it[type] = task.type.name
                it[command] = task.command
                it[runWhen] = task.runWhen.name
                it[workingDirectory] = task.workingDirectory
            }
            else -> Tasks.update({ Tasks.id eq task.id }) {
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
    val result = Tasks.select { Tasks.id eq id }

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
    Tasks.deleteWhere { Tasks.id eq id }
}

fun putJob(job: Job): Unit = transaction {
    when {
        Jobs.select { Jobs.id eq job.id }.empty() -> Jobs.insert {
            it[id] = job.id
            it[envId] = job.env?.id
            it[name] = job.name
        }
        else -> {
            Jobs.update({ Jobs.id eq job.id }) {
                it[envId] = job.env?.id
                it[name] = job.name
            }

            val associatedTasks = job.tasks.map { it.id }
            Tasks.deleteWhere {
                Tasks.jobId eq job.id and (Tasks.id notInList associatedTasks)
            }
        }
    }
}

fun getJob(id: String) = transaction {
    val jobs = Jobs.select { Jobs.id eq id }

    when {
        jobs.empty() -> null
        else -> {
            val tasks = Tasks.select { Tasks.jobId eq id }.map {
                Task(
                    it[Tasks.id],
                    it[Tasks.jobId],
                    TaskType.valueOf(it[Tasks.type]),
                    it[Tasks.command],
                    RunWhen.valueOf(it[Tasks.runWhen]),
                    it[Tasks.workingDirectory]
                )
            }.toImmutableList()

            val eId = jobs.first()[Jobs.envId]
            val name = jobs.first()[Jobs.name]
            val env = when (eId) {
                null -> null
                else -> getEnv(eId)
            }

            Job(id, name, env, tasks)
        }
    }
}

fun delJob(id: String) = transaction {
    Jobs.deleteWhere { Jobs.id eq id }
}

fun putTag(tag: Tag) = transaction {
    if (Tags.select { Tags.name eq tag.name }.empty()) {
        Tags.insert { it[name] = tag.name }
    }
}

fun getTag(name: String) = transaction {
    if (Tags.select { Tags.name eq name }.empty()) {
        null
    } else {
        Tag(name)
    }
}

fun delTag(name: String) = transaction {
    Tags.deleteWhere { Tags.name eq name }
}
