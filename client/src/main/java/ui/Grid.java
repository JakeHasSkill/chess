package ui;

public interface Grid {
    public void setSelectedElement(int row, int col);

    public Destination sendKeyPress(CLIKeyListener.KeyPress keyPress);

    public void backspace();

    public void sendLetter(char c);

    public String getValue(int row, int col);

    public int getSelectedRow();
    public int getSelectedColumn();

    @Override
    public String toString();
}
