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

package core

import junit.framework.TestCase
import kotlinx.collections.immutable.immutableMapOf
import org.junit.Test


class EnvTest : TestCase() {

    @Test
    fun testAddEnvVar() {
        val vars = immutableMapOf(Pair("k1", "v1"))
        val env = Env(vars)
        val newEnv = core.addEnvVarIn(env, key="k2", value="v2")

        assertEquals(newEnv.envVars["k2"], "v2")
    }

    @Test
    fun testRemoveEnvVar() {
        val vars = immutableMapOf(Pair("k1", "v1"))
        val env = Env(vars)
        val newEnv = core.removeEnvVarFrom(env, "k1")

        assertNull(newEnv.envVars["k1"])
    }
}
