package ktwebapp

import org.koin.Koin
import org.koin.dsl.module.*
import org.koin.standalone.StandAloneContext.startKoin

val appModule = applicationContext {
    bean { ServerApp(get()) }
    bean { GraphQLServer(get()) as GraphQLServerInterface }
    bean { PuppiesImpl() as Puppies }
}

fun main(args: Array<String>) {
    println("Starting server")
    val ctx : Koin = startKoin(listOf(appModule))
    ctx.koinContext.get<ServerApp>()
}