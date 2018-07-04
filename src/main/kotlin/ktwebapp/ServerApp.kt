package ktwebapp

import io.ktor.application.*
import io.ktor.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.*

import graphql.ExecutionInput

class ServerApp(private val queryServer : GraphQLServerInterface) {
    private val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson {
                // Configure Gson here
                serializeNulls()
            }
        }

        routing {
            static("/") {
                default("static/index.html")
            }

            static("/images") {
                files("static/images")
            }

            post("/graphql") {
                val request : ExecutionInput = call.receive()
                val result = queryServer.runQuery(request)
                call.respond(result.toSpecification())
            }
        }
    }

    init {
        server.start(wait = true)
    }
}