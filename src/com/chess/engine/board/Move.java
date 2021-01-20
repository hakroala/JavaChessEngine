package com.chess.engine.board;

import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.Rook;

public abstract class Move {
    protected final Board board;
    protected final Piece pieceToBeMoved;
    protected final int destinationCoordinate;
    protected final boolean isFirstMove;

    private static final Move NULL_MOVE = new NullMove();

    // Common move offsets
    // -16: Up 2 rows straight (only for pawns)
    public static final int UP_STRAIGHT_TWO = -16;
    // -9: Up one row diagonally left
    public static final int UP_LEFT = -9;
    // -8: Up one row straight
    public static final int UP_STRAIGHT = -8;
    // -7: Up one row diagonally right
    public static final int UP_RIGHT = -7;
    // -1: Same row one tile left
    public static final int LEFT = -1;
    // 1: Same row one tile right
    public static final int RIGHT = 1;
    // 7: Down one row diagonally left
    public static final int DOWN_LEFT = 7;
    // 8: Down one row straight
    public static final int DOWN_STRAIGHT = 8;
    // 9: Down one row diagonally right
    public static final int DOWN_RIGHT = 9;
    // 16: Down 2 rows straight (only for pawns)
    public static final int DOWN_STRAIGHT_TWO = 16;

    public static boolean isMoveToRight(int offset)
    {
        return offset == UP_RIGHT ||
            offset == DOWN_RIGHT ||
            offset == RIGHT;
    }

    public static boolean isMoveToLeft(int offset)
    {
        return offset == UP_LEFT ||
                offset == DOWN_LEFT ||
                offset == LEFT;
    }

    public static boolean isStraightOneRow(int offset)
    {
        return offset == UP_STRAIGHT || offset == DOWN_STRAIGHT;
    }

    public static boolean isStraightTwoRows(int offset)
    {
        return offset == UP_STRAIGHT_TWO || offset == DOWN_STRAIGHT_TWO;
    }

    public static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.FIRST_COLUMN[currentPosition] && isMoveToLeft(candidateOffset);
    }

    public static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset)
    {
        return BoardUtils.EIGHT_COLUMN[currentPosition] && isMoveToRight(candidateOffset);
    }

    private Move(final Board board,
                 final Piece pieceToBeMoved,
                 final int destinationCoordinate) {
        this.board = board;
        this.pieceToBeMoved = pieceToBeMoved;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = pieceToBeMoved.isFirstMove();
    }

    private Move(final Board board,
                 final int destinationCoordinate) {
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.pieceToBeMoved = null;
        this.isFirstMove = false;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.pieceToBeMoved.hashCode();
        result = prime * result + this.pieceToBeMoved.getPiecePosition();

        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        return getCurrentCoordinate() == otherMove.getCurrentCoordinate() &&
                getDestinationCoordinate() == otherMove.getDestinationCoordinate() &&
                getPieceToBeMoved().equals(otherMove.getPieceToBeMoved());
    }

    public Board getBoard(){
        return this.board;
    }

    public int getCurrentCoordinate()
    {
        return this.getPieceToBeMoved().getPiecePosition();
    }

    public int getDestinationCoordinate()
    {
        return this.destinationCoordinate;
    }

    public Piece getPieceToBeMoved()
    {
        return this.pieceToBeMoved;
    }

    public boolean isAttack()
    {
        return false;
    }

    public boolean isCastlingMove()
    {
        return false;
    }

    public Piece getAttackedPiece()
    {
        return null;
    }

    public Board execute()
    {
        final Builder builder = new Builder();

        for (final Piece piece: this.board.currentPlayer().getActivePieces())
        {
            if(!this.pieceToBeMoved.equals(piece))
            {
                builder.setPiece(piece);
            }
        }

        for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces())
        {
            builder.setPiece(piece);
        }

        builder.setPiece(this.pieceToBeMoved.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
        return builder.build();
    }

    public static final class MajorMove extends Move
    {
        public MajorMove (final Board board, final Piece movedPiece, final int destinationCoordinate)
        {
            super(board,movedPiece,destinationCoordinate);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return pieceToBeMoved.getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static  class AttackMove extends Move
    {
        final Piece attackedPiece;

        public AttackMove (final Board board,
                           final Piece movedPiece,
                           final int destinationCoordinate,
                           final Piece attackedPiece)
        {
            super(board,movedPiece,destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode()
        {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other)
        {
            if(this == other)
            {
                return true;
            }
            if(!(other instanceof AttackMove))
            {
                return false;
            }
            final AttackMove otherAttackMove = (AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();

            for (final Piece piece: this.board.currentPlayer().getActivePieces())
            {
                if (!this.pieceToBeMoved.equals(piece) &&
                    !this.getAttackedPiece().equals(piece))
                {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(piece);
            }

            builder.setPiece(this.pieceToBeMoved.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack ()
        {
            return true;
        }

        @Override
        public Piece getAttackedPiece()
        {
            return this.attackedPiece;
        }
    }

    public static class MajorAttackMove extends AttackMove {
        public MajorAttackMove (final Board board,
                                final Piece pieceMoved,
                                final int destinationCoordinate,
                                final Piece pieceAttacked)
        {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        @Override
        public boolean equals (final Object other)
        {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return pieceToBeMoved.getPieceType() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static final class PawnMove extends Move
    {
        public PawnMove (final Board board, final Piece movedPiece, final int destinationCoordinate)
        {
            super(board,movedPiece,destinationCoordinate);
        }

        @Override
        public boolean equals ( final  Object other)
        {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static class PawnAttackMove extends AttackMove
    {
        public PawnAttackMove (final Board board,
                               final Piece movedPiece,
                               final int destinationCoordinate,
                               final Piece attackedPiece)
        {
            super(board,movedPiece,destinationCoordinate,attackedPiece);
        }

        @Override
        public boolean equals (final Object other)
        {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return BoardUtils.getPositionAtCoordinate(this.pieceToBeMoved.getPiecePosition()).substring(0,1) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }
    public static class PawnPromotion extends Move
    {
        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getPieceToBeMoved(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getPieceToBeMoved();
        }
        @Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals (final Object other)
        {
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }

        @Override
        public Board execute()
        {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();
            for (final Piece piece: pawnMovedBoard.currentPlayer().getActivePieces())
            {
                if(!this.promotedPawn.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece: pawnMovedBoard.currentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(piece);
            }

            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
            return builder.build();
        }

        @Override
        public boolean isAttack()
        {
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece()
        {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString()
        {
            return "";
        }
    }

    public static final class PawnJump extends Move
    {
        public PawnJump (final Board board, final Piece movedPiece, final int destinationCoordinate)
        {
            super(board,movedPiece,destinationCoordinate);
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();
            for (final  Piece piece : this.board.currentPlayer().getActivePieces())
            {
                if(!this.pieceToBeMoved.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(piece);
            }
            
            final Pawn movedPawn = (Pawn) this.pieceToBeMoved.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class PawnEnPassantAttackMove extends PawnAttackMove
    {
        public PawnEnPassantAttackMove (final Board board,
                                        final Piece movedPiece,
                                        final int destinationCoordinate,
                                        final Piece attackedPiece)
        {
            super(board,movedPiece,destinationCoordinate,attackedPiece);
        }

        @Override
        public boolean equals (final Object other)
        {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();
            for (final Piece piece: this.board.currentPlayer().getActivePieces())
            {
                if( !this.pieceToBeMoved.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces())
            {
                if(!piece.equals(this.getAttackedPiece()))
                {
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.pieceToBeMoved.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

    }

    static abstract class CastleMove extends Move
    {
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        public CastleMove (final Board board,
                           final Piece movedPiece,
                           final int destinationCoordinate,
                           final Rook castleRook,
                           final int castleRookStart,
                           final int castleRookDestination)
        {
            super(board,movedPiece,destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook()
        {
            return this.castleRook;
        }

        @Override
        public boolean isCastlingMove()
        {
            return true;
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();
            for(final  Piece piece : this.board.currentPlayer().getActivePieces())
            {
                if(!this.pieceToBeMoved.equals(piece) && !this.castleRook.equals(piece))
                {
                    builder.setPiece(piece);
                }
            }

            for(final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces())
            {
                builder.setPiece(piece);
            }
            builder.setPiece(this.pieceToBeMoved.movePiece(this));

            builder.setPiece(new Rook(this.castleRook.getPieceAlliance(), this.castleRookDestination));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
            return builder.build();
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals (final Object other)
        {
            if(this == other)
            {
                return true;
            }

            if (!(other instanceof CastleMove))
            {
                return false;
            }

            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }

    public static final class KingSideCastleMove extends CastleMove
    {
        public KingSideCastleMove (final Board board,
                                   final Piece movedPiece,
                                   final int destinationCoordinate,
                                   final Rook castleRook,
                                   final int castleRookStart,
                                   final int castleRookDestination
        )
        {
            super(board,movedPiece,destinationCoordinate, castleRook, castleRookStart,castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
                return this == other || other instanceof KingSideCastleMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return "O-O";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove
    {
        public QueenSideCastleMove (final Board board,
                                    final Piece movedPiece,
                                    final int destinationCoordinate,
                                    final Rook castleRook,
                                    final int castleRookStart,
                                    final int castleRookDestination)
        {
            super(board,movedPiece,destinationCoordinate, castleRook, castleRookStart,castleRookDestination);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return "O-O-O";
        }
    }

    public static class NullMove extends Move
    {
        public NullMove ()
        {
            super(null,65);
        }

        @Override
        public Board execute()
        {
            throw new RuntimeException("cannot execute the null move");
        }

        @Override
        public int getCurrentCoordinate()
        {
            return -1;
        }
    }

    public static class MoveFactory
    {
        private MoveFactory()
        {
            throw new RuntimeException("Not instantiable");
        }

        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate)
        {
            for (final Move move: board.getAllLegalMoves())
            {
                if(move.getCurrentCoordinate()== currentCoordinate &&
                move.getDestinationCoordinate() == destinationCoordinate)
                {return move;}
            }
            return NULL_MOVE;
        }

    }
}
