package passoff.chess.piecemoves;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoff.chess.TestUtilities;

public class BishopMoveTests {

    @Test
    public void testEquals() {
        Assertions.assertEquals(new ChessPosition(3, 5), new ChessPosition(3, 5));
        Assertions.assertEquals(new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        Assertions.assertEquals(new ChessMove(new ChessPosition(4, 5), new ChessPosition(5, 6), null), new ChessMove(new ChessPosition(4, 5), new ChessPosition(5, 6), null));
    }

    @Test
    public void bishopMoveUntilEdge() {
        TestUtilities.validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |B| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(5, 4),
                new int[][]{
                        {6, 5}, {7, 6}, {8, 7},
                        {4, 5}, {3, 6}, {2, 7}, {1, 8},
                        {4, 3}, {3, 2}, {2, 1},
                        {6, 3}, {7, 2}, {8, 1},
                }
        );
    }


    @Test
    public void bishopCaptureEnemy() {
        TestUtilities.validateMoves("""
                        | | | | | | | | |
                        | | | |Q| | | | |
                        | | | | | | | | |
                        | |b| | | | | | |
                        |r| | | | | | | |
                        | | | | | | | | |
                        | | | | |P| | | |
                        | | | | | | | | |
                        """,
                new ChessPosition(5, 2),
                new int[][]{
                        {6, 3}, {7, 4},
                        {4, 3}, {3, 4}, {2, 5},
                        // none
                        {6, 1},
                }
        );
    }


    @Test
    public void bishopBlocked() {
        TestUtilities.validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | |R| |P| |
                        | | | | | |B| | |
                        """,
                new ChessPosition(1, 6),
                new int[][]{}
        );
    }

}
