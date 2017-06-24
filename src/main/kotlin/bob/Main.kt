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

import bob.core.setupRoutesWith
import io.vertx.core.Vertx
import io.vertx.ext.web.Router


fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    val server = vertx.createHttpServer()
    val port = 7777
    val router = Router.router(vertx)

    setupRoutesWith(router)

    println("Can we build it?")
    server.requestHandler {
        router.accept(it)
    }.listen(port) {
        if (it.succeeded()) {
            println("Yes we can!")
            println("Lets talk over port $port")
        }
        else {
            println("No we can't as: ${it.cause()}")
        }
    }
}
