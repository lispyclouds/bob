// This file is part of Bob.
//
// Bob is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Bob is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Bob. If not, see <http://www.gnu.org/licenses/>.

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router


class Server : AbstractVerticle() {
    override fun start() {
        val router = Router.router(vertx)

        router.get("/status/").handler { request ->
            request
                    .response()
                    .putHeader("content-type", "application/json")
                    .end("{\"status\": \"Ok\"}")
        }

        vertx.createHttpServer().requestHandler {
            router.accept(it)
        }.listen(7777)
    }
}
