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
import bob.core.blocks.RunWhen
import bob.core.blocks.Task
import bob.core.blocks.TaskType
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.immutableMapOf
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

object PersistenceTest : Spek({
    given("Bob Storage") {
        val db = File.createTempFile("bob", ".db")

        initStorage(
            url = "jdbc:h2:${db.absolutePath}",
            driver = "org.h2.Driver"
        )

        on("saving an Env") {
            putEnv(Env("id1", immutableMapOf("k1" to "v1")))

            it("should save to DB") {
                assertNotNull(getEnv("id1"))
            }
        }

        on("deleting an Env") {
            delEnv("id1")

            it("should return null on fetch") {
                assertNull(getEnv("id1"))
            }
        }

        on("saving a Task") {
            val env = Env("id1", immutableMapOf("k1" to "v1"))
            putEnv(env)
            putJob(Job(
                "job1",
                "Test Job",
                env,
                tasks = immutableListOf()
            ))
            putTask(Task(
                "id1",
                "job1",
                TaskType.FETCH,
                "ls",
                RunWhen.PASSED,
                "/tmp"
            ))

            it("should save to DB") {
                val task = getTask("id1")

                assertNotNull(task)

                if (task != null) {
                    assertEquals("id1", task.id)
                    assertEquals("job1", task.jobId)
                    assertEquals(TaskType.FETCH, task.type)
                    assertEquals("ls", task.command)
                    assertEquals(RunWhen.PASSED, task.runWhen)
                    assertEquals("/tmp", task.workingDirectory)
                } else {
                    fail("Task save test failed.")
                }
            }
        }

        on("deleting a Task") {
            delTask("id1")

            it("should return null on fetch") {
                assertNull(getTask("id1"))
            }
        }

        on("saving a Job") {
            val env = Env("id2", immutableMapOf("k1" to "v1"))
            val task = Task(
                "id1",
                "job1",
                TaskType.FETCH,
                "ls",
                RunWhen.PASSED,
                "/tmp"
            )
            putEnv(env)
            putJob(Job(
                "job1",
                "Test Job",
                env,
                tasks = immutableListOf(task)
            ))
            putTask(task)

            it("should save to DB") {
                val job = getJob("job1")

                assertNotNull(job)
            }
        }

        on("deleting a Job") {
            delJob("job1")

            it("should return null on fetch") {
                assertNull(getJob("job1"))
            }
        }

        db.deleteOnExit()
    }
})
