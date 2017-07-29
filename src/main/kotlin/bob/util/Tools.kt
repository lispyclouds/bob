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

package bob.util

import bob.core.GenericResponse
import com.google.gson.Gson
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.response.respondText


fun generateID() = java.util.UUID.randomUUID().toString()

fun <T> jsonStringOf(obj: T): String = Gson().toJson(obj)

suspend fun respond(call: ApplicationCall, message: String) {
    call.respondText(
            jsonStringOf(GenericResponse(message)),
            ContentType.Application.Json
    )
}

suspend fun respondWithError(call: ApplicationCall, message: String, errorCode: HttpStatusCode) {
    call.response.status(errorCode)
    call.respondText(
            jsonStringOf(GenericResponse(message)),
            ContentType.Application.Json
    )
}
