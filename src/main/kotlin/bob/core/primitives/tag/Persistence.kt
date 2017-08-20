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

package bob.core.primitives.tag

import bob.core.Tags
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

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
