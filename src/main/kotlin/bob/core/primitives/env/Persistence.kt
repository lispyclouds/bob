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

package bob.core.primitives.env

import bob.core.EnvVars
import bob.core.Envs
import kotlinx.collections.immutable.toImmutableMap
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun putEnv(env: Env) = transaction {
    // TODO 2: Optimize
    delEnv(env.id)

    bob.core.Envs.insert { it[id] = env.id }

    env.vars.forEach { envVar ->
        bob.core.EnvVars.insert {
            it[id] = env.id
            it[key] = envVar.key
            it[value] = envVar.value
        }
    }
}

fun getEnv(id: String) = transaction {
    val results = bob.core.EnvVars.select { EnvVars.id eq id }

    when {
        results.empty() -> null
        else -> {
            Env(id, results.map {
                Pair(it[EnvVars.key], it[EnvVars.value])
            }.toMap().toImmutableMap())
        }
    }
}

fun delEnv(id: String) = transaction {
    bob.core.Envs.deleteWhere { Envs.id eq id }
}
