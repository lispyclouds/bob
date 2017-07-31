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
import bob.util.jsonStringOf
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.collections.immutable.toImmutableMap


private data class RawEnv(val id: String, val envVars: Map<String, String>)

// TODO: Use GSON for better field names for Env
fun envToJson(env: Env) = jsonStringOf(env)

fun jsonToEnv(json: String) = try {
    val e = Gson().fromJson(json, RawEnv::class.java)

    if (e != null) Env(e.id, e.envVars.toImmutableMap()) else null
} catch (_: JsonSyntaxException) {
    null
}
