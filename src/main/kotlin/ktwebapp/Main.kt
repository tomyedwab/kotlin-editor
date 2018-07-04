package ktwebapp

import graphql.schema.GraphQLSchema
import org.koin.Koin
import org.koin.dsl.module.*
import org.koin.standalone.StandAloneContext.startKoin

private val appModule = applicationContext {
    bean { ServerApp(get()) }
    bean { GraphQLServer(get(), listOf(Video::class, Exercise::class)) as GraphQLServerInterface }
    bean { MemoryStore().init() as Store }
}

fun main(args: Array<String>) {
    println("Starting server")
    val ctx : Koin = startKoin(listOf(appModule))
    ctx.koinContext.get<ServerApp>()
}