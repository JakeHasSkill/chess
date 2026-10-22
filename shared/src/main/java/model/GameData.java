package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData(int gameID, GameData gameData) {
        this(gameID, gameData.whiteUsername, gameData.blackUsername, gameData.gameName, gameData.game);
    }
}
