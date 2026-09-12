package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition start, end;
    private final ChessPiece.PieceType promotion;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotion = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        ChessMove that = (ChessMove) object;
        if (promotion == null && that.promotion == null) {
            return this.start.equals(that.start) && this.end.equals(that.end);
        }
        else if (promotion == null || that.promotion == null) {
            return false;
        }
        return this.start.equals(that.start) && this.end.equals(that.end) && this.promotion.name().equals(that.promotion.name());
    }

    @Override
    public int hashCode() {
        if (promotion != null) {
            return start.hashCode() * end.hashCode() * promotion.hashCode() * 31;
        }
        return start.hashCode() * end.hashCode() * 31;
    }

    @Override
    public String toString(){
        String out = "{Move:" + start.toString() + " to " + end.toString();
        if (promotion != null) {
            out += " with promotion " + promotion.name();
        }
        return out + "}";
    }
}
