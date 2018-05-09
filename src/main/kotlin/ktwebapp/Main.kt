package ktwebapp

import org.koin.dsl.module.*
import org.koin.standalone.StandAloneContext.startKoin

val appModule = applicationContext {
    bean { GraphQLServer() as GraphQLServerInterface }
    bean { PuppiesImpl() as Puppies }
}

fun main(args: Array<String>) {
    println("Starting server")
    startKoin(listOf(appModule))
    ServerApp()
}