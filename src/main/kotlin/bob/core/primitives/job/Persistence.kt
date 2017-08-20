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

package bob.core.primitives.job

import bob.core.Jobs
import bob.core.Tasks
import bob.core.primitives.env.getEnv
import bob.core.primitives.task.RunWhen
import bob.core.primitives.task.Task
import bob.core.primitives.task.TaskType
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

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
