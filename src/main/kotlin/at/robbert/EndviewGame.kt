package at.robbert

class EndviewGame(
    val size: Int,
    val letters: Int,
    hints: String,
) {
    private val setLetters = IntArray(size * size) { -1 }
    private val options = mutableMapOf<Int, MutableSet<Int>>()

    private val updateListeners = mutableListOf<() -> Unit>()

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

    fun letterAt(x: Int, y: Int): Int? {
        val l = setLetters[x + y * size]
        return if (l >= 0)
            l
        else
            null
    }

    fun optionsAt(x: Int, y: Int): Set<Int> {
        return options[x + y * size] ?: emptySet()
    }

    fun toggleOptions(x: Int, y: Int, char: Int): Boolean {
        require(char == 0 || char in 1..letters)

        val set = options.computeIfAbsent(x + y * size) {
            mutableSetOf()
        }

        return if (char in set) {
            set.remove(char)
            false
        } else {
            set.add(char)
            true
        }.also {
            updated()
        }
    }

    fun setLetter(x: Int, y: Int, letter: Int?) {
        setLetters[x + y * size] = letter ?: -1
        updated()
    }

    private fun updated() {
        updateListeners.forEach { listener ->
            listener()
        }
    }

    fun addUpdateListener(listener: () -> Unit) {
        updateListeners += listener
    }
}

fun Int.toEndviewChar(): Char {
    return if (this == 0)
        '~'
    else
        'A' + (this - 1)
}
