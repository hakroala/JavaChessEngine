package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Rook extends Piece
{
    private final static int [] CANDIDATE_MOVE_VECTOR_COORDINATES = {
        Move.UP_STRAIGHT,
        Move.DOWN_STRAIGHT,
        Move.LEFT,
        Move.RIGHT
    };

    public Rook( final Alliance pieceAlliance,final int piecePosition)
    {
        super(PieceType.ROOK,piecePosition, pieceAlliance, true);
    }

    public Rook(final Alliance pieceAlliance,
                final int piecePosition,
                final boolean isFirstMove )
    {
        super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);
    }


    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getPieceToBeMoved().getPieceAlliance(), move.getDestinationCoordinate());
    }
    @Override
    public String toString()
    {
        return PieceType.ROOK.toString();
    }
    @Override
    public Collection<Move> calculateLegalMoves(final Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int candidateCoordinateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES)
        {
            int candidateDestinationCoordinate = this.piecePosition;
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                if (Move.isFirstColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset) ||
                    Move.isEighthColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset))
                {
                    break;
                }

                candidateDestinationCoordinate += candidateCoordinateOffset;
                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
                {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if (!candidateDestinationTile.isTileOccupied())
                    {
                        legalMoves.add(new Move.MajorMove(board,this, candidateDestinationCoordinate));
                    }
                    else
                    {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                        if(this.pieceAlliance != pieceAlliance)
                        {
                            legalMoves.add(new Move.MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }
}
