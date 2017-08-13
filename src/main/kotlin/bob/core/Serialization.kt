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
import bob.core.blocks.Task
import bob.util.jsonStringOf
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.collections.immutable.toImmutableMap

private data class RawEnv(
        val id: String,
        val variables: Map<String, String>
)

fun Env.toJson() = jsonStringOf(this)

fun jsonToEnv(json: String) = try {
    val e = Gson().fromJson(json, RawEnv::class.java)

    if (e != null) Env(e.id, e.variables.toImmutableMap()) else null
} catch (_: JsonSyntaxException) {
    null
}

fun Task.toJson() = jsonStringOf(this)

fun jsonToTask(json: String) = try {
    val t = Gson().fromJson(json, Task::class.java)

    when (t) {
        null -> t
        else -> Task(
                t.id,
                t.type,
                t.command,
                t.runWhen,
                t.workingDirectory ?: "."
        )
    }
} catch (_: JsonSyntaxException) {
    null
}
