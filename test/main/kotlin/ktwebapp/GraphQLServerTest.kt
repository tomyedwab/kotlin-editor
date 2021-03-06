package ktwebapp

import com.nhaarman.mockito_kotlin.*
import graphql.ExecutionInput
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.Koin
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest

/*
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GraphQLServerTest : KoinTest {
    private val puppiesMock : Puppies = mock()
    private val videoStoreMock : Store<Video> = mock()

    private val ctx: Koin = startKoin(listOf(applicationContext {
        bean { GraphQLServer(get(), get()) as GraphQLServerInterface }
        bean { puppiesMock }
        bean { videoStoreMock }
    }))

    @BeforeEach
    fun init() {
        reset(puppiesMock)
    }

    @Test fun `it loads correctly`() {
        // This implicitly tests whether the GraphQL schema is actually valid
        ctx.koinContext.get<GraphQLServerInterface>()
    }

    @Test fun `can list empty puppies`() {
        val query = ExecutionInput( """
            {
                puppies {
                    id,
                    name,
                    url
                }
            }
            """, null, null, null, HashMap<String, Any>())

        whenever(puppiesMock.listPuppies()).thenReturn(ArrayList())

        val server = ctx.koinContext.get<GraphQLServerInterface>()
        val response : LinkedHashMap<String, ArrayList<Puppy>> = server.runQuery(query).getData()
        assertThat(response["puppies"]?.size).isEqualTo(0)
    }

    @Test fun `can list some puppies`() {
        val query = ExecutionInput( """
            {
                puppies {
                    id,
                    name,
                    url
                }
            }
            """, null, null, null, HashMap<String, Any>())

        whenever(puppiesMock.listPuppies()).thenReturn(arrayListOf(
            Puppy("Id1", "Puppy1", "Url1"),
            Puppy("Id2", "Puppy2", "Url2")
        ))

        val server = ctx.koinContext.get<GraphQLServerInterface>()
        val response : LinkedHashMap<String, ArrayList<LinkedHashMap<String, Any>>> = server.runQuery(query).getData()
        assertThat(response["puppies"]?.size).isEqualTo(2)
        assertThat(response["puppies"]?.get(0)?.get("id")).isEqualTo("Id1")
        assertThat(response["puppies"]?.get(0)?.get("name")).isEqualTo("Puppy1")
        assertThat(response["puppies"]?.get(0)?.get("url")).isEqualTo("Url1")
        assertThat(response["puppies"]?.get(1)?.get("id")).isEqualTo("Id2")
        assertThat(response["puppies"]?.get(1)?.get("name")).isEqualTo("Puppy2")
        assertThat(response["puppies"]?.get(1)?.get("url")).isEqualTo("Url2")
    }

    @Test fun `can add a puppy`() {
        val query = ExecutionInput( """
            mutation AddPuppy(${'$'}name: String!, ${'$'}url: String!) {
              addPuppy(name: ${'$'}name, url: ${'$'}url) {
                id
                name
                url
              }
            }
            """, null, null, null,
            hashMapOf("name" to "Puppy1", "url" to "Url1") as HashMap<String, Any>)

        whenever(puppiesMock.addPuppy("Puppy1", "Url1")).thenReturn(
            Puppy("Id1", "Puppy1", "Url1")
        )

        val server = ctx.koinContext.get<GraphQLServerInterface>()
        val response : LinkedHashMap<String, LinkedHashMap<String, Any>> = server.runQuery(query).getData()
        assertThat(response["addPuppy"]?.get("id")).isEqualTo("Id1")
        assertThat(response["addPuppy"]?.get("name")).isEqualTo("Puppy1")
        assertThat(response["addPuppy"]?.get("url")).isEqualTo("Url1")
    }

    @Test fun `can delete a puppy`() {
        val query = ExecutionInput( """
            mutation DeletePuppy(${'$'}id: String!) {
              deletePuppy(id: ${'$'}id) {
                id
                name
                url
              }
            }
            """, null, null, null,
                hashMapOf("id" to "Id1") as HashMap<String, Any>)

        whenever(puppiesMock.deletePuppy("Id1")).thenReturn(
            Puppy("Id1", "Puppy1", "Url1")
        )

        val server = ctx.koinContext.get<GraphQLServerInterface>()
        val response : LinkedHashMap<String, LinkedHashMap<String, Any>> = server.runQuery(query).getData()
        assertThat(response["deletePuppy"]?.get("id")).isEqualTo("Id1")
        assertThat(response["deletePuppy"]?.get("name")).isEqualTo("Puppy1")
        assertThat(response["deletePuppy"]?.get("url")).isEqualTo("Url1")
    }
}
*/