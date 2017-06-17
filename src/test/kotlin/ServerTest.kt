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

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(VertxUnitRunner::class)
class ServerTest {

    private var vertx: Vertx? = null
    private val port: Int = 7777

    @Before
    @Throws(IOException::class)
    fun setUp(context: TestContext) {
        vertx = Vertx.vertx()

        val options = DeploymentOptions().setConfig(JsonObject().put("http.port", port))

        vertx!!.deployVerticle(Server::class.java.name, options,
                context.asyncAssertSuccess<String>())
    }

    @After
    fun tearDown(context: TestContext) {
        vertx!!.close(context.asyncAssertSuccess<Void>())
    }

    @Test
    fun testServerStatusRoute(context: TestContext) {
        val async = context.async()

        vertx!!.createHttpClient().getNow(port, "localhost", "/status") { response ->
            response.handler {
                context.assertEquals(response.statusCode(), 200)
                context.assertEquals(response.headers().get("content-type"), "application/json")

                response.bodyHandler { body ->
                    val responseJSON = JsonObject(body.toString())
                    context.assertEquals(responseJSON.getString("status"), "Ok")
                    async.complete()
                }

                async.complete()
            }
        }
    }

    @Test
    fun testServerNonExistentRoute(context: TestContext) {
        val async = context.async()

        vertx!!.createHttpClient().getNow(port, "localhost", "/does-not-exist") { response ->
            response.handler {
                context.assertEquals(response.statusCode(), 404)
                async.complete()
            }
        }
    }
}
