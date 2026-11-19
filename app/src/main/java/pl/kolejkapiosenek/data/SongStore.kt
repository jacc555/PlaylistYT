package pl.kolejkapiosenek.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/** Zapis listy w SharedPreferences (lokalnie na telefonie). */
class SongStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("kolejka", Context.MODE_PRIVATE)

    fun load(): List<Song> {
        val raw = prefs.getString(KEY, null) ?: return DEFAULT_SONGS
        return try {
            val array = JSONArray(raw)
            val result = ArrayList<Song>()
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.optString("id", "")
                if (!VideoLinks.isValidId(id)) continue
                result.add(Song(id, obj.optString("name", "")))
            }
            result.distinctBy { it.id }
        } catch (e: JSONException) {
            DEFAULT_SONGS
        }
    }

    fun save(songs: List<Song>) {
        val array = JSONArray()
        songs.forEach { song ->
            array.put(JSONObject().put("id", song.id).put("name", song.name))
        }
        prefs.edit().putString(KEY, array.toString()).apply()
    }

    private companion object {
        const val KEY = "kolejka-piosenek-v3"
    }
}
