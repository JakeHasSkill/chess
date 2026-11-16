package ui;

public class OptionGrid {
    private final Option[][] options;
    private int selectedRow, selectedColumn;
    private final Screen screen;

    public OptionGrid(Screen screen, Option[][] gridOptions) {
        this.options = gridOptions;
        selectedRow = 0;
        selectedColumn = 0;
        this.screen = screen;
    }

    public void setSelectedElement(int row, int col) {
        selectedRow = row;
        selectedColumn = col;
    }

    public void goUp() {
        if (selectedRow == 0) {return;}
        selectedRow--;
    }

    public void goDown() {
        if (selectedRow == options[0].length - 1) {return;}
        selectedRow++;
    }

    public void goLeft() {
        if (selectedColumn == 0) {return;}
        selectedColumn--;
    }

    public void goRight() {
        if (selectedColumn == options.length - 1) {return;}
        selectedColumn++;
    }

    public Screen select() {
        if (options[selectedColumn][selectedRow].getClass() == Button.class) {
            return ((Button) options[selectedColumn][selectedRow]).getDestination();
        }
        return screen;
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
