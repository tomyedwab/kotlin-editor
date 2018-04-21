package ktwebapp

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

class Puppy(val id: String, val name: String, val url: String)

fun randomID() = java.util.UUID.randomUUID().toString()

class Puppies {
    val puppiesList = arrayListOf(
        Puppy(randomID(), "Homer", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/Szczenie_Jack_Russell_Terrier3.jpg/1280px-Szczenie_Jack_Russell_Terrier3.jpg"),
        Puppy(randomID(), "Erica", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Puppy_on_Halong_Bay.jpg/2560px-Puppy_on_Halong_Bay.jpg"),
        Puppy(randomID(), "Jan", "https://upload.wikimedia.org/wikipedia/commons/7/71/St._Bernard_puppy.jpg")
    )

    inner class ListPuppies : DataFetcher<Any> {
        override fun get(environment: DataFetchingEnvironment) : ArrayList<Puppy> {
            return puppiesList
        }
    }

    inner class AddPuppy : DataFetcher<Any> {
        override fun get(environment: DataFetchingEnvironment) : Puppy {
            val newPuppy = Puppy(
                randomID(), environment.getArgument("name"),
                environment.getArgument("url"))
            puppiesList.add(newPuppy)
            Thread.sleep(15000) // Simulate some RPC time
            return newPuppy
        }
    }

    inner class DeletePuppy : DataFetcher<Any> {
        override fun get(environment: DataFetchingEnvironment) : Puppy {
            val idToRemove: String = environment.getArgument("id")
            var pupToRemove: Puppy? = null
            for (pup in puppiesList) {
                if (pup.id == idToRemove) {
                    pupToRemove = pup
                    break
                }
            }
            if (pupToRemove == null) {
                throw IllegalArgumentException("Could not find puppy to remove")
            }
            puppiesList.remove(pupToRemove)
            Thread.sleep(15000) // Simulate some RPC time
            return pupToRemove
        }
    }
}
