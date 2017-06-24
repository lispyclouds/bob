/* This file is part of Bob.
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

import kotlinx.collections.immutable.ImmutableMap


data class Env(val envVars: ImmutableMap<String, String>)

fun addEnvVarIn(env: Env, key: String, value: String): Env {
    return Env(env.envVars.put(key, value))
}

fun removeEnvVarFrom(env: Env, key: String): Env {
    return Env(env.envVars.remove(key))
}