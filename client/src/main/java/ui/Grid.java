package ui;

public interface Grid {
    Destination sendKeyPress(CLIKeyListener.KeyPress keyPress);
    void backspace();
    void sendLetter(char c);
    String getValue(int row, int col);
    int getSelectedRow();
    int getSelectedColumn();
    @Override
    String toString();
}
