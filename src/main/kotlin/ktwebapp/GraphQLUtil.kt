package ktwebapp

import graphql.GraphQL
import graphql.schema.idl.*
import graphql.ExecutionResult

import ktwebapp.PuppiesFetcher

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

class GraphQLRequest(val operationName: String, val query: String)

class GraphQLServer() {
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

    fun runQuery(request : GraphQLRequest) : ExecutionResult {
        return build.execute(request.query)
    }
}