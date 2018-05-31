package ktwebapp

import com.nhaarman.mockito_kotlin.*
import graphql.ExecutionInput
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.Koin
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GraphQLServerTest : KoinTest {
    private val puppiesMock : Puppies = mock()

    private val ctx: Koin = startKoin(listOf(applicationContext {
        bean { GraphQLServer(get()) as GraphQLServerInterface }
        bean { puppiesMock }
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
}