package ktwebapp

class Puppy(val id: String, val name: String, val url: String)

interface Puppies {
    fun listPuppies() : ArrayList<Puppy>
    fun addPuppy(name: String, url: String) : Puppy
    fun deletePuppy(id: String) : Puppy
}

fun randomID() = java.util.UUID.randomUUID().toString()

class PuppiesImpl : Puppies {
    private val puppiesList = arrayListOf(
        Puppy(randomID(), "Homer", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/Szczenie_Jack_Russell_Terrier3.jpg/1280px-Szczenie_Jack_Russell_Terrier3.jpg"),
        Puppy(randomID(), "Erica", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Puppy_on_Halong_Bay.jpg/2560px-Puppy_on_Halong_Bay.jpg"),
        Puppy(randomID(), "Jan", "https://upload.wikimedia.org/wikipedia/commons/7/71/St._Bernard_puppy.jpg")
    )

    override fun listPuppies() : ArrayList<Puppy> {
        return puppiesList
    }

    override fun addPuppy(name: String, url: String) : Puppy {
        val newPuppy = Puppy(randomID(), name, url)
        puppiesList.add(newPuppy)
        Thread.sleep(15000) // Simulate some RPC time
        return newPuppy
    }

    override fun deletePuppy(id: String) : Puppy {
        var pupToRemove: Puppy? = null
        for (pup in puppiesList) {
            if (pup.id == id) {
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
