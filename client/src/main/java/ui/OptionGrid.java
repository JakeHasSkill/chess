package ui;

public class OptionGrid {
    private final Option[][] options;
    private int selectedRow, selectedColumn;
    private final Destination currentScreen;

    public OptionGrid(Destination currentScreen, Option[][] gridOptions) {
        this.options = gridOptions;
        selectedRow = 0;
        selectedColumn = 0;
        this.currentScreen = currentScreen;
    }

    public void setSelectedElement(int row, int col) {
        selectedRow = row;
        selectedColumn = col;
    }

    private void goUp() {
        if (selectedRow == 0) {return;}
        selectedRow--;
    }

    private void goDown() {
        if (selectedRow == options[0].length - 1) {return;}
        selectedRow++;
    }

    private void goLeft() {
        if (selectedColumn == 0) {return;}
        selectedColumn--;
    }

    private void goRight() {
        if (selectedColumn == options.length - 1) {return;}
        selectedColumn++;
    }

    public Destination sendKeyPress(CLIKeyListener.KeyPress keyPress) {
        if (keyPress.key() == CLIKeyListener.Key.UP) {goUp();}
        else if (keyPress.key() == CLIKeyListener.Key.DOWN || keyPress.key() == CLIKeyListener.Key.TAB) {
            goDown();
        }
        else if (keyPress.key() == CLIKeyListener.Key.ENTER) {
            if (options[selectedColumn][selectedRow].getClass() == Button.class) {
                return ((Button) options[selectedColumn][selectedRow]).getDestination();
            }
        }
        else if (keyPress.key() == CLIKeyListener.Key.ESCAPE) {return Destination.PRE_LOGIN;}
        else if (keyPress.key() == CLIKeyListener.Key.BACKSPACE) {backspace();}
        else if (keyPress.key() == CLIKeyListener.Key.CHAR) {sendLetter(keyPress.character());}
        return currentScreen;
    }

    public void backspace() {
        if (options[selectedColumn][selectedRow].getClass() == TextField.class) {
            ((TextField) options[selectedColumn][selectedRow]).removeChar();
        }
    }

    public void sendLetter(char c) {
        if (options[selectedColumn][selectedRow].getClass() == TextField.class) {
            ((TextField) options[selectedColumn][selectedRow]).addChar(c);
        }
    }

    public String getValue(int row, int col) {
        if (options[col][row].getClass() == TextField.class) {
            return ((TextField) options[col][row]).getValue();
        }
        return "";
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int row = 0; row < options[0].length; row++) {
            for (int col = 0; col < options.length; col++) {
                if (row == selectedRow && col == selectedColumn) {
                    output.append(options[col][row].toStringHighlighted());
                }
                else {output.append(options[col][row]);}
            }
            output.append("\r\n");
        }
        return output.toString().stripTrailing();
    }
}
