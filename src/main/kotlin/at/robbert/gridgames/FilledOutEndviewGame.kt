package at.robbert.gridgames

class FilledOutEndviewGame(val game: IEndviewGame) : IEndviewGame by game {

    override fun optionsAt(x: Int, y: Int): Set<Int> {
        return game.optionsAt(x, y).let {
            if (it.isEmpty()) {
                return game.allOptions
            } else {
                it
            }
        }
    }

    override fun toggleOptions(x: Int, y: Int, char: Int): Boolean {
        if (game.optionsAt(x, y).isEmpty()) game.setOptions(x, y, allOptions)

        return game.toggleOptions(x, y, char)
    }

    override fun removeOptions(x: Int, y: Int, options: Set<Int>): Boolean {
        if (game.optionsAt(x, y).isEmpty()) game.setOptions(x, y, allOptions)

        return game.removeOptions(x, y, options)
    }
}
