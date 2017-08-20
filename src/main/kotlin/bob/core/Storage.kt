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

import bob.core.primitives.Tag
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.ReferenceOption.RESTRICT
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Envs : Table() {
    val id = varchar("id", 36).primaryKey()
}

object EnvVars : Table() {
    val id = varchar("id", 36).references(
        Envs.id, onDelete = CASCADE
    )
    val key = varchar("key", 30)
    val value = varchar("value", 50)
}

object Jobs : Table() {
    val id = varchar("id", 36).primaryKey()
    val envId = varchar("envId", 36).references(
        Envs.id, onDelete = RESTRICT
    ).nullable()
    val name = varchar("name", 50)
}

object Tags : Table() {
    val name = varchar("name", 50).primaryKey()
}

object Tasks : Table() {
    val id = varchar("id", 36).primaryKey()
    val jobId = varchar("jobId", 36).references(
        Jobs.id, onDelete = CASCADE
    )
    val type = varchar("type", 5)
    val command = varchar("command", 500)
    val runWhen = varchar("runWhen", 6)
    val workingDirectory = varchar("workingDirectory", 100)
}

fun initStorage(url: String, driver: String) {
    Database.connect(url, driver)

    transaction {
        createMissingTablesAndColumns(
            Envs,
            EnvVars,
            Jobs,
            Tasks,
            Tags
        )
    }
}

fun putTag(tag: Tag) = transaction {
    if (Tags.select { Tags.name eq tag.name }.empty()) {
        Tags.insert { it[name] = tag.name }
    }
}

fun getTag(name: String) = transaction {
    if (Tags.select { Tags.name eq name }.empty()) {
        null
    } else {
        Tag(name)
    }
}

fun delTag(name: String) = transaction {
    Tags.deleteWhere { Tags.name eq name }
}
