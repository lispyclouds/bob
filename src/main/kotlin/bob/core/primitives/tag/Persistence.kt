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
