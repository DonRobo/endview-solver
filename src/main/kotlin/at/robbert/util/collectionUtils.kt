package at.robbert.util

fun <E> Collection<Set<E>>.canContainAllOf(options: Set<E>): Boolean {
    if (this.size < options.size) return false
    if (!options.all { o ->
            this.any { c ->
                o in c
            }
        }) return false

    if (this.count { it.containsAny(options) } < options.size) return false

    return true
}


fun <E> Set<E>.containsAny(options: Set<E>): Boolean {
    return options.any { it in this }
}
