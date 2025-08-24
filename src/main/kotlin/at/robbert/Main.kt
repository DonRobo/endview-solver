package at.robbert

import at.robbert.EndviewGameFactory.createEndviewGame
import at.robbert.ui.EndviewViewer
import java.time.LocalDate

fun main() {
    val downloader = WordleGameDownloader(LocalDate.parse("2024-08-04"))
    val game = createEndviewGame(downloader.getPuzzles("endview")[2])
    val viewer = EndviewViewer(game)
    viewer.show()
}
