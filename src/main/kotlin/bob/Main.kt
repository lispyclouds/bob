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

package bob

import bob.util.respond
import bob.util.respondWithError
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.StatusPages
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.get


fun Application.module() {
    install(CallLogging)

    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            respondWithError(
                    call,
                    "Sorry, Not found!",
                    HttpStatusCode.NotFound
            )
        }
    }

    install(Routing) {
        get("/status") {
            respond(call, "Ok")
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(
            Netty,
            port = 7777,
            reloadPackages = listOf("MainKt"),
            module = Application::module
    ).start()
}
