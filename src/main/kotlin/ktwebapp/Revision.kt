package ktwebapp

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.security.MessageDigest
import java.util.*

data class Revision<T>(val id: String, val sha: String, val content: T)

fun createNewContentId() = "x%08x".format(Random().nextInt(0x7fffffff))

fun calculateSha(content: ContentEntity, id: String) : String {
    val contentJson = jacksonObjectMapper().writeValueAsString(content) + "&content_id=$id"
    val bytes = contentJson.toByteArray()
    val md = MessageDigest.getInstance("SHA-1")
    val digest = md.digest(bytes)
    return digest.fold("", { acc, byte -> acc.plus("%02x".format(byte)) })
}

inline fun <reified T: Any> updateRevision(id: String, content: T): Revision<T> {
    val contentJson = jacksonObjectMapper().writeValueAsString(content) + "&content_id=$id"
    val bytes = contentJson.toByteArray()
    val md = MessageDigest.getInstance("SHA-1")
    val digest = md.digest(bytes)
    val sha = digest.fold("", { acc, byte -> acc.plus("%02x".format(byte)) })
    return Revision(id, sha, content)
}
