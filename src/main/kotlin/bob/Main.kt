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

package bob

import bob.core.GenericResponse
import bob.util.jsonResponseOf
import spark.Spark
import spark.kotlin.port


fun main(args: Array<String>) {
    port(7777)

    Spark.get("/status") { req, res ->
        res.status(200)

        jsonResponseOf(res, GenericResponse("Ok"))
    }
}
