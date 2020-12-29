package com.chess.engine.board;

import com.chess.engine.pieces.Piece;
import com.chess.engine.board.Board.Builder;


import java.awt.image.BufferedImage;

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
        public Board execute()
        {
            final Builder builder = new Builder();

            for (final Piece piece: this.board.currentPlayer().getActivePieces())
            {
                if(!this.movedPiece.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.currentPlayer().getActivePieces())
            {
                builder.setPiece(piece);
            }

            builder.setPiece(this.movedPiece.movedPiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();

        }
    }

    public int getDestinationCoordinate()
    {
     return this.destinationCoordinate;
    }

    public Piece getMovedPiece()
    {
        return this.movedPiece;
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
