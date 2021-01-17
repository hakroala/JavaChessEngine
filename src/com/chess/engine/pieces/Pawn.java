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
    private final static int [] CANDIDATE_MOVE_COORDINATES = {8, 16, 7, 9};

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
        for(final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATES )
        {
            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);
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

            boolean isOneTileJump = currentCandidateOffset == 8 && !destinationOccupied;

            // A pawn can only jump 2 tiles (offset by 16) if this is the first time it moves
            // and it is a white pawn on the seventh row or a black pawn on the second row
            // and the destination is not occupied
            boolean isTwoTileJump = currentCandidateOffset == 16 &&
                    isFirstMove && !destinationOccupied &&
                    ((isSecondRow && isBlack) || (isSeventhRow && isWhite));

            // A pawn can only make a left capture (offset by 9) if
            // it's not a white pawn on the first column
            // and not a black pawn on the eighth column
            boolean isLeftCapture = currentCandidateOffset == 9 &&
                    !(isFirstColumn && isWhite) &&
                    !(isEighthColumn && isBlack);

            // A pawn can only make a right capture (offset by 7) if
            // it's not a white pawn on the eighth column
            // and not a black pawn on the first column
            boolean isRightCapture = currentCandidateOffset == 7 &&
                    !(isEighthColumn && isWhite) &&
                    !(isFirstColumn && isBlack);

            if (isOneTileJump)
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
            else if (isTwoTileJump)
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
        return new Pawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
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
