package at.robbert

import at.robbert.EndviewGameFactory.createEndviewGame
import at.robbert.ui.EndviewViewer

fun main() {
    val downloader = WordleGameDownloader("daily-puzzles-2024-08-04.json")
    val game = createEndviewGame(downloader.getPuzzles("endview")[2])
    val viewer = EndviewViewer(game)
    viewer.show()
}
