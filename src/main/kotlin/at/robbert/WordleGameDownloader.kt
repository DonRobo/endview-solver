package at.robbert

import com.github.salomonbrys.kotson.array
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import com.google.gson.Gson
import com.google.gson.JsonElement
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import java.io.File

class WordleGameDownloader(
    val jsonName: String
) {
    private val json: JsonElement by lazy {
        val jsonFolder = File("jsons")
        if (!jsonFolder.exists()) {
            jsonFolder.mkdirs()
        }
        val jsonFile = File(jsonFolder, jsonName)
        val jsonString = if (jsonFile.exists()) {
            jsonFile.readText()
        } else {
            runBlocking {
                val client = HttpClient(CIO)
                val content = client.get("https://gridgames.app/puzzles/puzzlelists/$jsonName") {}.bodyAsText()
                jsonFile.writeText(content)
                content
            }
        }
        val gson = Gson()
        gson.fromJson(jsonString)
    }

    fun getPuzzles(game: String): List<String> {
        return json["types"].array.first {
            it["type"].string.equals(game, ignoreCase = true)
        }["puzzles"].array.map {
            it["puzzlestring"].string
        }
    }

}
