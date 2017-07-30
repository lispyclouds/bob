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

import java.io.File
import java.sql.Connection
import java.sql.DriverManager


private fun obtainConnection(): Connection {
    Class.forName("org.sqlite.JDBC")
    return DriverManager.getConnection(
            "jdbc:sqlite:${System.getProperty("user.home")}${File.separator}.bob.db"
    )
}

private fun initEnvStorage(conn: Connection) {
    val stmt = conn.createStatement()
    val envSQL = """
        CREATE TABLE IF NOT EXISTS envs (
            id TEXT PRIMARY KEY
        );
    """
    val varSQL = """
        CREATE TABLE IF NOT EXISTS vars (
            id    TEXT NOT NULL,
            key   TEXT NOT NULL,
            value TEXT NOT NULL,

            FOREIGN KEY(id) REFERENCES envs(id)
        );
    """

    stmt.execute(envSQL)
    stmt.execute(varSQL)

    stmt.close()
}

fun initStorage() {
    val conn = obtainConnection()

    initEnvStorage(conn)

    conn.close()
}
