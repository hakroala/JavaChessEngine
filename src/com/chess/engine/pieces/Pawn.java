package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Pawn extends Piece
{
    private int[] candidateMoveCoordinates()
    {
        if (this.pieceAlliance == Alliance.BLACK)
        {
            // Black pawns can only move down
            return new int[] {
                Move.DOWN_STRAIGHT,
                Move.DOWN_STRAIGHT_TWO,
                Move.DOWN_RIGHT,
                Move.DOWN_LEFT
            };
        }
        else
        {
            // White pawns can only move up
            return new int[] {
                Move.UP_STRAIGHT,
                Move.UP_STRAIGHT_TWO,
                Move.UP_RIGHT,
                Move.UP_LEFT
            };
        }
    }

    public Pawn(final Alliance pieceAlliance, final int piecePosition)
    {
        super(PieceType.PAWN,piecePosition, pieceAlliance, true);
    }

    public Pawn(final Alliance pieceAlliance,
                  final int piecePosition,
                  final boolean isFirstMove )
    {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();
        for(final int currentCandidateOffset: candidateMoveCoordinates())
        {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                continue;
            }

            // Declare all boolean variables for checking move validity here
            // for cleaner code and easier debugging
            boolean destinationOccupied = board.getTile(candidateDestinationCoordinate).isTileOccupied();
            boolean isFirstMove = this.isFirstMove();
            boolean isBlack = this.getPieceAlliance().isBlack();
            boolean isWhite = this.getPieceAlliance().isWhite();
            boolean isSecondRow = BoardUtils.SECOND_ROW[this.piecePosition];
            boolean isSeventhRow = BoardUtils.SEVENTH_ROW[this.piecePosition];
            boolean isFirstColumn = BoardUtils.FIRST_COLUMN[this.piecePosition];
            boolean isEighthColumn = BoardUtils.EIGHT_COLUMN[this.piecePosition];
            boolean isPromotionMove = this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate);
            boolean canEnPassant = board.getEnPassantPawn() != null &&
                    this.pieceAlliance != board.getEnPassantPawn().getPieceAlliance() &&
                    (board.getEnPassantPawn().getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection())));

            boolean isOneRowJump = Move.isStraightOneRow(currentCandidateOffset) && !destinationOccupied;

            // A pawn can only jump 2 rows if this is the first time it moves
            // and it is a white pawn on the seventh row or a black pawn on the second row
            // and the destination is not occupied
            boolean isTwoRowJump = Move.isStraightTwoRows(currentCandidateOffset) &&
                    isFirstMove && !destinationOccupied &&
                    ((isSecondRow && isBlack) || (isSeventhRow && isWhite));

            // A pawn can only make a left capture if it can move left,
            // i.e. it's not on the first column
            boolean isLeftCapture = Move.isMoveToLeft(currentCandidateOffset) && !isFirstColumn;

            // A pawn can only make a right capture if it can move right,
            // i.e. it's not on the eighth column
            boolean isRightCapture = Move.isMoveToRight(currentCandidateOffset) && !isEighthColumn;

            if (isOneRowJump)
            {
                if (isPromotionMove)
                {
                    legalMoves.add(new Move.PawnPromotion(new Move.PawnMove(board,this,candidateDestinationCoordinate)));
                }
                else
                {
                    legalMoves.add(new Move.PawnJump(board,this,candidateDestinationCoordinate));
                }
            }
            else if (isTwoRowJump)
            {
                final int coordinateBehindDestination = this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                boolean tileBehindDestinationOccupied = board.getTile(coordinateBehindDestination).isTileOccupied();

                // The pawn cannot make the 2-tile jump if the tile behind the destination is occupied
                if (!tileBehindDestinationOccupied)
                {
                    legalMoves.add(new Move.PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            else if (isRightCapture || isLeftCapture)
            {
                if (destinationOccupied)
                {
                    final Piece pieceOnDestination = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnDestination.getPieceAlliance())
                    {
                        Move attackMove = new Move.PawnAttackMove(board,this,candidateDestinationCoordinate,pieceOnDestination);

                        if (isPromotionMove)
                        {
                            // Wrap the attack move in a promotion move to create a composite move
                            Move promotionMove = new Move.PawnPromotion(attackMove);
                            legalMoves.add(promotionMove);
                        }
                        else
                        {
                            legalMoves.add(attackMove);
                        }
                    }
                }
                else if (canEnPassant)
                {
                    final Piece pieceOnDestination = board.getEnPassantPawn();
                    Move attackMove = new Move.PawnEnPassantAttackMove(board,this, candidateDestinationCoordinate, pieceOnDestination);
                    legalMoves.add(attackMove);
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getPieceToBeMoved().getPieceAlliance(), move.getDestinationCoordinate());
    }

    @Override
    public String toString()
    {
        return PieceType.PAWN.toString();
    }

    public Piece getPromotionPiece()
    {
        return new Queen(this.pieceAlliance, this.piecePosition, false);
    }

}
