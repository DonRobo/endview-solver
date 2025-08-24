package at.robbert.gridgames

interface IEndviewGame {
    val size: Int
    val letters: Int
    val northHints: List<Int>
    val southHints: List<Int>
    val westHints: List<Int>
    val eastHints: List<Int>
    val allOptions: Set<Int>
    fun letterAt(x: Int, y: Int): Int?
    fun optionsAt(x: Int, y: Int): Set<Int>
    fun toggleOptions(x: Int, y: Int, char: Int): Boolean
    fun setOptions(x: Int, y: Int, options: Set<Int>): Boolean
    fun removeOptions(x: Int, y: Int, options: Set<Int>): Boolean
    fun setLetter(x: Int, y: Int, letter: Int?): Boolean
    fun addUpdateListener(listener: () -> Unit)
}
