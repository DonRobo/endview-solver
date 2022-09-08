package at.robbert

class EndviewGame(
    val size: Int,
    val letters: Int,
    hints: String,
) {
    private val setLetters = IntArray(size * size) { -1 }
    private val options = mutableMapOf<Int, List<Int>>()

    fun letterAt(cellX: Int, cellY: Int): Int? {
        val l = setLetters[cellX + cellY * size]
        return if (l >= 0)
            l
        else
            null
    }

    fun optionsAt(cellX: Int, cellY: Int): List<Int> {
        return options[cellX + cellY * size] ?: emptyList()
    }

    val northHints: List<Int>
    val southHints: List<Int>
    val westHints: List<Int>
    val eastHints: List<Int>

    init {
        fun hints(i: Int): List<Int> {
            return hints.substring(i * size, (i + 1) * size).map { it.digitToInt() }
        }

        northHints = hints(0)
        southHints = hints(1)
        westHints = hints(2)
        eastHints = hints(3)
    }
}

fun Int.toEndviewChar(): Char {
    return if (this == 0)
        '~'
    else
        'A' + (this - 1)
}
