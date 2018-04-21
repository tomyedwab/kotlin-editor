package ktwebapp

import graphql.GraphQL
import graphql.schema.idl.*
import graphql.ExecutionResult
import graphql.ExecutionInput

import ktwebapp.Puppies

// TODO: Test that this is actually a valid schema at build time
val SCHEMA = """
type Puppy {
    id: String
    name: String
    url: String
}

type Query {
    puppies: [Puppy]
}

type Mutation {
    addPuppy(name: String!, url: String!): Puppy
    deletePuppy(id: String!): Puppy
}
"""

class GraphQLRequest(
    val operationName: String,
    val query: String,
    val variables: HashMap<String, String>)

class GraphQLServer() {
    var puppies = Puppies()
    var schemaParser = SchemaParser()
    var typeDefinitionRegistry = schemaParser.parse(SCHEMA)
    var runtimeWiring = RuntimeWiring.newRuntimeWiring()
        .type("Query", {
            builder ->
            builder.dataFetcher("puppies", puppies.ListPuppies())
        })
        .type("Mutation", {
            builder ->
            builder.dataFetcher("addPuppy", puppies.AddPuppy())
            builder.dataFetcher("deletePuppy", puppies.DeletePuppy())
        })
        .build()

    var schemaGenerator = SchemaGenerator()
    var graphQLSchema = schemaGenerator.makeExecutableSchema(
        typeDefinitionRegistry, runtimeWiring)

    var build = GraphQL.newGraphQL(graphQLSchema).build()

    fun runQuery(request : ExecutionInput) : ExecutionResult {
        return build.execute(request)
    }
}