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
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertNull


object PersistenceTest : Spek({
    given("Bob Storage") {
        val db = File.createTempFile("bob", ".db")

        initStorage(
                url = "jdbc:h2:${db.absolutePath}",
                driver = "org.h2.Driver"
        )

        on("saving an Env") {
            putEnv(Env("id1", immutableMapOf("k1" to "v1")))

            it("should save to DB") {
                assertNotNull(getEnv("id1"))
            }
        }

        on("deleting an Env") {
            putEnv(Env("id1", immutableMapOf("k1" to "v1")))
            delEnv("id1")

            it("should return null on fetch") {
                assertNull(getEnv("id1"))
            }
        }

        db.deleteOnExit()
    }
})
