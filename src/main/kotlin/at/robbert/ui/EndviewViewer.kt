package at.robbert.ui

import at.robbert.EndviewGame
import java.awt.BorderLayout
import javax.swing.JFrame

class EndviewViewer(
    game: EndviewGame
) {
    private val frame = JFrame("Endview")

    init {
        frame.layout = BorderLayout()
        frame.add(EndviewComponent(game), BorderLayout.CENTER)
    }

    fun show() {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }
}
