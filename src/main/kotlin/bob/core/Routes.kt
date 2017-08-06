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
import bob.core.blocks.Task
import bob.util.putIfCorrect
import bob.util.respondIfExists
import bob.util.respondWith
import bob.util.respondWith404
import bob.util.respondWithError
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.application.receive
import org.jetbrains.ktor.features.StatusPages
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.delete
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.put
import org.jetbrains.ktor.routing.route


fun Application.module() {
    install(CallLogging)

    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            respondWith404(call)
        }

        status(HttpStatusCode.InternalServerError) {
            respondWithError(call)
        }
    }

    install(Routing) {
        get("/status") {
            respondWith(call, "Ok")
        }

        route("/env") {
            route("/{id}") {
                get {
                    val id = call.parameters["id"]

                    if (id != null) {
                        respondIfExists(call, getEnv(id), Env::toJson)
                    } else {
                        respondWith404(call)
                    }
                }

                put {
                    val id = call.parameters["id"]
                    val rawVars = call.request.receive<String>()

                    if (!rawVars.isEmpty()) {
                        val envJson = """{
                            "id": "$id",
                            "variables": $rawVars
                        }"""

                        putIfCorrect(call, envJson, ::jsonToEnv, ::putEnv)
                        respondWith(call, "Ok")
                    } else {
                        respondWith(
                                call,
                                "Invalid Env provided",
                                HttpStatusCode.BadRequest
                        )
                    }
                }

                delete {
                    val id = call.parameters["id"]

                    if (id != null) {
                        delEnv(id)
                        respondWith(call, "Ok")
                    } else {
                        respondWith404(call)
                    }
                }
            }
        }

        route("/task") {
            route("/{id}") {
                get {
                    val id = call.parameters["id"]

                    if (id != null) {
                        respondIfExists(call, getTask(id), Task::toJson)
                    } else {
                        respondWith404(call)
                    }
                }

                put {
                    val id = call.parameters["id"]
                    val taskOptions = call.request.receive<String>()

                    if (!taskOptions.isEmpty()) {
                        val options = jsonToTask(taskOptions)

                        if (id != null && options != null) {
                            val task = Task(
                                    id,
                                    options.type,
                                    options.command,
                                    options.runWhen
                            )

                            putTask(task)
                            respondWith(call, "Ok")
                        } else {
                            respondWith(
                                    call,
                                    "Invalid Task options provided",
                                    HttpStatusCode.BadRequest
                            )
                        }
                    } else {
                        respondWith(
                                call,
                                "Invalid Task provided",
                                HttpStatusCode.BadRequest
                        )
                    }
                }

                delete {
                    val id = call.parameters["id"]

                    if (id != null) {
                        delTask(id)
                        respondWith(call, "Ok")
                    } else {
                        respondWith404(call)
                    }
                }
            }
        }
    }
}
