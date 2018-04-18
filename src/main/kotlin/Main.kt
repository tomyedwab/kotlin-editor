import io.ktor.application.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    println("Starting server")
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            static("js") {
                files("static/js")
            }
            static("/") {
                default("static/index.html")
            }
            api()
        }
    }
    server.start(wait = true)
}

fun Routing.api() {
    get("/test") {
        call.respondText("Hello, World")
    }
}