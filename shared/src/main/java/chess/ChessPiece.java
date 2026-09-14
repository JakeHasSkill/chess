package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;
    private final ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        switch (type) {
            case BISHOP -> {
                for (int[] i : new int[][]{{1, 1}, {-1, 1}, {-1, -1}, {1, -1}}) {
                    straightLineMovement(board, myPosition, validMoves, i);
                }
            }
            case ROOK -> {
                for (int[] i : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                    straightLineMovement(board, myPosition, validMoves, i);
                }
            }
            case QUEEN -> {
                for (int[] i : new int[][]{{1, 1}, {-1, 1}, {-1, -1}, {1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                    straightLineMovement(board, myPosition, validMoves, i);
                }
            }
            case KING -> {
                for (int[] i : new int[][]{{1, 1}, {1, 0}, {1, -1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}}) {
                    singlePositionMovement(board, myPosition, i, validMoves);
                }
            }
            case KNIGHT -> {
                for (int[] i : new int[][]{{1, 2}, {2, 1}, {-1, 2}, {2, -1}, {-1, -2}, {-2, -1}, {1, -2}, {-2, 1}}) {
                    singlePositionMovement(board, myPosition, i, validMoves);
                }
            }
            case PAWN -> {

            }
            default -> {
                System.err.println("Not implemented yet");
            }
        }
        return validMoves;
    }

    /**
     * Helper function for pieceMoves
     * For pieces that move to single positions like the King, pawns, and knight
     */
    private void singlePositionMovement(ChessBoard board, ChessPosition myPosition, int[] i, ArrayList<ChessMove> validMoves) {
        ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + i[0], myPosition.getColumn() + i[1]);
        if (!nextPosition.validPosition())
            return;
        if (board.getPiece(nextPosition) != null) {
            if (board.getPiece(nextPosition).color == color)
                return;
            validMoves.add(new ChessMove(myPosition, nextPosition, null));
            return;
        }
        validMoves.add(new ChessMove(myPosition, nextPosition, null));
    }

    /**
     * Helper function for pieceMoves
     * For pieces that move in a line as far as they can, diagonally or straight
     */
    private void straightLineMovement(ChessBoard board, ChessPosition myPosition, ArrayList<ChessMove> validMoves, int[] i) {
        int j = 1;
        while (true) {
            ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + (i[0] * j), myPosition.getColumn() + (i[1] * j));
            if (!nextPosition.validPosition())
                break;
            if (board.getPiece(nextPosition) != null) {
                if (board.getPiece(nextPosition).color == color)
                    break;
                validMoves.add(new ChessMove(myPosition, nextPosition, null));
                break;
            }
            validMoves.add(new ChessMove(myPosition, nextPosition, null));
            j++;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) object;
        return this.type == that.type && this.color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color) * 31;
    }

    @Override
    public String toString() {
        return "{Piece:" + type.name() + " Color:" + color.name() + "}";
    }
}
