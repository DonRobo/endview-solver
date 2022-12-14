package at.robbert.ui

import at.robbert.*
import java.awt.*
import java.awt.event.*
import java.awt.geom.AffineTransform
import javax.swing.JComponent
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class EndviewComponent(
    val game: IEndviewGame,
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

    private var selectedCell = -1
    private val hintFont = Font("Arial Rounded MT Bold", Font.BOLD, 60)

    private val optionFont = Font("Arial Rounded MT Bold", Font.BOLD, 25)

    init {
        cellSize = 64f

        isFocusable = true
        addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {
            }

            override fun keyPressed(e: KeyEvent) {
                this@EndviewComponent.keyPressed(e)
            }

            override fun keyReleased(e: KeyEvent?) {
            }

        })
        addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) {
            }

            override fun mousePressed(e: MouseEvent) {
                this@EndviewComponent.mouseClicked(e)
            }

            override fun mouseReleased(e: MouseEvent?) {
            }

            override fun mouseEntered(e: MouseEvent?) {
            }

            override fun mouseExited(e: MouseEvent?) {
            }
        })
        addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) {
            }

            override fun mouseMoved(e: MouseEvent?) {
            }
        })
        game.addUpdateListener {
            repaint()
        }
    }

    private fun mouseClicked(mouseEvent: MouseEvent) {
        val scale = cellSize / 100.0

        val scaledX = (mouseEvent.x / scale) - 100
        val scaledY = (mouseEvent.y / scale) - 100

        val cellX = (scaledX / 100).toInt()
        val cellY = (scaledY / 100).toInt()

        if (cellX !in 0 until game.size) return
        if (cellY !in 0 until game.size) return

        when (mouseEvent.button) {
            MouseEvent.BUTTON1 -> toggleSelectCell(cellX, cellY)
            MouseEvent.BUTTON2 -> solveCell(cellX, cellY).also { selectCell(cellX, cellY) }
            MouseEvent.BUTTON3 -> clearCell(cellX, cellY).also { selectCell(cellX, cellY) }
        }
    }

    private fun solveCell(cellX: Int, cellY: Int) {
        val maskedGame = MaskedEndviewGame(FilledOutEndviewGame(game), setOf(Int2(cellX, cellY)))
        EndviewHintGenerator(maskedGame).solveStep()
    }

    private fun clearCell(cellX: Int, cellY: Int) {
        game.setOptions(cellX, cellY, emptySet())
        game.setLetter(cellX, cellY, null)
    }

    fun keyPressed(e: KeyEvent) {
        if (selectedCell >= 0) {
            val x = selectedCell % game.size
            val y = selectedCell / game.size

            val char = e.keyChar
            if (char in 'a'..'z' || char == ' ') {
                val selectedChar = when (char) {
                    in 'a'..'z' -> (char - 'a') + 1
                    ' ' -> 0
                    else -> error("Shouldn't happen")
                }

                if (selectedChar in 0..game.letters) {
                    game.toggleOptions(
                        x, y, selectedChar
                    )
                    game.setLetter(x, y, game.optionsAt(x, y).singleOrNull())
                }
            }
        }
    }

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
        if (isCellSelected(cellX, cellY)) {
            g2.paint = Color.LIGHT_GRAY
        } else {
            g2.paint = Color.WHITE
        }
        g2.fillRect(0, 0, 100, 100)

        g2.paint = Color.BLACK
        val setLetter = game.letterAt(cellX, cellY)
        if (setLetter != null) {
            g2.font = hintFont
            g2.drawStringCenteredInRect(setLetter.toEndviewChar().toString(), Rectangle(0, 0, 100, 100))
        } else {
            g2.font = optionFont
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

    fun isCellSelected(cellX: Int, cellY: Int): Boolean {
        return selectedCell == cellX + cellY * game.size
    }

    fun toggleSelectCell(cellX: Int, cellY: Int): Boolean {
        require(cellX in 0 until game.size)
        require(cellY in 0 until game.size)

        return if (isCellSelected(cellX, cellY)) {
            deselectCell()
            false
        } else {
            selectCell(cellX, cellY)
            true
        }
    }

    fun selectCell(cellX: Int, cellY: Int): Boolean {
        require(cellX in 0 until game.size)
        require(cellY in 0 until game.size)

        val cellIndex = cellX + cellY * game.size
        val wasAlreadySelected = selectedCell == cellIndex
        selectedCell = cellIndex

        grabFocus()
        if (!wasAlreadySelected) repaint()
        return !wasAlreadySelected
    }

    fun deselectCell(): Boolean {
        val wasSelected = selectedCell >= 0

        selectedCell = -1

        if (wasSelected) repaint()

        return wasSelected
    }

    private fun Graphics2D.drawStringCenteredInRect(text: String, rectangle: Rectangle) {
        val bounds = fontMetrics.getStringBounds(text, this)
        drawString(
            text,
            (rectangle.width / 2f + rectangle.x - bounds.width / 2f - bounds.x).toFloat(),
            (rectangle.height / 2f + rectangle.y - bounds.height / 2f - bounds.y).toFloat()
        )
    }

}
