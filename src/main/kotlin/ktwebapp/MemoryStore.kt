package ktwebapp

interface Store {
    fun <T: ContentEntity>getAll(kind: String) : List<Revision<T>>
    fun <T: ContentEntity>getBySha(kind: String, sha: String) : Revision<T>?
    fun <T: ContentEntity>getById(kind: String, id: String) : Revision<T>?
    fun <T: ContentEntity>writeRevision(revision: Revision<T>, isNew: Boolean) : Revision<T>
}

data class KindAndId(val kind: String, val id: String)
data class KindAndSha(val kind: String, val sha: String)

class MemoryStore : Store {
    // Map of SHA -> Revision
    public val store = HashMap<KindAndSha, Revision<Any>>()

    // Map of Content ID -> latest SHA
    private val editIndex = HashMap<KindAndId, String>()

    override fun <T: ContentEntity>getAll(kind: String) : List<Revision<T>> =
            editIndex.mapNotNull {
                entry ->
                if (entry.key.kind == kind) {
                    store[KindAndSha(kind, entry.value)] as Revision<T>
                } else null
            }

    override fun <T: ContentEntity>getBySha(kind: String, sha: String) : Revision<T>? =
            store[KindAndSha(kind, sha)] as Revision<T>

    override fun <T: ContentEntity>getById(kind: String, id: String) : Revision<T>? {
        val sha = editIndex[KindAndId(kind, id)] ?: return null
        return store[KindAndSha(kind, sha)] as Revision<T>
    }

    override fun <T: ContentEntity>writeRevision(revision: Revision<T>, isNew: Boolean) : Revision<T> {
        val kind = revision.content.kind()
        if (isNew) {
            if (editIndex.containsKey(KindAndId(kind, revision.id))) {
                throw Error("Revision ID already exists: ${revision.id}")
            }
        } else {
            if (!editIndex.containsKey(KindAndId(kind, revision.id))) {
                throw Error("Updating unknown revision with ID: ${revision.id}")
            }
        }
        store[KindAndSha(kind, revision.sha)] = revision as Revision<Any>
        editIndex[KindAndId(kind, revision.id)] = revision.sha
        return revision
    }
}

fun MemoryStore.init(): MemoryStore {
    writeRevision(Revision("x00000001", "111111111", Video("video_001", "myvideo1", "My video #1", false, 5)), true)
    writeRevision(Revision("x00000002", "222222222", Video("video_002", "myvideo2", "My video #2", false, 10)), true)
    writeRevision(Revision("x00000003", "333333333", Video("video_003", "myvideo3", "My video #3", false, 15)), true)
    writeRevision(Revision("x00000004", "444444444", Exercise("myexercise4", "My exercise #1")), true)
    return this
}