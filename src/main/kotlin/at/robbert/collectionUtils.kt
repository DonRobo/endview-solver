package at.robbert

/**
 * Checks if the collection of sets can together contain all elements in the options set.
 * This means that for each element in options, there is at least one set in the collection that contains it.
 * Additionally, there must be enough sets in the collection that contain at least one of the options
 * to cover all elements in options.
 *
 * @param options The set of elements to check against the collection of sets.
 * @return True if the collection can contain all elements in options, false otherwise.
 */
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
