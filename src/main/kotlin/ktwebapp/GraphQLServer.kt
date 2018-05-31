package ktwebapp

import graphql.GraphQL
import graphql.schema.idl.*
import graphql.ExecutionResult
import graphql.ExecutionInput
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

interface GraphQLServerInterface {
    fun runQuery(request : ExecutionInput) : ExecutionResult
}

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

class GraphQLServer(private val puppies : Puppies) : GraphQLServerInterface {
    private val schemaParser = SchemaParser()
    private val typeDefinitionRegistry = schemaParser.parse(SCHEMA)
    private val runtimeWiring = RuntimeWiring.newRuntimeWiring()
        .type("Query", {
            builder ->
            builder.dataFetcher("puppies") { puppies.listPuppies() }
        })
        .type("Mutation", {
            builder ->
            builder.dataFetcher("addPuppy", {
                puppies.addPuppy(it.getArgument("name"), it.getArgument("url"))
            })
            builder.dataFetcher("deletePuppy", {
                puppies.deletePuppy(it.getArgument("id"))
            })
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