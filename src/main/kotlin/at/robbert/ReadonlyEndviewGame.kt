package at.robbert

class ReadonlyEndviewGame(
    game: EndviewGame,
) : IEndviewGame by game {
    override fun setLetter(x: Int, y: Int, letter: Int?): Boolean {
        TODO("Not yet implemented")
    }

    override fun setOptions(x: Int, y: Int, options: Set<Int>): Boolean {
        TODO("Not yet implemented")
    }

    override fun toggleOptions(x: Int, y: Int, char: Int): Boolean {
        TODO("Not yet implemented")
    }

}
