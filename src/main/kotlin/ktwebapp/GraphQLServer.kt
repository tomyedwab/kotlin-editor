package ktwebapp

import graphql.GraphQL
import graphql.schema.idl.*
import graphql.ExecutionResult
import graphql.ExecutionInput

interface GraphQLServerInterface {
    fun runQuery(request : ExecutionInput) : ExecutionResult
}

// TODO: Test that this is actually a valid schema at build time
const val SCHEMA = """
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

data class GraphQLRequest(
    val operationName: String,
    val query: String,
    val variables: HashMap<String, String>)

class GraphQLServer(private val puppies : Puppies) : GraphQLServerInterface {
    private val schemaParser = SchemaParser()
    private val typeDefinitionRegistry = schemaParser.parse(SCHEMA)
    private val runtimeWiring = RuntimeWiring.newRuntimeWiring()
        .type("Query", {
            builder ->
            builder.dataFetcher("puppies", puppies.getListPuppies())
        })
        .type("Mutation", {
            builder ->
            builder.dataFetcher("addPuppy", puppies.getAddPuppy())
            builder.dataFetcher("deletePuppy", puppies.getDeletePuppy())
        })
        .build()

    private val schemaGenerator = SchemaGenerator()
    private val graphQLSchema = schemaGenerator.makeExecutableSchema(
        typeDefinitionRegistry, runtimeWiring)

    private val build = GraphQL.newGraphQL(graphQLSchema).build()

    override fun runQuery(request : ExecutionInput) : ExecutionResult {
        return build.execute(request)
    }
}