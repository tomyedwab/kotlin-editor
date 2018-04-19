package ktwebapp

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class Puppy(val id: String, val name: String, val url: String)

class PuppiesFetcher : DataFetcher<Any> {
    val puppiesList = arrayOf(
        Puppy("1", "Homer", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/Szczenie_Jack_Russell_Terrier3.jpg/1280px-Szczenie_Jack_Russell_Terrier3.jpg"),
        Puppy("2", "Erica", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Puppy_on_Halong_Bay.jpg/2560px-Puppy_on_Halong_Bay.jpg"),
        Puppy("3", "Jan", "https://upload.wikimedia.org/wikipedia/commons/7/71/St._Bernard_puppy.jpg")
    )

    override fun get(environment: DataFetchingEnvironment) : Array<Puppy> {
        return puppiesList
    }
}
