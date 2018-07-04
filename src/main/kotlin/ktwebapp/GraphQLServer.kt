package ktwebapp

import graphql.*
import graphql.schema.idl.*
import graphql.execution.DataFetcherResult
import graphql.language.SourceLocation
import graphql.ErrorType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

interface GraphQLServerInterface {
    fun runQuery(request : ExecutionInput) : ExecutionResult
}

class GraphQLServer(private val revisionStore: Store, private val revisionKinds: List<KClass<out ContentEntity>>) : GraphQLServerInterface {
    private val schemaParser = SchemaParser()
    private val schema = generateSchema().also { System.out.println("Schema:\n$it") }
    private val typeDefinitionRegistry = schemaParser.parse(schema)
    private val runtimeWiring = RuntimeWiring.newRuntimeWiring()
        .type("Query") {
            builder ->
            for (kind in revisionKinds) {
                buildQueries(builder, revisionStore, kind)
            }
            builder
        }
        .type("Mutation") {
            builder ->
            for (kind in revisionKinds) {
                buildMutations(builder, revisionStore, kind)
            }
            builder
        }
        .build()

    private val schemaGenerator = SchemaGenerator()
    private val graphQLSchema = schemaGenerator.makeExecutableSchema(
        typeDefinitionRegistry, runtimeWiring)

    private val build = GraphQL.newGraphQL(graphQLSchema).build()

    private fun graphQLPropToType(prop: KProperty1<out ContentEntity, Any?>): String? {
        prop.findAnnotation<AutoAPI>() ?: return null

        return when(prop.returnType.toString()) {
            "kotlin.String" -> "String!"
            "kotlin.String?" -> "String"
            "kotlin.Int" -> "Int!"
            "kotlin.Int?" -> "Int"
            "kotlin.Float" -> "Float!"
            "kotlin.Float?" -> "Float"
            "kotlin.Boolean" -> "Boolean!"
            "kotlin.Boolean?" -> "Boolean"
            else -> null.also { System.out.println("Unknown type ${prop.returnType} for property ${prop.name}") }
        }
    }

    private fun generateSchemaType(kind: KClass<out ContentEntity>): String {
        val name = kind.java.simpleName
        val properties = kind.declaredMemberProperties

        return listOf(
                "type $name {",
                *properties.mapNotNull map@{
                    val type = this.graphQLPropToType(it) ?: return@map null
                    "    ${it.name}: $type"
                }.toTypedArray(),
                "}",
                "",
                "type ${name}Revision {",
                "    id: String",
                "    sha: String",
                "    content: $name",
                "}"
        ).joinToString("\n")
    }

    private fun generateSchemaGetter(kind: KClass<out ContentEntity>): String {
        val name = kind.java.simpleName
        return "    get$name(id: String!): ${name}Revision\n    getAll${name}s: [${name}Revision]\n"
    }

    private fun generateSchemaMutations(kind: KClass<out ContentEntity>): String {
        val name = kind.java.simpleName
        val properties = kind.declaredMemberProperties

        val createMutation = listOf(
                "    create$name(",
                properties.mapNotNull map@{
                    prop ->
                    val type = this.graphQLPropToType(prop) ?: return@map null
                    "${prop.name}: $type"
                }.joinToString(", "),
                "): ${name}Revision"
        ).joinToString("")

        val updateMutations = properties.mapNotNull map@{
            prop ->
            val type = this.graphQLPropToType(prop) ?: return@map null
            System.out.println("${prop.name} -> $type")
            "    set$name${prop.name.capitalize()}(id: String, ${prop.name}: $type): ${name}Revision"
        }.joinToString("\n")

        return "$createMutation\n$updateMutations\n"
    }

    private fun generateSchema() : String = (
            listOf(
                *revisionKinds.map { generateSchemaType(it) }.toTypedArray(),
                "type Query {",
                *revisionKinds.map { generateSchemaGetter(it) }.toTypedArray(),
                "}",
                "",
                "type Mutation {",
                *revisionKinds.map{ generateSchemaMutations(it) }.toTypedArray(),
                "}",
                ""
            ).joinToString("\n"))

    private fun buildQueries(builder: TypeRuntimeWiring.Builder, revisionStore: Store, kind: KClass<out ContentEntity>) {
        val name = kind.java.simpleName
        builder.dataFetcher("get$name") {
            revisionStore.getById<ContentEntity>(name, it.getArgument("id") as String)
        }
        builder.dataFetcher("getAll${name}s") {
            revisionStore.getAll<ContentEntity>(name)
        }
    }

    private fun buildMutations(builder: TypeRuntimeWiring.Builder, revisionStore: Store, kind: KClass<out ContentEntity>) {
        val name = kind.java.simpleName
        val parameters = kind.primaryConstructor!!.parameters
        val properties = kind.declaredMemberProperties

        builder.dataFetcher("create$name") {
            env ->
            val args : Array<Any?> = parameters.map { env.getArgument<Any?>(it.name) }.toTypedArray()
            val newContent = kind.primaryConstructor!!.call(*args)
            val id = createNewContentId()
            val sha = calculateSha(newContent, id)
            revisionStore.writeRevision(Revision(id, sha, newContent), true)
        }
        for (prop in properties) {
            builder.dataFetcher("set$name${prop.name.capitalize()}") {
                env ->
                val id : String = env.getArgument("id")
                val revision = revisionStore.getById<ContentEntity>(name, id) ?: throw Exception("$name with ID $id not found.")
                val value : Any? = env.getArgument(prop.name)

                System.out.println("Setting $name ID $id field ${prop.name} to $value")

                val instanceClass = revision.content.javaClass.kotlin
                val args : Array<Any?> = parameters.map {
                    param ->
                    if (param.name == prop.name) value else (
                            instanceClass.declaredMemberProperties.first { it.name == param.name }.get(revision.content))
                }.toTypedArray()

                val newContent = kind.primaryConstructor!!.call(*args)
                val newSha = calculateSha(newContent, id)
                revisionStore.writeRevision(Revision(id, newSha, newContent), false)
            }
        }
    }

    override fun runQuery(request : ExecutionInput) : ExecutionResult {
        return build.execute(request)
    }
}