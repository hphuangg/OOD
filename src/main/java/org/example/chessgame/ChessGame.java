package org.example.chessgame;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

public class ChessGame {
    Player[] players;
    int currentPlayer; // index of player (0, 1)
    Board board;
    List<Move> moves;
    GameState gameState;

    public boolean makeMove(Player player, Square from, Square to) {

        // get piece on the square
        Piece piece = from.getPiece();
        if (!player.getColor().equals(piece.getColor())) {
            System.out.println("piece is not player's");
            return false;
        }
        // validate move
        if (!piece.canMove(board, from, to)) {
            System.out.println("this movement is disallowed.");
            return false;
        }

        Move move = new Move(from, to, piece, null);
        piece.move(to);

        // validate kill
        // move.setPieceKilled(pieceKilled);
        moves.add(move);

        // validate gameState and update
        swapPlayer();
        return true;
    }


    public void swapPlayer() {
        currentPlayer = currentPlayer == 0 ? 1 : 0;
    }
}

enum GameState {
    IN_PROGRESS,
    WHITE_WIN,
    BLACK_WIN,
    STALEMATE
    // , and other states.
}

@Data
class Square {
    int row;
    int col;
    Piece piece;
}

class Board {
    Square[][] squares;
}

@Data
abstract class Piece {
    Square position;
    abstract boolean canMove(Board board, Square from, Square to);
    void move(Square to) {
        position = to;
    }
    PieceColor color;
}

enum PieceColor {
    BLACK, WHITE
}

class King extends Piece {

    @Override
    boolean canMove(Board board, Square from, Square to) {
        // king's movement rules
        return false;
    }
}

// same as Queue, other pieces.....

@Data
class Player {
    int id;
    PieceColor color;
}

@Data
@AllArgsConstructor
class Move {
    Square from;
    Square to;
    Piece pieceMoved;
    Piece pieceKilled;
}