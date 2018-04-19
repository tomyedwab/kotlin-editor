package ktwebapp

import io.ktor.application.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.*

import ktwebapp.GraphQLRequest
import ktwebapp.GraphQLServer

fun main(args: Array<String>) {
    println("Starting server")
    val queryServer = GraphQLServer()
    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            gson {
                // Configure Gson here
            }
        }
        routing {
            static("/") {
                default("static/index.html")
            }
            api(queryServer)
        }
    }
    server.start(wait = true)
}

fun Routing.api(queryServer : GraphQLServer) {
    post("/graphql") {
        var request : GraphQLRequest = call.receive()
        var result = queryServer.runQuery(request)
        call.respond(result.toSpecification())
    }
}