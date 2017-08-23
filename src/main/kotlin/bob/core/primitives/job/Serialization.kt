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

import bob.core.primitives.env.getEnv
import bob.core.primitives.task.getTask
import bob.util.jsonStringOf
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.collections.immutable.toImmutableList

private data class RawJob(
    val id: String?,
    val name: String,
    val envId: String?,
    val tasks: List<String>
)

// TODO 7: Remove !! when (1) is done
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

    // TODO 4: Remove when Gson has @Required
    @Suppress("SENSELESS_COMPARISON")
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
