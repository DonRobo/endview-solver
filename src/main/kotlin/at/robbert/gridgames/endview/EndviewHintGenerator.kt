package at.robbert.gridgames.endview

import at.robbert.Int2
import at.robbert.canContainAllOf
import at.robbert.gridgames.IEndviewGame

class EndviewHintGenerator(
    private val game: IEndviewGame,
) {

    fun solveStep() {
        if (fillEmptyCells()) {
            println("Found empty cells")
        } else if (removeOptionsWhereLetterAlreadySet()) {
            println("Found letters already set that removed adjacent options")
        } else if (applyHints()) {
            println("Found options to remove using hints")
        } else if (findSingleOptions()) {
            println("Found rows/columns with only one position for a letter")
        } else if (findMustBeEmpty()) {
            println("Found rows/columns with only one combination of empty spaces")
        } else if (removeEmptyOptionsWherePossible()) {
            println("Could remove some empty options by counting already set empty cells")
        } else if (findPairs()) {
            //already output in function itself
        } else {
            println("Nothing more to do")
        }

        setLettersWherePossible()
    }

    private fun findMustBeEmpty(): Boolean {
        var updated = false
        applyFindMustBeEmpty { x, y -> Int2(x, y) }.also {
            if (it) updated = true
        }
        applyFindMustBeEmpty { x, y -> Int2(y, x) }.also {
            if (it) updated = true
        }
        return updated
    }

    private fun applyFindMustBeEmpty(coordinate: (Int, Int) -> Int2): Boolean {
        var updated = false

        val emptiesRequired = game.size - game.letters
        for (y in 0 until game.size) {
            var emptiesFound = 0
            for (x in 0 until game.size) {
                if (0 in game.optionsAt(coordinate(x, y))) {
                    emptiesFound++
                }
            }
            if (emptiesFound <= emptiesRequired) {
                for (x in 0 until game.size) {
                    if (0 in game.optionsAt(coordinate(x, y))) {
                        game.setOptions(coordinate(x, y), setOf(0)).also {
                            if (it) updated = true
                        }
                    }
                }
            }
        }

        return updated
    }


    private fun findPairs(): Boolean {
        var updated = false
        applyRemovePairs { x, y -> Int2(x, y) }.also {
            if (it) updated = true
        }
        applyRemovePairs { x, y -> Int2(y, x) }.also {
            if (it) updated = true
        }
        return updated
    }

    private fun applyRemovePairs(coordinate: (Int, Int) -> Int2): Boolean {
        var updated = false
        for (letter in 1..game.letters) {
            for (y in 0 until game.size) {
                val xs = (0 until game.size).filter { x ->
                    letter in game.optionsAt(coordinate(x, y))
                }
                if (xs.size <= 1) continue
                val otherYs = (0 until game.size).toSet() - y
                val otherYsToPairWith = otherYs.filter { y2 ->
                    (0 until game.size).filter { it !in xs }.none { x2 ->
                        letter in game.optionsAt(coordinate(x2, y2))
                    }
                }.toSet()
                if (otherYsToPairWith.size + 1 >= xs.size) {
                    var count = 0
                    (otherYs - otherYsToPairWith).forEach { y2 ->
                        for (x2 in xs) {
                            if (game.removeOptions(coordinate(x2, y2), setOf(letter))) {
                                count++
                            }
                        }
                    }
                    if (count > 0) {
                        println("Removed $count ${letter.toEndviewChar()}s using a pair of ${otherYsToPairWith.size + 1}")
                        updated = true
                    }
                }
            }
        }
        return updated
    }

    private fun removeEmptyOptionsWherePossible(): Boolean {
        var updated = false
        for (x in 0 until game.size) {
            applyRemoveEmptyOptionsWherePossible { Int2(x, it) }.also {
                if (it) updated = true
            }
        }
        for (y in 0 until game.size) {
            applyRemoveEmptyOptionsWherePossible { Int2(it, y) }.also {
                if (it) updated = true
            }
        }
        return updated
    }

    private fun applyRemoveEmptyOptionsWherePossible(coordinate: (Int) -> Int2): Boolean {
        var updated = false
        val empties = (0 until game.size).count {
            game.letterAt(coordinate(it)) == 0
        }
        if (empties >= (game.size - game.letters)) {
            for (i in 0 until game.size) {
                if (game.letterAt(coordinate(i)) == null) {
                    game.removeOptions(coordinate(i), setOf(0)).also {
                        if (it) updated = true
                    }
                }
            }
        }
        return updated
    }

    private fun removeOptionsWhereLetterAlreadySet(): Boolean {
        var updated = false
        for (x in 0 until game.size) {
            applyRemoveOptionsWhereLetterAlreadySet { Int2(x, it) }.also {
                if (it) updated = true
            }
        }
        for (y in 0 until game.size) {
            applyRemoveOptionsWhereLetterAlreadySet { Int2(it, y) }.also {
                if (it) updated = true
            }
        }
        return updated
    }

    private fun applyRemoveOptionsWhereLetterAlreadySet(coordinate: (Int) -> Int2): Boolean {
        var updated = false
        val toRemove = mutableSetOf<Int>()
        for (i in 0 until game.size) {
            game.letterAt(coordinate(i))?.let {
                if (it > 0)
                    toRemove += it
            }
        }
        if (toRemove.isNotEmpty()) {
            for (i in 0 until game.size) {
                if (game.letterAt(coordinate(i)) == null)
                    game.removeOptions(coordinate(i), toRemove).also {
                        if (it) updated = true
                    }
            }
        }
        return updated
    }

    private fun findSingleOptions(): Boolean {
        var updated = false

        for (x in 0 until game.size) {
            applyFindSingleOptions { Int2(x, it) }.also {
                if (it) updated = true
            }
        }
        for (y in 0 until game.size) {
            applyFindSingleOptions { Int2(it, y) }.also {
                if (it) updated = true
            }
        }

        return updated
    }

    private fun applyFindSingleOptions(coordinate: (Int) -> Int2): Boolean {
        var updated = false
        val letters = 1..game.letters
        letters.forEach { letter ->
            var index = 0
            val found = (0 until game.size).count {
                val coord = coordinate(it)
                val f = letter in game.optionsAt(coord)
                if (f) {
                    index = it
                }
                f
            }
            if (found == 1) {
                game.setOptions(coordinate(index), setOf(letter)).also {
                    if (it) updated = true
                }
            }
        }

        return updated
    }

    private fun applyHints(): Boolean {
        var updated = false
        for (x in 0 until game.size) {
            val hint = game.northHints[x]
            if (hint > 0) {
                applyHint(hint) {
                    Int2(x, it)
                }.also {
                    if (it) updated = true
                }
            }
        }
        for (x in 0 until game.size) {
            val hint = game.southHints[x]
            if (hint > 0) {
                applyHint(hint) {
                    Int2(x, game.size - it - 1)
                }.also {
                    if (it) updated = true
                }
            }
        }
        for (y in 0 until game.size) {
            val hint = game.westHints[y]
            if (hint > 0) {
                applyHint(hint) {
                    Int2(it, y)
                }.also {
                    if (it) updated = true
                }
            }
        }
        for (y in 0 until game.size) {
            val hint = game.eastHints[y]
            if (hint > 0) {
                applyHint(hint) {
                    Int2(game.size - it - 1, y)
                }.also {
                    if (it) updated = true
                }
            }
        }

        return updated
    }

    private fun setLettersWherePossible(): Boolean {
        var updated = false
        for (y in 0 until game.size) {
            for (x in 0 until game.size) {
                game.setLetter(x, y, game.optionsAt(x, y).singleOrNull()).also {
                    if (it) updated = true
                }
            }
        }
        return updated
    }

    fun fillEmptyCells(): Boolean {
        var updated = false
        for (y in 0 until game.size) {
            for (x in 0 until game.size) {
                if (game.optionsAt(x, y).isEmpty()) {
                    game.setOptions(x, y, (0..game.letters).toSet()).also {
                        if (it) updated = true
                    }
                }
            }
        }
        return updated
    }


    private fun applyHint(hint: Int, coordinate: (Int) -> Int2): Boolean {
        var updated = false
        var found = false
        var index = 0
        do {
            if (index >= game.size) return false

            val coord = coordinate(index)
            index++
            val opts = game.optionsAt(coord)

            if (hint in opts) {
                game.removeOptions(coord, game.allOptions - setOf(hint, 0)).also {
                    if (it) updated = true
                }
                found = true
            } else {
                game.removeOptions(coord, game.allOptions - setOf(0)).also {
                    if (it) updated = true
                }
            }
        } while (!found)

        index = game.size - 1
        val cells = mutableListOf<Set<Int>>()
        while (!cells.canContainAllOf(game.allOptions - setOf(hint, 0))) {
            if (index < 0) return false

            val coord = coordinate(index)
            index--
            cells += game.optionsAt(coord)

            game.removeOptions(coord, setOf(hint)).also {
                if (it) updated = true
            }
        }

        return updated
    }
}

private fun IEndviewGame.optionsAt(coord: Int2) = this.optionsAt(coord.x, coord.y)
private fun IEndviewGame.setOptions(coord: Int2, options: Set<Int>) = this.setOptions(coord.x, coord.y, options)
private fun IEndviewGame.removeOptions(coord: Int2, options: Set<Int>) = this.removeOptions(coord.x, coord.y, options)
private fun IEndviewGame.letterAt(coord: Int2) = this.letterAt(coord.x, coord.y)
