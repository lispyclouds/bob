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
import bob.core.blocks.Tag
import bob.core.blocks.Task
import bob.util.deleteEntity
import bob.util.doIfParamsValid
import bob.util.respondIfExists
import bob.util.respondWith
import bob.util.respondWith404
import bob.util.respondWithBadRequest
import bob.util.respondWithError
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.StatusPages
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.delete
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.put
import org.jetbrains.ktor.routing.route

fun Application.module() {
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
                    respondIfExists(call, ::getEnv, Env::toJson)
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

                            val entity = jsonToEnv(envJson)

                            when (entity) {
                                null -> respondWithBadRequest(call)
                                else -> putEnv(entity)
                            }

                            respondWith(call, "Ok")
                        }
                    }
                }

                delete {
                    deleteEntity(call, ::delEnv)
                }
            }
        }

        route("/task") {
            route("/{id}") {
                get {
                    respondIfExists(call, ::getTask, Task::toJson)
                }

                put {
                    doIfParamsValid(call, deserializeUsing = ::jsonToTask) { id, entity ->
                        val task = Task(
                            id,
                            entity.jobId,
                            entity.type,
                            entity.command,
                            entity.runWhen,
                            entity.workingDirectory
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
                }

                delete {
                    deleteEntity(call, ::delTask)
                }
            }
        }

        route("/job") {
            route("/{id}") {
                get {
                    respondIfExists(call, ::getJob, Job::toJson)
                }

                put {
                    doIfParamsValid(call, deserializeUsing = ::jsonToJob) { id, entity ->
                        val job = Job(
                            id,
                            entity.name,
                            entity.env,
                            entity.tasks
                        )

                        putJob(job)
                        respondWith(call, "Ok")
                    }
                }

                delete {
                    deleteEntity(call, ::delJob)
                }
            }
        }

        route("/tag") {
            route("/{name}") {
                get {
                    respondIfExists(call, ::getTag, null, "name")
                }

                put {
                    val name = call.parameters["name"]

                    when (name) {
                        null -> respondWithBadRequest(call)
                        else -> {
                            putTag(Tag(name))
                            respondWith(call, "Ok")
                        }
                    }
                }

                delete {
                    deleteEntity(call, ::delTag, "name")
                }
            }
        }
    }
}
