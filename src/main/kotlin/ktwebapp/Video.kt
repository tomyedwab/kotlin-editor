package ktwebapp

data class Video(
        @AutoAPI val youtubeId: String,
        @AutoAPI val slug: String,
        @AutoAPI val title: String,
        @AutoAPI val hidden: Boolean?,
        @AutoAPI val duration: Int?) : ktwebapp.ContentEntity {
    override fun kind() = "Video"
}
