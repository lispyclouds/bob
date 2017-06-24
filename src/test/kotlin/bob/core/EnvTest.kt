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

import kotlinx.collections.immutable.immutableMapOf
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertNull


object EnvTest : Spek({
    given("A Bob Env") {
        on("adding an environment variable") {
            val vars = immutableMapOf(Pair("k1", "v1"))
            val env = Env(vars)
            val newEnv = addEnvVarIn(env, key = "k2", value = "v2")

            it("should give a new Env with the added variable") {
                assertEquals(newEnv.envVars["k2"], "v2")
            }
        }

        on("removing an environment variable") {
            val vars = immutableMapOf(Pair("k1", "v1"))
            val env = Env(vars)
            val newEnv = removeEnvVarFrom(env, "k1")

            it("should give a new Env without the variable") {
                assertNull(newEnv.envVars["k1"])
            }
        }
    }
})
