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

package bob.core.blocks

import com.google.gson.annotations.SerializedName


enum class TaskType {
    @SerializedName("fetch") FETCH,
    @SerializedName("shell") SHELL
}

enum class RunWhen {
    @SerializedName("failed") FAILED,
    @SerializedName("passed") PASSED,
    @SerializedName("any") ANY
}

data class Task(
        // TODO: Find a better way to serialize
        val id: String?,
        val type: TaskType,
        val command: String,
        val runWhen: RunWhen,
        val workingDirectory: String? = "."
)
