package at.robbert.ui

import at.robbert.*
import org.jdatepicker.impl.JDatePanelImpl
import org.jdatepicker.impl.JDatePickerImpl
import org.jdatepicker.impl.UtilDateModel
import java.awt.BorderLayout
import java.awt.event.ItemEvent
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.*
import javax.swing.JFormattedTextField.AbstractFormatter

class EndviewHelperMainUI {
    private var gameComponent: EndviewComponent? = null
    private var game: EndviewGame? = null
    private val frame = JFrame("Endview")
    private val centerPanel = JPanel()
    private val sidePanel = JPanel()

    private var gameOptions: List<String> = emptyList()

    init {
        frame.layout = BorderLayout()
        frame.add(centerPanel, BorderLayout.CENTER)
        frame.add(sidePanel, BorderLayout.EAST)

        setCenter(JLabel("Please start a game"))
        initializeSidePanel()
    }

    private fun setCenter(component: JComponent) {
        centerPanel.removeAll()
        centerPanel.add(component)
        component.isFocusable = true
        component.grabFocus()
    }

    private fun datePicker(): JDatePickerImpl {
        val model = UtilDateModel()
        val datePanel = JDatePanelImpl(model, Properties())
        val dateFormat = SimpleDateFormat.getDateInstance()
        val formatter = object : AbstractFormatter() {
            override fun stringToValue(text: String?): Any {
                if (text == null) return Calendar.getInstance()

                return Calendar.getInstance().also { it.time = dateFormat.parse(text) }
            }

            override fun valueToString(value: Any?): String {
                if (value == null) return ""
                require(value is Calendar)

                return dateFormat.format(value.time)
            }

        }
        return JDatePickerImpl(datePanel, formatter)
    }

    private fun initializeSidePanel() {
        sidePanel.layout = BoxLayout(sidePanel, BoxLayout.Y_AXIS)

        val datePicker = datePicker()
        sidePanel.add(datePicker)

        val gameModel = DefaultComboBoxModel<String>()
        val gamePicker = JComboBox(gameModel)
        gamePicker.addItemListener {
            if (it.stateChange == ItemEvent.SELECTED) {
                if (gamePicker.selectedIndex in gameOptions.indices) {
                    this.game = EndviewGameFactory.createEndviewGame(gameOptions[gamePicker.selectedIndex])
                    this.gameComponent = EndviewComponent(this.game!!)
                    setCenter(this.gameComponent!!)
                    frame.pack()
                }
            }
        }
        sidePanel.add(gamePicker)

        val getHint = JButton("Get hint")
        sidePanel.add(getHint)

        val reset = JButton("Reset")
        sidePanel.add(reset)

        val solve = JButton("Solve")
        sidePanel.add(solve)

        solve.addActionListener {
            game?.let { g ->
                EndviewHintGenerator(g).solveStep()
            }
        }
        reset.addActionListener {
            game?.let { g ->
                for (y in 0 until g.size) {
                    for (x in 0 until g.size) {
                        g.setOptions(x, y, setOf())
                        g.setLetter(x, y, null)
                    }
                }
            }
        }
        getHint.addActionListener {
            val game = this.game ?: return@addActionListener
            val readOnlyGame = ReadonlyEndviewGame(FilledOutEndviewGame(game))
            val hintGen = EndviewHintGenerator(readOnlyGame)
            hintGen.solveStep()
            println("Got ${readOnlyGame.attemptedChanges.distinct().size} hints")
            if (readOnlyGame.attemptedChanges.isNotEmpty())
                gameComponent?.let { gc ->
                    readOnlyGame.attemptedChanges.distinct().random().let { coord ->
                        gc.selectCell(coord.x, coord.y)
                    }
                }
            else
                gameComponent?.deselectCell()
        }

        datePicker.model.addChangeListener {
            val value = datePicker.model.value ?: return@addChangeListener

            val format = SimpleDateFormat("yyyy-MM-dd")
            val puzzles =
                WordleGameDownloader("daily-puzzles-${format.format(value)}.json").getPuzzles("endview")
            this.gameOptions = puzzles
            gameModel.removeAllElements()
            gameModel.addAll(List(gameOptions.size) { index ->
                when (index) {
                    0 -> "Easy"
                    1 -> "Normal"
                    2 -> "Hard"
                    else -> "ULTRA"
                }
            })
        }
    }

    fun show() {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }
}
