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

import bob.core.primitives.env.Env
import bob.core.primitives.env.jsonToEnv
import bob.core.primitives.env.putEnv
import bob.core.primitives.env.toJson
import bob.core.primitives.job.Job
import bob.core.primitives.job.jsonToJob
import bob.core.primitives.job.putJob
import bob.core.primitives.job.toJson
import bob.core.primitives.task.RunWhen
import bob.core.primitives.task.Task
import bob.core.primitives.task.TaskType
import bob.core.primitives.task.jsonToTask
import bob.core.primitives.task.putTask
import bob.core.primitives.task.toJson
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.immutableMapOf
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull

object SerializationTest : Spek({
    given("Bob Serialization") {

        on("converting an Env to JSON") {
            val env = Env("id1", immutableMapOf("k1" to "v1"))

            it("should give a JSON String of it") {
                assertEquals(
                    "{\"id\":\"id1\",\"variables\":{\"k1\":\"v1\"}}",
                    env.toJson()
                )
            }
        }

        on("converting a JSON to Env") {
            val env = Env("id1", immutableMapOf("k1" to "v1"))
            val envJ = "{\"id\":\"id1\",\"variables\":{\"k1\":\"v1\"}}"

            it("should give a Env object of it") {
                assertEquals(jsonToEnv(envJ), env)
            }
        }

        on("converting a invalid JSON to Env") {
            val envJ = "{\"id\":\"id1\",\"variables\":{\"k1\":}}"

            it("should give null") {
                assertNull(jsonToEnv(envJ))
            }
        }

        on("converting a Task to JSON") {
            val task = Task("id1", "job1", TaskType.FETCH, "ls",
                RunWhen.PASSED)

            it("should give a JSON String of it") {
                assertEquals(
                    "{\"id\":\"id1\",\"jobId\":\"job1\"," +
                        "\"type\":\"fetch\",\"command\":\"ls\"," +
                        "\"runWhen\":\"passed\"," +
                        "\"workingDirectory\":\".\"}",
                    task.toJson()
                )
            }
        }

        on("converting a JSON to Task") {
            val task = Task("id1", "job1", TaskType.FETCH, "ls",
                RunWhen.PASSED)
            val taskJ = "{\"id\":\"id1\",\"jobId\":\"job1\"," +
                "\"type\":\"fetch\",\"command\":\"ls\"," +
                "\"runWhen\":\"passed\"}"

            it("should give a Task object of it") {
                assertEquals(jsonToTask(taskJ), task)
            }
        }

        on("converting a invalid JSON to Task") {
            val taskJ = "{\"id\":\"id1\",\"type\":\"fetch\"," +
                "\"command\":\"ls\",\"runWhen\":}"

            it("should give null") {
                assertNull(jsonToTask(taskJ))
            }
        }

        on("converting a Job to JSON") {
            val env = Env("env1", immutableMapOf("k1" to "v1"))
            val task = Task("task1", "job1", TaskType.FETCH, "ls",
                RunWhen.PASSED)
            val job = Job(
                "job1",
                "Test",
                env,
                immutableListOf(task)
            )

            it("should give a JSON String of it") {
                assertEquals("{\"id\":\"job1\",\"name\":\"Test\"," +
                    "\"envId\":\"env1\",\"tasks\":[\"task1\"]}", job.toJson())
            }
        }

        on("converting a JSON to Job") {
            val db = File.createTempFile("bob", ".db")

            initStorage(
                url = "jdbc:h2:${db.absolutePath}",
                driver = "org.h2.Driver"
            )

            val env = Env("env1", immutableMapOf("k1" to "v1"))
            val task = Task("task1", "job1", TaskType.FETCH, "ls",
                RunWhen.PASSED)
            val job = Job(
                "job1",
                "Test",
                env,
                immutableListOf(task)
            )
            val jobJ = "{\"id\":\"job1\",\"name\":\"Test\", " +
                "\"envId\":\"env1\",\"tasks\":[\"task1\"]}"

            putEnv(env)
            putJob(job)
            putTask(task)

            it("should give a Job object of it") {
                assertEquals(job, jsonToJob(jobJ))
            }

            db.deleteOnExit()
        }

        on("converting a invalid JSON to Job") {
            val jobJ = "{\"id\":\"job1\",\"envId\":\"env1\"," +
                "\"tasks\"}"

            it("should give null") {
                assertNull(jsonToJob(jobJ))
            }
        }
    }
})
