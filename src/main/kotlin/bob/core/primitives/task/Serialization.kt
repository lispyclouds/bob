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

package bob.core.primitives.task

import bob.util.jsonStringOf
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

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
