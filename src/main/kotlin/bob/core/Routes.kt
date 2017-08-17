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

import bob.core.blocks.Env
import bob.core.blocks.Job
import bob.core.blocks.Task
import bob.util.putIfCorrect
import bob.util.respondIfExists
import bob.util.respondWith
import bob.util.respondWith404
import bob.util.respondWithError
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.features.StatusPages
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.request.receive
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

        // TODO: 5: Unit/Integration Test these routes.
        route("/env") {
            route("/{id}") {
                get {
                    val id = call.parameters["id"]

                    when (id) {
                        null -> respondWith404(call)
                        else -> respondIfExists(call, getEnv(id), Env::toJson)
                    }
                }

                put {
                    val id = call.parameters["id"]
                    val rawVars = call.receive<String>()

                    when {
                        rawVars.isEmpty() -> {
                            respondWith(
                                call,
                                "Invalid Env provided",
                                HttpStatusCode.BadRequest
                            )
                        }
                        else -> {
                            val envJson = """{
                                "id": "$id",
                                "variables": $rawVars
                            }"""

                            putIfCorrect(call, envJson, ::jsonToEnv, ::putEnv)
                            respondWith(call, "Ok")
                        }
                    }
                }

                delete {
                    val id = call.parameters["id"]

                    when (id) {
                        null -> respondWith404(call)
                        else -> {
                            delEnv(id)
                            respondWith(call, "Ok")
                        }
                    }
                }
            }
        }

        route("/task") {
            route("/{id}") {
                get {
                    val id = call.parameters["id"]

                    when (id) {
                        null -> respondWith404(call)
                        else -> respondIfExists(call, getTask(id), Task::toJson)
                    }
                }

                put {
                    val id = call.parameters["id"]
                    val taskOptions = call.receive<String>()

                    when {
                        taskOptions.isEmpty() -> {
                            respondWith(
                                call,
                                "Invalid Task provided",
                                HttpStatusCode.BadRequest
                            )
                        }
                        else -> {
                            val options = jsonToTask(taskOptions)

                            when {
                                id != null && options != null -> {
                                    val task = Task(
                                        id,
                                        options.jobId,
                                        options.type,
                                        options.command,
                                        options.runWhen,
                                        options.workingDirectory
                                    )

                                    if (putTask(task))
                                        respondWith(call, "Ok")
                                    else
                                        respondWith(
                                            call,
                                            "Invalid Job ID",
                                            HttpStatusCode.BadRequest
                                        )
                                }
                                else -> {
                                    respondWith(
                                        call,
                                        "Invalid Task options provided",
                                        HttpStatusCode.BadRequest
                                    )
                                }
                            }
                        }
                    }
                }

                delete {
                    val id = call.parameters["id"]

                    when (id) {
                        null -> respondWith404(call)
                        else -> {
                            delTask(id)
                            respondWith(call, "Ok")
                        }
                    }
                }
            }
        }

        route("/job") {
            route("/{id}") {
                get {
                    val id = call.parameters["id"]

                    when (id) {
                        null -> respondWith404(call)
                        else -> respondIfExists(call, getJob(id), Job::toJson)
                    }
                }

                put {
                    val id = call.parameters["id"]
                    val jobOptions = call.receive<String>()

                    when {
                        jobOptions.isEmpty() -> {
                            respondWith(
                                call,
                                "Invalid Job provided",
                                HttpStatusCode.BadRequest
                            )
                        }
                        else -> {
                            val options = jsonToJob(jobOptions)

                            when {
                                id != null && options != null -> {
                                    val job = Job(
                                        id,
                                        options.env,
                                        options.tasks
                                    )

                                    putJob(job)
                                    respondWith(call, "Ok")
                                }
                                else -> {
                                    respondWith(
                                        call,
                                        "Invalid parameters provided",
                                        HttpStatusCode.BadRequest
                                    )
                                }
                            }
                        }
                    }
                }

                delete {
                    val id = call.parameters["id"]

                    when (id) {
                        null -> respondWith404(call)
                        else -> {
                            delJob(id)
                            respondWith(call, "Ok")
                        }
                    }
                }
            }
        }
    }
}
