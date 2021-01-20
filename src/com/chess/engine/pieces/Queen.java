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

public class Queen extends Piece {

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {
        Move.UP_LEFT,
        Move.UP_RIGHT,
        Move.UP_STRAIGHT,
        Move.LEFT,
        Move.RIGHT,
        Move.DOWN_LEFT,
        Move.DOWN_RIGHT,
        Move.DOWN_STRAIGHT
    };

    public Queen(final Alliance pieceAlliance, final int piecePosition)
    {
        super(PieceType.QUEEN,piecePosition, pieceAlliance, true);
    }

    public Queen(final Alliance pieceAlliance,
                  final int piecePosition,
                  final boolean isFirstMove )
    {
        super(PieceType.QUEEN, piecePosition, pieceAlliance, isFirstMove);
    }


    @Override
    public Queen movePiece(Move move) {
        return new Queen(move.getPieceToBeMoved().getPieceAlliance(), move.getDestinationCoordinate());
    }
    @Override
    public String toString()
    {
        return PieceType.QUEEN.toString();
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

                        if (this.pieceAlliance != pieceAlliance)
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
