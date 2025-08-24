package at.robbert

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WordleGameDownloaderTest {

    @Test
    fun getPuzzles() {
        val downloader = WordleGameDownloader("1000")
        val games = downloader.getGames()
        assertTrue(games.contains("Sudoku"), "Expected to find 'sudoku' in the list of games, but got: $games")
        assertTrue(games.contains("Endview"), "Expected to find 'endview' in the list of games, but got: $games")
        assertTrue(games.contains("Sterne"), "Expected to find 'endview' in the list of games, but got: $games")
    }

}