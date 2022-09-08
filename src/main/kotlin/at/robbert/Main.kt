package at.robbert

import at.robbert.ui.EndviewViewer

fun main() {
    val downloader = WordleGameDownloader("daily-puzzles-2022-09-09.json")
    val decrypted = decrypt(downloader.getPuzzles("endview")[1])

    val props = wordleGameProperties(decrypted)

    val dimensions = props.getValue("dim").toInt()
    val emptySpaces = props.getValue("leerfelder").toInt()
    val hints = props.getValue("randzahlen")

    val game = EndviewGame(
        size = dimensions,
        letters = dimensions - emptySpaces,
        hints = hints,
    )
    val viewer = EndviewViewer(game)
    viewer.show()
}
