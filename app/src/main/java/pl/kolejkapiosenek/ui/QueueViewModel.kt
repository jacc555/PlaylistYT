package pl.kolejkapiosenek.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import pl.kolejkapiosenek.data.Song
import pl.kolejkapiosenek.data.SongStore
import pl.kolejkapiosenek.data.VideoLinks

class QueueViewModel(app: Application) : AndroidViewModel(app) {

    private val store = SongStore(app)

    val songs = mutableStateListOf<Song>().also { it.addAll(store.load()) }

    var addMessage by mutableStateOf("")
        private set
    var shuffleMessage by mutableStateOf("")
        private set
    var status by mutableStateOf("")
        private set

    val queueUrl: String
        get() = VideoLinks.queueUrl(songs.map { it.id })

    /** Dodaje linki z wklejonego tekstu. Zwraca true, jeśli coś dodano. */
    fun addLinks(text: String): Boolean {
        val tokens = text.split(Regex("[\\s,]+")).filter { it.isNotEmpty() }
        val looksLikeLink = Regex("^https?:|youtu", RegexOption.IGNORE_CASE)
        var added = 0
        var duplicates = 0
        var overLimit = 0
        var unrecognized = 0

        for (token in tokens) {
            val id = VideoLinks.extractId(token)
            if (id == null) {
                if (looksLikeLink.containsMatchIn(token)) unrecognized++
                continue
            }
            when {
                songs.any { it.id == id } -> duplicates++
                songs.size >= VideoLinks.MAX_SONGS -> overLimit++
                else -> {
                    songs.add(Song(id, ""))
                    added++
                }
            }
        }
        if (added > 0) persist()

        val parts = mutableListOf<String>()
        if (added > 0) parts.add("Dodano: $added.")
        if (duplicates > 0) parts.add("Już w kolejce: $duplicates.")
        if (overLimit > 0) parts.add("Limit to ${VideoLinks.MAX_SONGS} piosenek.")
        if (unrecognized > 0) parts.add("Nie rozpoznano linków: $unrecognized.")
        if (parts.isEmpty()) {
            parts.add(
                if (text.isNotBlank()) "Nie znalazłem w tym tekście linku do YouTube."
                else "Wklej najpierw link do filmu."
            )
        }
        addMessage = parts.joinToString(" ")
        return added > 0
    }

    fun rename(id: String, name: String) {
        val index = songs.indexOfFirst { it.id == id }
        if (index < 0) return
        songs[index] = songs[index].copy(name = name.take(120))
        persist()
    }

    fun move(index: Int, delta: Int) {
        val target = index + delta
        if (index !in songs.indices || target !in songs.indices) return
        val tmp = songs[index]
        songs[index] = songs[target]
        songs[target] = tmp
        persist()
    }

    fun remove(id: String) {
        val index = songs.indexOfFirst { it.id == id }
        if (index < 0) return
        songs.removeAt(index)
        persist()
        addMessage = "Usunięto z kolejki."
    }

    fun shuffle() {
        if (songs.size < 2) {
            shuffleMessage = "Do wymieszania potrzebne są co najmniej dwie piosenki."
            return
        }
        val before = songs.map { it.id }
        var shuffled = songs.toList()
        var tries = 0
        do {
            shuffled = shuffled.shuffled()
            tries++
        } while (shuffled.map { it.id } == before && tries < 5)
        songs.clear()
        songs.addAll(shuffled)
        persist()
        shuffleMessage = "Wymieszano. Przycisk „Odtwarzaj wszystkie” puści piosenki w tej kolejności."
    }

    fun showStatus(message: String) {
        status = message
    }

    private fun persist() = store.save(songs.toList())
}
