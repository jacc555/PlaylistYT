package pl.kolejkapiosenek.data

object VideoLinks {
    const val MAX_SONGS = 50

    private val ID_REGEX = Regex("^[A-Za-z0-9_-]{11}$")
    private val LINK_REGEX = Regex(
        """(?:youtu\.be/|youtube(?:-nocookie)?\.com/(?:watch\?(?:[^#\s]*&)?v=|shorts/|embed/|live/|v/))([A-Za-z0-9_-]{11})"""
    )

    fun isValidId(id: String): Boolean = ID_REGEX.matches(id)

    /** Wyciąga 11-znakowe ID filmu z linku YouTube albo zwraca null. */
    fun extractId(text: String): String? = LINK_REGEX.find(text)?.groupValues?.get(1)

    /** Link, który YouTube odtwarza jako tymczasową kolejkę (do 50 filmów). */
    fun queueUrl(ids: List<String>): String =
        "https://www.youtube.com/watch_videos?video_ids=" + ids.joinToString(",")

    fun watchUrl(id: String): String = "https://www.youtube.com/watch?v=$id"
}
