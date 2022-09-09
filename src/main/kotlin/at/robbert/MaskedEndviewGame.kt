package at.robbert

class MaskedEndviewGame(
    val game: EndviewGame,
    val mask: Set<Int2>
) : IEndviewGame by game {

    override fun setLetter(x: Int, y: Int, letter: Int?): Boolean {
        if (Int2(x, y) !in mask)
            return false

        return game.setLetter(x, y, letter)
    }

    override fun setOptions(x: Int, y: Int, options: Set<Int>): Boolean {
        if (Int2(x, y) !in mask)
            return false

        return game.setOptions(x, y, options)
    }

    override fun toggleOptions(x: Int, y: Int, char: Int): Boolean {
        if (Int2(x, y) !in mask)
            return false

        return game.toggleOptions(x, y, char)
    }

    override fun removeOptions(x: Int, y: Int, options: Set<Int>): Boolean {
        if (Int2(x, y) !in mask)
            return false

        return game.removeOptions(x, y, options)
    }

}
