package ui;

import static ui.EscapeSequences.*;

public class TextField extends Option{
    private final String textWhileEmpty;
    private StringBuilder contents;

    public TextField(String text) {
        this.textWhileEmpty = text;
        contents = new StringBuilder();
    }

    @Override
    public String toString() {
        if (contents.isEmpty()) {return SET_TEXT_COLOR_LIGHT_GREY + textWhileEmpty + RESET_ALL_FORMATTING;}
        return contents.toString();
    }

    @Override
    public String toStringHighlighted() {
        if (contents.isEmpty()) {return SET_TEXT_COLOR_LIGHT_GREY + SET_TEXT_BOLD + textWhileEmpty + RESET_ALL_FORMATTING;}
        return SET_TEXT_BOLD + contents + RESET_ALL_FORMATTING;
    }

    public void addChar(char c) {
        contents.append(c);
    }

    public void removeChar() {
        if (contents.isEmpty()) {return;}
        contents.deleteCharAt(contents.length() - 1);
    }
}
