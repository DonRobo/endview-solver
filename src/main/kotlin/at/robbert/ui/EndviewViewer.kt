package at.robbert.ui

import at.robbert.EndviewGame
import at.robbert.toEndviewChar
import java.awt.*
import java.awt.geom.AffineTransform
import javax.swing.JComponent
import javax.swing.JFrame
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

private const val gridSize = 64

private enum class BoardDirection {
    NORTH, SOUTH, EAST, WEST
}

private class HintComponent(
    val game: EndviewGame,
    val boardDirection: BoardDirection,
    val position: Int,
) : JComponent() {

    override fun getPreferredSize(): Dimension {
        return Dimension(gridSize, gridSize)
    }

    override fun paint(g: Graphics?) {
        val g2 = g as Graphics2D
        val hint = when (boardDirection) {
            BoardDirection.NORTH -> game.northHints
            BoardDirection.SOUTH -> game.southHints
            BoardDirection.EAST -> game.eastHints
            BoardDirection.WEST -> game.westHints
        }[position]
        val hintString = when (hint) {
            0 -> ""
            else -> hint.toEndviewChar().toString()
        }
        g2.drawString(hintString, 30, 30)
    }

}

private class EndviewComponent(
    val game: EndviewGame,
) : JComponent() {

    var cellSize: Float
        get() {
            return min(size.width, size.height).toFloat() / (game.size + 2)
        }
        set(value) {
            val s = ceil(value * (game.size + 2)).roundToInt()
            size = Dimension(s, s)
            preferredSize = size
        }

    init {
        cellSize = 64f
    }

    private val hintFont = Font("Arial Rounded MT Bold", Font.BOLD, 60)
    private val optionFont = Font("Arial Rounded MT Bold", Font.BOLD, 25)

    override fun paint(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.addRenderingHints(
            RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            )
        )

        val originalTransform = g2.transform.clone() as AffineTransform

        val scale = cellSize / 100.0
        g2.scale(scale, scale)

        for (y in -1 until game.size + 1) {
            for (x in -1 until game.size + 1) {
                g2.translate(100 * (x + 1), 100 * (y + 1))

                val inBorderX = x in 0 until game.size
                val inBorderY = y in 0 until game.size
                when {
                    inBorderX && inBorderY -> paintGameCell(g2, x, y)
                    !inBorderX && !inBorderY -> paintEmptyCell(g2)
                    inBorderX xor inBorderY -> {
                        val hint = when {
                            x == -1 -> game.westHints[y].toEndviewChar()
                            x == game.size -> game.eastHints[y].toEndviewChar()
                            y == -1 -> game.northHints[x].toEndviewChar()
                            y == game.size -> game.southHints[x].toEndviewChar()
                            else -> 'X'
                        }

                        paintHint(g2, if (hint == '~') ' ' else hint)
                    }

                    else -> error("Not possible")
                }

                g2.translate(-100 * (x + 1), -100 * (y + 1))
            }
        }

        g2.paint = Color.BLACK
        g2.stroke = BasicStroke(5f)
        for (x in 0..game.size) {
            g2.drawLine(100 + x * 100, 100, 100 + x * 100, game.size * 100 + 100)
        }
        for (y in 0..game.size) {
            g2.drawLine(100, 100 + y * 100, game.size * 100 + 100, 100 + y * 100)
        }
        g2.transform = originalTransform
    }

    private fun paintHint(g2: Graphics2D, hint: Char) {
        g2.paint = Color.WHITE
        g2.fillRect(0, 0, 100, 100)
        g2.paint = Color.BLACK
        g2.font = hintFont
        g2.drawStringCenteredInRect(hint.toString(), Rectangle(0, 0, 100, 100))
    }

    private fun paintEmptyCell(g2: Graphics2D) {
        g2.paint = Color.WHITE
        g2.fillRect(0, 0, 100, 100)
    }

    private fun paintGameCell(g2: Graphics2D, cellX: Int, cellY: Int) {
        g2.paint = Color.WHITE
        g2.fillRect(0, 0, 100, 100)

        g2.paint = Color.BLACK
        g2.font = optionFont
        val setLetter = game.letterAt(cellX, cellY)
        if (setLetter != null) {
            g2.drawStringCenteredInRect(setLetter.toEndviewChar().toString(), Rectangle(0, 0, 100, 100))
        } else {
            val options = game.optionsAt(cellX, cellY)
            val lettersInLine = ceil(sqrt(game.letters + 1f)).roundToInt()
            val lines = ceil((game.letters + 1f) / lettersInLine).roundToInt()
            val rectSize = 100 / lettersInLine
            val lineHeight = 100 / lines
            for (l in 0..game.letters) {
                val isInOptions = if (l == game.letters)
                    0 in options
                else
                    l + 1 in options

                if (isInOptions) {
                    val x = l % lettersInLine
                    val y = l / lettersInLine
                    val rect = Rectangle(rectSize * x, lineHeight * y, rectSize, lineHeight)
                    g2.drawStringCenteredInRect(
                        (if (l < game.letters)
                            (l + 1).toEndviewChar()
                        else
                            '~').toString(),
                        rect
                    )
                }
            }
        }
    }

}

private fun Graphics2D.drawStringCenteredInRect(text: String, rectangle: Rectangle) {
    val bounds = fontMetrics.getStringBounds(text, this)
    drawString(
        text,
        (rectangle.width / 2f + rectangle.x - bounds.width / 2f - bounds.x).toFloat(),
        (rectangle.height / 2f + rectangle.y - bounds.height / 2f - bounds.y).toFloat()
    )
}

class EndviewViewer(
    val game: EndviewGame
) {
    private val frame = JFrame("Endview")

    init {
        frame.add(EndviewComponent(game))
    }

    fun show() {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }
}
