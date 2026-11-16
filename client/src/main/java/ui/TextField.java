package ui;

import static ui.EscapeSequences.*;

public class TextField extends Option{
    private final String textWhileEmpty;
    private StringBuilder contents;
    private final boolean confidential;

    public TextField(String text, boolean confidential) {
        this.textWhileEmpty = text;
        contents = new StringBuilder();
        this.confidential = confidential;
    }

    public TextField(String text) {
        this.textWhileEmpty = text;
        contents = new StringBuilder();
        this.confidential = false;
    }

    private String getContents() {
        if (confidential) {return "*".repeat(contents.length());}
        else {return contents.toString();}
    }

    @Override
    public String toString() {
        if (contents.isEmpty()) {return SET_TEXT_COLOR_LIGHT_GREY + textWhileEmpty + RESET_ALL_FORMATTING;}
        return getContents();
    }

    @Override
    public String toStringHighlighted() {
        if (contents.isEmpty()) {return SET_TEXT_COLOR_LIGHT_GREY + SET_TEXT_BOLD + textWhileEmpty + RESET_ALL_FORMATTING;}
        return SET_TEXT_BOLD + getContents() + RESET_ALL_FORMATTING;
    }

    public void addChar(char c) {
        contents.append(c);
    }

    public void removeChar() {
        if (contents.isEmpty()) {return;}
        contents.deleteCharAt(contents.length() - 1);
    }

    public String getValue() {
        return contents.toString();
    }
}
