package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board,
                       final Collection<Move> whiteStandardLegalMoves,
                       final Collection<Move> blackStandardLegalMoves)

    {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance(){
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals, final Collection<Move> opponentsLegals)
    {
        final List<Move> kingCastles = new ArrayList<>();
        if(this.playerKing.isFirstMove() && !this.isInCheck())
        {
            int kingSideRookPosition = 63;
            int kingSideCastleKingDest = kingSideRookPosition - 1;
            int kingSideCastleRookDest = kingSideCastleKingDest - 1;
            // If none of the tiles between the king-side rook and the king is occupied
            if (!this.board.getTile(kingSideCastleKingDest).isTileOccupied() &&
                !this.board.getTile(kingSideCastleRookDest).isTileOccupied())
            {
                final Tile rookTile = this.board.getTile(kingSideRookPosition);
                final Piece rookTilePiece = rookTile.getPiece();
                // If the piece on the rook tile is really a rook
                // and this rook has not been moved
                if (rookTile.isTileOccupied() &&
                    rookTilePiece.getPieceType().isRook() &&
                    rookTilePiece.isFirstMove())
                {
                    // If the king's destination is not under attack
                    // and the rook destination is not under attack
                    if (Player.calculateAttacksOnTile(kingSideCastleKingDest,opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(kingSideCastleRookDest, opponentsLegals).isEmpty())
                    {
                        kingCastles.add(
                            new Move.KingSideCastleMove(
                                this.board,
                                this.playerKing,
                                kingSideCastleKingDest,
                                (Rook)rookTilePiece,
                                kingSideRookPosition,
                                kingSideCastleRookDest
                            )
                        );
                    }
                }
            }

            int queenSideRookPosition = 56;
            int queenSideCastleKnightPosition = queenSideRookPosition + 1;
            int queenSideCastleKingDest = queenSideCastleKnightPosition + 1;
            int queenSideCastleRookDest = queenSideCastleKingDest + 1;
            // If none of the tiles between the queen-side rook and the king is occupied
            if (!this.board.getTile(queenSideCastleKnightPosition).isTileOccupied() &&
                !this.board.getTile(queenSideCastleKingDest).isTileOccupied() &&
                !this.board.getTile(queenSideCastleRookDest).isTileOccupied())
            {
                final Tile rookTile = this.board.getTile(queenSideRookPosition);
                final Piece rookTilePiece = rookTile.getPiece();
                // If the piece on the rook tile is really a rook
                // and this rook has not been moved
                if (rookTile.isTileOccupied() &&
                    rookTilePiece.getPieceType().isRook() &&
                    rookTilePiece.isFirstMove())
                {
                    // If the king's destination is not under attack
                    // and the rook destination is not under attack
                    if (Player.calculateAttacksOnTile(queenSideCastleKingDest,opponentsLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(queenSideCastleRookDest, opponentsLegals).isEmpty())
                    {
                        kingCastles.add(
                            new Move.QueenSideCastleMove(
                                this.board,
                                this.playerKing,
                                queenSideCastleKingDest,
                                (Rook)rookTilePiece,
                                queenSideRookPosition,
                                queenSideCastleRookDest
                            )
                        );
                    }
                }
            }
        }


        return ImmutableList.copyOf(kingCastles);
    }

}
