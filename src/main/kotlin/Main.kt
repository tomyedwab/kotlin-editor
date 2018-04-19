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

import graphql.GraphQL
import graphql.schema.idl.*
import graphql.schema.StaticDataFetcher
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

val SCHEMA = """
type Puppy {
    id: String
    name: String
    url: String
}

type Query {
    puppies: [Puppy]
}
"""

fun main(args: Array<String>) {
    println("Starting server")
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
            api()
        }
    }
    server.start(wait = true)
}

class Puppy(val id: String, val name: String, val url: String)

class PuppiesFetcher : DataFetcher<Any> {
    val puppiesList = arrayOf(
        Puppy("1", "Homer", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/Szczenie_Jack_Russell_Terrier3.jpg/1280px-Szczenie_Jack_Russell_Terrier3.jpg"),
        Puppy("2", "Erica", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Puppy_on_Halong_Bay.jpg/2560px-Puppy_on_Halong_Bay.jpg"),
        Puppy("3", "Jan", "https://upload.wikimedia.org/wikipedia/commons/7/71/St._Bernard_puppy.jpg")
    )

    override fun get(environment: DataFetchingEnvironment) : Array<Puppy> {
        return puppiesList
    }
}

class GraphQLRequest(val operationName: String, val query: String)

fun Routing.api() {
    post("/graphql") {
        var schemaParser = SchemaParser()
        var typeDefinitionRegistry = schemaParser.parse(SCHEMA)
        var runtimeWiring = RuntimeWiring.newRuntimeWiring()
            .type("Query", {
                builder ->
                builder.dataFetcher("puppies", PuppiesFetcher())
            })
            .build()

        var schemaGenerator = SchemaGenerator()
        var graphQLSchema = schemaGenerator.makeExecutableSchema(
            typeDefinitionRegistry, runtimeWiring)

        var build = GraphQL.newGraphQL(graphQLSchema).build()

        var request : GraphQLRequest = call.receive()
        var executionResult = build.execute(request.query)

        call.respond(executionResult.toSpecification())
    }
}