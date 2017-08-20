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

package bob.util

import bob.core.GenericResponse
import bob.core.asJsonString
import com.google.gson.Gson
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respondText

fun generateID() = java.util.UUID.randomUUID().toString()

fun <T> jsonStringOf(obj: T): String = Gson().toJson(obj)

suspend fun respondWith(call: ApplicationCall, message: String,
                        status: HttpStatusCode = HttpStatusCode.OK) {
    call.response.status(status)
    call.respondText(
        GenericResponse(message).asJsonString(),
        ContentType.Application.Json
    )
}

suspend fun respondWith404(call: ApplicationCall) {
    call.response.status(HttpStatusCode.NotFound)
    call.respondText(
        GenericResponse("Sorry, Not found!").asJsonString(),
        ContentType.Application.Json
    )
}

suspend fun respondWithError(call: ApplicationCall) {
    call.response.status(HttpStatusCode.InternalServerError)
    call.respondText(
        GenericResponse("Internal Error happened!").asJsonString(),
        ContentType.Application.Json
    )
}

suspend fun respondWithBadRequest(call: ApplicationCall) {
    call.response.status(HttpStatusCode.BadRequest)
    call.respondText(
        GenericResponse("Bad request: Please check the params supplied")
            .asJsonString(),
        ContentType.Application.Json
    )
}

suspend fun <T> respondIfExists(call: ApplicationCall,
                                getUsing: (String) -> T?,
                                serializeUsing: ((T) -> String)?,
                                param: String = "id") {
    val id = call.parameters[param]

    when (id) {
        null -> respondWithBadRequest(call)
        else -> {
            val entity = getUsing(id)

            when (entity) {
                null -> respondWith404(call)
                else -> {
                    call.response.status(HttpStatusCode.OK)

                    if (serializeUsing != null) {
                        call.respondText(
                            serializeUsing(entity),
                            ContentType.Application.Json
                        )
                    } else {
                        respondWith(call, "Ok")
                    }
                }
            }
        }
    }
}

suspend fun deleteEntity(call: ApplicationCall, using: (String) -> Int,
                         param: String = "id") {
    val id = call.parameters[param]

    when (id) {
        null -> respondWithBadRequest(call)
        else -> {
            using(id)
            respondWith(call, "Ok")
        }
    }
}

suspend fun <T> doIfParamsValid(call: ApplicationCall, param: String = "id",
                                deserializeUsing: (String) -> T?,
                                toDo: suspend (String, T) -> Unit) {
    val id = call.parameters[param]
    val options = call.receive<String>()

    when {
        id == null || options.isEmpty() -> respondWithBadRequest(call)
        else -> {
            val entity = deserializeUsing(options)

            when (entity) {
                null -> respondWithBadRequest(call)
                else -> toDo(id, entity)
            }
        }
    }
}
