package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private ChessMove lastMove;
    private TeamColor whoseTurn = TeamColor.WHITE;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return whoseTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.whoseTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public static TeamColor otherTeam(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) return TeamColor.BLACK;
        else return TeamColor.WHITE;
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        TeamColor color = piece.getTeamColor();
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            if (!getMovesMinusStalemate(color, board).contains(move)) {
                continue;
            }
            if (isInCheck(color)) {
                if (!getCheckAvoidingMoves(color).contains(move)) {
                    continue;
                }
            }
            ChessBoard nextMoveBoard = testMove(move, board);
            if (isInCheck(color, nextMoveBoard)) {
                continue;
            }
            moves.add(move);
        }
        return moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No piece to move at that location: " + move);
        }
        TeamColor color = board.getPiece(move.getStartPosition()).getTeamColor();
        if (color != this.whoseTurn) {
            throw new InvalidMoveException("It's not " + color.name() + " player's turn!");
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Not a valid move: " + move);
        }
        board.movePiece(move.getStartPosition(), move.getEndPosition());
        whoseTurn = otherTeam(whoseTurn);
    }

    /**
     * Returns a copy of the board with the given move applied
     * Helper function for game logic
     * Does not check to see if a move is valid
     */
    public static ChessBoard testMove(ChessMove move, ChessBoard board) {
        if (board.getPiece(move.getStartPosition()) == null) throw new RuntimeException("testMove got a move for a piece that is null");
        ChessBoard newBoard = new ChessBoard(board);
        newBoard.movePiece(move.getStartPosition(), move.getEndPosition());
        return newBoard;
    }

    /**
     * Gets all possible moves a player could make, not considering check logic
     */
    private static ArrayList<ChessMove> getTeamMoves(TeamColor teamColor, ChessBoard board) {
        if (board == null) return new ArrayList<>();
        ArrayList<ChessMove> allMoves = new ArrayList<>();
        for (int col = 1; col <= 8; col++) {
            for (int row = 1; row <= 8; row++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null) continue;
                if (piece.getTeamColor() != teamColor) continue;
                allMoves.addAll(piece.pieceMoves(board, position));
            }
        }
        return allMoves;
    }

    /**
     * @param teamColor which team might be in check
     * @return a list of opposing moves that are causing check
     */
    private static ArrayList<ChessMove> getInCheckMoves(TeamColor teamColor, ChessBoard board) {
        ArrayList<ChessMove> allMoves = getTeamMoves(otherTeam(teamColor), board);

        ArrayList<ChessMove> inCheckMoves = new ArrayList<>();
        for (ChessMove move : allMoves) {
            if (board.getPiece(move.getEndPosition()) == null) continue;
            if (board.getPiece(move.getEndPosition()).getPieceType().equals(ChessPiece.PieceType.KING)) {
                inCheckMoves.add(move);
            }
        }

        return inCheckMoves;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return !getInCheckMoves(teamColor, this.board).isEmpty();
    }
    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        return !getInCheckMoves(teamColor, board).isEmpty();
    }

    /**
     * @param teamColor which team is trying to get out of check
     * @return list of moves that gets the team out of check
     */
    private ArrayList<ChessMove> getCheckAvoidingMoves(TeamColor teamColor) {
        ArrayList<ChessMove> possibleMoves = getTeamMoves(teamColor, this.board);
        ArrayList<ChessMove> avoidingMoves = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            ChessBoard newBoard = testMove(move, board);
            if (isInCheck(teamColor, newBoard)) continue;
            avoidingMoves.add(move);
        }
        return avoidingMoves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return getCheckAvoidingMoves(teamColor).isEmpty();
    }

    /**
     * Returns a list of moves that doesn't get the team in check
     */
    private ArrayList<ChessMove> getMovesMinusStalemate(TeamColor teamColor, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (ChessMove move : getTeamMoves(teamColor, board)) {
            ChessBoard nextMoveBoard = testMove(move, board);
            if (isInCheck(teamColor, nextMoveBoard)) continue;
            moves.add(move);
        }
        return moves;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;
        return getMovesMinusStalemate(teamColor, this.board).isEmpty();
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return new ChessBoard(board);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (this == object) return true;
        if (object.getClass() != this.getClass()) return false;
        ChessGame chessGame = (ChessGame) object;
        return Objects.equals(board, chessGame.board) && Objects.equals(lastMove, chessGame.lastMove) && whoseTurn == chessGame.whoseTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, lastMove, whoseTurn);
    }
}
