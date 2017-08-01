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
import kotlinx.collections.immutable.immutableMapOf
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertNull


object SerializationTest : Spek({
    given("Bob Serialization") {

        on("converting an Env to JSON") {
            val env = Env("id1", immutableMapOf("k1" to "v1"))

            it("should give a JSON String of it") {
                assertEquals(
                        envToJson(env),
                        "{\"id\":\"id1\",\"variables\":{\"k1\":\"v1\"}}"
                )
            }
        }

        on("converting a JSON to Env") {
            val env = Env("id1", immutableMapOf("k1" to "v1"))
            val envJ = "{\"id\":\"id1\",\"variables\":{\"k1\":\"v1\"}}"

            it("should give a Env object of it") {
                assertEquals(jsonToEnv(envJ), env)
            }
        }

        on("converting a invalid JSON to Env") {
            val envJ = "{\"id\":\"id1\",\"variables\":{\"k1\":}}"

            it("should give null") {
                assertNull(jsonToEnv(envJ))
            }
        }
    }
})
