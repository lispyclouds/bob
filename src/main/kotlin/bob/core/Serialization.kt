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

// TODO: 3: Remove when GSON has @Required
@file:Suppress("SENSELESS_COMPARISON")

package bob.core

import bob.core.blocks.Env
import bob.core.blocks.Job
import bob.core.blocks.Task
import bob.util.jsonStringOf
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

private data class RawEnv(
    val id: String,
    val variables: Map<String, String>
)

fun Env.toJson() = jsonStringOf(this)

fun jsonToEnv(json: String) = try {
    val env = Gson().fromJson(json, RawEnv::class.java)

    if (env != null) Env(env.id, env.variables.toImmutableMap()) else null
} catch (_: JsonSyntaxException) {
    null
}

fun Task.toJson() = jsonStringOf(this)

fun jsonToTask(json: String) = try {
    val task = Gson().fromJson(json, Task::class.java)

    when {
        task?.type == null || task.runWhen == null -> null
        else -> Task(
            task.id,
            task.jobId,
            task.type,
            task.command,
            task.runWhen,
            task.workingDirectory ?: "."
        )
    }
} catch (_: JsonSyntaxException) {
    null
}

private data class RawJob(
    val id: String?,
    val name: String,
    val envId: String?,
    val tasks: List<String>
)

// TODO: Remove !! when (1) is done
fun Job.toJson() = jsonStringOf(RawJob(
    this.id,
    this.name,
    this.env?.id,
    this.tasks.map { it.id!! }
))

fun jsonToJob(json: String) = try {
    val job = Gson().fromJson(json, RawJob::class.java)
    val env = when (job.envId) {
        null -> null
        "" -> null
        else -> getEnv(job.envId)
    }

    when {
        job?.tasks == null || job.name == null -> null
        else -> {
            val tasks = job.tasks.map { getTask(it) }

            when {
                tasks.any { it == null } -> null
                else -> Job(
                    job.id,
                    job.name,
                    env,
                    job.tasks.map { getTask(it)!! }.toImmutableList()
                )
            }
        }
    }
} catch (_: JsonSyntaxException) {
    null
}
