package at.robbert.gridgames

import at.robbert.containsAny

data class AttemptedChange(
    val x: Int,
    val y: Int,
)


class ReadonlyEndviewGame(
    val game: IEndviewGame,
) : IEndviewGame by game {
    private val attemptedChangesInternal: MutableList<AttemptedChange> = mutableListOf()
    val attemptedChanges: List<AttemptedChange> get() = attemptedChangesInternal.toList()

    override fun setLetter(x: Int, y: Int, letter: Int?): Boolean {
        val isChange = game.letterAt(x, y) != letter
        if (isChange)
            attemptedChangesInternal += AttemptedChange(x, y)

        return isChange
    }

    override fun setOptions(x: Int, y: Int, options: Set<Int>): Boolean {
        val isChange = game.optionsAt(x, y) != options
        if (isChange)
            attemptedChangesInternal += AttemptedChange(x, y)

        return isChange
    }

    override fun toggleOptions(x: Int, y: Int, char: Int): Boolean {
        val isChange = true
        if (isChange)
            attemptedChangesInternal += AttemptedChange(x, y)

        return isChange
    }

    override fun removeOptions(x: Int, y: Int, options: Set<Int>): Boolean {
        val isChange = game.optionsAt(x, y).containsAny(options)
        if (isChange)
            attemptedChangesInternal += AttemptedChange(x, y)

        return isChange
    }

}
