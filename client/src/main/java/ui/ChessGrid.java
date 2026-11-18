package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPiece.PieceType;

import java.util.ArrayList;
import java.util.Map;

import static ui.EscapeSequences.*;

public class ChessGrid implements Grid {
    private final ChessTile[][] tiles;
    private int selectedRow, selectedColumn;
    private final Destination currentScreen;
    private final ChessGame.TeamColor color;
    private static final Map<PieceType, String> whitePieces = Map.of(
            PieceType.ROOK, WHITE_ROOK,
            PieceType.BISHOP, WHITE_BISHOP,
            PieceType.KNIGHT, WHITE_KNIGHT,
            PieceType.QUEEN, WHITE_QUEEN,
            PieceType.KING, WHITE_KING,
            PieceType.PAWN, WHITE_PAWN
    );
    private static final Map<PieceType, String> blackPieces = Map.of(
            PieceType.ROOK, BLACK_ROOK,
            PieceType.BISHOP, BLACK_BISHOP,
            PieceType.KNIGHT, BLACK_KNIGHT,
            PieceType.QUEEN, BLACK_QUEEN,
            PieceType.KING, BLACK_KING,
            PieceType.PAWN, BLACK_PAWN
    );

    public ChessGrid(Destination currentScreen, ChessPiece[][] board, ChessGame.TeamColor color) {
        this.color = color;
        ArrayList<ArrayList<ChessTile>> tilesBuilder = new ArrayList<>();
        if (color == ChessGame.TeamColor.WHITE) {
            for (int row = 7; row >= 0; row--) {
                ArrayList<ChessTile> currentRow = new ArrayList<>();
                for (int col = 7; col >= 0; col--) {
                    boolean whiteTile = (col + row) % 2 == 0;
                    ChessPiece piece = board[row][col];
                    if (piece == null) {
                        currentRow.add(new ChessTile("  ", whiteTile));
                    } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        currentRow.add(new ChessTile(blackPieces.get(piece.getPieceType()), whiteTile));
                    } else {
                        currentRow.add(new ChessTile(whitePieces.get(piece.getPieceType()), whiteTile));
                    }

                }
                tilesBuilder.add(currentRow);
            }
        } else {
            for (int row = 0; row <= 7; row++) {
                ArrayList<ChessTile> currentRow = new ArrayList<>();
                for (int col = 0; col <= 7; col++) {
                    boolean whiteTile = (col + row) % 2 == 0;
                    ChessPiece piece = board[row][col];
                    if (piece == null) {
                        currentRow.add(new ChessTile("  ", whiteTile));
                    } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        currentRow.add(new ChessTile(blackPieces.get(piece.getPieceType()), whiteTile));
                    } else {
                        currentRow.add(new ChessTile(whitePieces.get(piece.getPieceType()), whiteTile));
                    }

                }
                tilesBuilder.add(currentRow);
            }
        }
        this.tiles = tilesBuilder.stream().map(row -> row.toArray(new ChessTile[0])).toArray(ChessTile[][]::new);
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
        if (selectedRow == tiles[0].length - 1) {return;}
        selectedRow++;
    }

    private void goLeft() {
        if (selectedColumn == 0) {return;}
        selectedColumn--;
    }

    private void goRight() {
        if (selectedColumn == tiles.length - 1) {return;}
        selectedColumn++;
    }

    public Destination sendKeyPress(CLIKeyListener.KeyPress keyPress) {
        if (keyPress.key() == CLIKeyListener.Key.ESCAPE) {return Destination.LIST_GAMES;}
        return currentScreen;
    }

    public void backspace() {

    }

    public void sendLetter(char c) {

    }

    public String getValue(int row, int col) {
        return tiles[col][row].toString();
    }

    public int getSelectedRow() {return selectedRow;}
    public int getSelectedColumn() {return selectedColumn;}

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        if (color == ChessGame.TeamColor.WHITE) {
            output.append(SET_TEXT_COLOR_LIGHT_GREY + "  ａｂｃｄｅｆｇｈ  \r\n" + RESET_ALL_FORMATTING);
            for (int row = 0; row <= 7; row++) {
                output.append(SET_TEXT_COLOR_LIGHT_GREY).append(8 - row).append(" ").append(RESET_ALL_FORMATTING);
                for (int col = 0; col <= 7; col++) {
                    if (row == selectedRow && col == selectedColumn) {
                        output.append(tiles[col][row].toStringHighlighted());
                    } else {
                        output.append(tiles[col][row]);
                    }
                }
                output.append(SET_TEXT_COLOR_LIGHT_GREY).append(" ").append(8 - row).append(RESET_ALL_FORMATTING);
                output.append("\r\n");
            }
            output.append(SET_TEXT_COLOR_LIGHT_GREY + "  ａｂｃｄｅｆｇｈ  \r\n" + RESET_ALL_FORMATTING);
        } else {
            output.append(SET_TEXT_COLOR_LIGHT_GREY + "  ｈｇｆｅｄｃｂａ  \r\n" + RESET_ALL_FORMATTING);
            for (int row = 0; row <= 7; row++) {
                output.append(SET_TEXT_COLOR_LIGHT_GREY).append(1 + row).append(" ").append(RESET_ALL_FORMATTING);
                for (int col = 0; col <= 7; col++) {
                    if (row == selectedRow && col == selectedColumn) {
                        output.append(tiles[col][row].toStringHighlighted());
                    } else {
                        output.append(tiles[col][row]);
                    }
                }
                output.append(SET_TEXT_COLOR_LIGHT_GREY).append(" ").append(1 + row).append(RESET_ALL_FORMATTING);
                output.append("\r\n");
            }
            output.append(SET_TEXT_COLOR_LIGHT_GREY + "  ｈｇｆｅｄｃｂａ  \r\n" + RESET_ALL_FORMATTING);
        }
        return output.toString().stripTrailing();
    }
}
