package at.robbert

import at.robbert.ui.EndviewViewer

fun main() {
    val decrypted =
        decrypt("kmEc7iwmXccCEiuUKzq4NWvcRvWKrRQGqspVL9mqx304FEtwR7DYGXUAk+cf7SHIVK+NQJP0dX9pb7jU+v7gwjNsl0bBp3oFLTm44HsRa+0KVUt8tYxUvT5FvbIsUn8tLtwU1YRpr")

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
