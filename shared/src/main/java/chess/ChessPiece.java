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
                for (int i : new int[]{-1, 1}) {
                    for (int j : new int[]{-1, 1}) {
                        int k = 1;
                        while (true) {
                            ChessPosition nextPosition = new ChessPosition(myPosition.getRow() + (i * k), myPosition.getColumn() + (j * k));
                            if (!nextPosition.validPosition())
                                break;
                            if (board.getPiece(nextPosition) != null) {
                                if (board.getPiece(nextPosition).color == color)
                                    break;
                                validMoves.add(new ChessMove(myPosition, nextPosition, null));
                                break;
                            }
                            validMoves.add(new ChessMove(myPosition, nextPosition, null));
                            k++;
                        }
                    }
                }
            }
            default -> {
                System.err.println("Not implemented yet");
            }
        }
        return validMoves;
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
