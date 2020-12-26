package com.chess.engine.board;

import com.chess.engine.pieces.Piece;

public abstract class Move
{
    final Board board;
    final Piece movedPiece;
    final int destinationCoordinate;

    private Move(final Board board,
         final Piece movedPiece,
         final int destinationCoordinate)
    {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
    }

    @Override
    public Board execute(){
        return null;
    };

    public static final class MajorMove extends Move
    {
        public MajorMove (final Board board, final Piece movedPiece, final int destinationCoordinate)
        {
            super(board,movedPiece,destinationCoordinate);
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    public int getDestinationCoordinate()
    {
     return this.destinationCoordinate;
    }
    public static final class AttackMove extends Move
    {
        final Piece attackPiece;
        public AttackMove (final Board board, final Piece movedPiece,final int destinationCoordinate, final Piece attackPiece)
        {
            super(board,movedPiece,destinationCoordinate);
            this.attackPiece = attackPiece;
        }

        @Override
        public Board execute() {
            return null;
        }
    }
}
