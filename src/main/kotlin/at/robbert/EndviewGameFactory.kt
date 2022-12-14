package at.robbert

object EndviewGameFactory {

    fun createEndviewGame(puzzlestring: String): EndviewGame {
        val decrypted = decrypt(puzzlestring)

        val props = wordleGameProperties(decrypted)

        val dimensions = props.getValue("dim").toInt()
        val emptySpaces = props.getValue("leerfelder").toInt()
        val hints = props.getValue("randzahlen")

        return EndviewGame(
            size = dimensions,
            letters = dimensions - emptySpaces,
            hints = hints,
        )
    }

}
