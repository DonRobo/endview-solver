package at.robbert.gridgames.endview

import at.robbert.gridgames.IEndviewGame

class EndviewGame(
    override val size: Int,
    override val letters: Int,
    hints: String,
) : IEndviewGame {
    private val setLetters = IntArray(size * size) { -1 }
    private val options = mutableMapOf<Int, MutableSet<Int>>()

    private val updateListeners = mutableListOf<() -> Unit>()

    override val northHints: List<Int>
    override val southHints: List<Int>
    override val westHints: List<Int>
    override val eastHints: List<Int>

    init {
        fun hints(i: Int): List<Int> {
            return hints.substring(i * size, (i + 1) * size).map { it.digitToInt() }
        }

        northHints = hints(0)
        southHints = hints(1)
        westHints = hints(2)
        eastHints = hints(3)
    }

    override fun letterAt(x: Int, y: Int): Int? {
        requireValidCoordinates(x, y)

        val l = setLetters[x + y * size]
        return if (l >= 0)
            l
        else
            null
    }

    override fun optionsAt(x: Int, y: Int): Set<Int> {
        requireValidCoordinates(x, y)

        return options[x + y * size] ?: emptySet()
    }

    private fun getMutableOptions(x: Int, y: Int) = options.computeIfAbsent(x + y * size) {
        mutableSetOf()
    }

    override fun toggleOptions(x: Int, y: Int, char: Int): Boolean {
        require(char.isValidChar())
        requireValidCoordinates(x, y)

        val set = getMutableOptions(x, y)

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

    override fun setOptions(x: Int, y: Int, options: Set<Int>): Boolean {
        require(options.all { it.isValidChar() })
        requireValidCoordinates(x, y)

        val oldOptions = this.options[x + y * size]
        this.options[x + y * size] = options.toMutableSet()
        return if (oldOptions != this.options[x + y * size]) {
            updated()
            true
        } else {
            false
        }
    }

    override fun removeOptions(x: Int, y: Int, options: Set<Int>): Boolean {
        requireValidCoordinates(x, y)
        require(options.all { it.isValidChar() })

        return getMutableOptions(x, y).removeAll(options).also {
            if (it)
                updated()
        }
    }

    override val allOptions = (0..letters).toSet()

    override fun setLetter(x: Int, y: Int, letter: Int?): Boolean {
        requireValidCoordinates(x, y)
        letter?.let { require(it.isValidChar()) }

        val prev = setLetters[x + y * size]
        setLetters[x + y * size] = letter ?: -1
        return if (prev != setLetters[x + y * size]) {
            updated()
            true
        } else {
            false
        }
    }

    private fun requireValidCoordinates(x: Int, y: Int) {
        require(x in 0 until size)
        require(y in 0 until size)
    }

    private fun updated() {
        updateListeners.forEach { listener ->
            listener()
        }
    }

    override fun addUpdateListener(listener: () -> Unit) {
        updateListeners += listener
    }

    private fun Int.isValidChar(): Boolean {
        return this == 0 || this in 1..letters
    }
}

fun Int.toEndviewChar(): Char {
    return if (this == 0)
        '~'
    else
        'A' + (this - 1)
}
