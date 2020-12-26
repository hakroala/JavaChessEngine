package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves)
    {
        this.board = board;
        this.playerKing = establishKing();
        this.legalMoves = legalMoves;
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentMoves).isEmpty();

    }

    private King establishKing()
    {
        for(final Piece piece : getActionPieces())
        {
            if(piece.getPieceType().isKing())
            {
                return (King) piece;
            }
        }
        throw new RuntimeException("Should not reach here! Not a valid board !!" );
    }

    private static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves)
    {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move: moves)
        {
             if (piecePosition == move.getDestinationCoordinate())
             {
                 attackMoves.add(move);
             }
        }
        return ImmutableList.copyOf(attackMoves);
    }
    public boolean isMoveLegal(final Move move)
    {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck()
    {
        return this.isInCheck;
    }

    public boolean isInCheckMate()
    {
        return this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves()
    {
        for (final Move move: this.legalMoves){
            final MoveTransition transition = makeMoves(move);
            if(transition.getMoveStatus().isDone())
            {
                return true;
            }
        }
        return false;
    }

    public boolean isInStaleMate()
    {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled()
    {
        return false;
    }

    public MoveTransition makeMoves(final Move move)
    {
        return null;
    }
    public abstract Collection<Piece> getActionPieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();

}
