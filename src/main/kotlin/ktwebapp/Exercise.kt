package ktwebapp

data class Exercise(
        @AutoAPI val slug: String,
        @AutoAPI val title: String) : ktwebapp.ContentEntity {
    override fun kind() = "Exercise"
}
