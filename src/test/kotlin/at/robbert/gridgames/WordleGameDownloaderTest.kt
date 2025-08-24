package at.robbert.gridgames

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class WordleGameDownloaderTest {

    @Test
    fun jsonNameCalculatorWorks() {
        assertEquals("1000", WordleGameDownloader(LocalDate.parse("2024-03-15")).jsonName)
        assertEquals("1001", WordleGameDownloader(LocalDate.parse("2024-03-16")).jsonName)
        assertEquals("1527", WordleGameDownloader(LocalDate.parse("2025-08-24")).jsonName)
    }

    @Test
    fun getPuzzles() {
        val downloader = WordleGameDownloader(LocalDate.now())
        val games = downloader.getGames()
        assertTrue(games.contains("Sudoku"), "Expected to find 'sudoku' in the list of games, but got: $games")
        assertTrue(games.contains("Endview"), "Expected to find 'endview' in the list of games, but got: $games")
        assertTrue(games.contains("Sterne"), "Expected to find 'endview' in the list of games, but got: $games")
    }

}