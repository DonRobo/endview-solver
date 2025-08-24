package at.robbert.gridgames

import com.github.salomonbrys.kotson.array
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class WordleGameDownloader(
    date: LocalDate,
) {
    val jsonName: String = date.let { d ->
        val referenceDate = LocalDate.of(2024, 3, 15)
        val referenceId = 1000
        val daysBetween = ChronoUnit.DAYS.between(referenceDate, d)
        (referenceId + daysBetween).toString()
    }

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
                val client = HttpClient(CIO) {
                    expectSuccess = true
                }
                val response = client.get("https://gridgames.app/api/games/puzzles/puzzlelists/$jsonName")
                require(response.contentType()?.match(ContentType.Application.Json) ?: false) {
                    "Expected JSON response but got ${response.contentType()}"
                }
                val content = response.bodyAsText()
                jsonFile.writeText(content)
                content
            }
        }
        val gson = Gson()
        try {
            gson.fromJson(jsonString)
        } catch (e: JsonSyntaxException) {
            jsonFile.delete()
            throw JsonSyntaxException("Malformed JSON: $jsonString", e)
        }
    }

    fun getPuzzles(game: String): List<String> {
        return json["types"].array.first {
            it["type"].string.equals(game, ignoreCase = true)
        }["puzzles"].array.map {
            it["puzzlestring"].string
        }
    }

    fun getGames(): List<String> {
        return json["types"].array.map {
            it["type"].string
        }
    }

}
