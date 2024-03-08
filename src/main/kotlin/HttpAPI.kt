import Interfaces.TodoListRepoInterface
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.core.*
import org.http4k.routing.path

val mapper: ObjectMapper = jacksonObjectMapper() // tool to allow us to convert to and from JSON data

val app: HttpHandler = routes(
    "/todos" bind GET to {request: Request ->
        val todoId: String = request.query("todoId")?: "" // handle errors if id is incorrect

        val todoListRepo: TodoListRepoInterface = TodoListRepoJSON()
        val domain = Domain(todoListRepo)
        val todoList: MutableList<TodoItem> = domain.getTodoList(todoId)
        val toDoListAsJsonString: String = mapper.writeValueAsString(todoList) // turn back to a json string
        Response(OK).body(toDoListAsJsonString)
    },

    "/addTodo/{todoName}" bind POST to {request: Request ->
        val todoName: String = request.path("todoName")?: "" // handle "" scenario and errors

        val todoListRepo: TodoListRepoInterface = TodoListRepoJSON()
        val domain = Domain(todoListRepo)

        val confirmation = domain.addTodoItem(todoName)
        Response(OK).body(confirmation)

    }
)

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(app)

    val server = printingApp.asServer(SunHttp(9000)).start()

    println("Server started on " + server.port())
}


