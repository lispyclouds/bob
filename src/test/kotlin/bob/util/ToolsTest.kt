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

package bob.util

import bob.core.GenericResponse
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

object ToolsTest : Spek({
    given("Bob Tools") {
        on("generating two IDs") {
            val id1 = generateID()
            val id2 = generateID()

            it("should not be equal") {
                assertNotEquals(id1, id2)
            }
        }

        on("calling jsonStringOf with an object") {
            val res = GenericResponse("Ok")

            it("should give a JSON string") {
                assertTrue(jsonStringOf(res) == "{\"message\":\"Ok\"}")
            }
        }
    }
})
