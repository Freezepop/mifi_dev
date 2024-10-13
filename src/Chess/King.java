package Chess;

public class King extends ChessPiece {

    public King (String color) {
        super(color);
    }

    @Override
    public String getColor() {
        return super.getColor();
    }

    @Override
    public boolean canMoveToPosition(ChessBoard chessBoard, int line, int column, int toLine, int toColumn) {

        boolean outOfBoard = true;
        boolean movePatternIsValid = false;
        boolean weAreMove = false;

        if (toLine >= 0 && toColumn >= 0 && toLine <= 7 && toColumn <= 7) {
            outOfBoard = false;
        }

        if (Math.abs(line - toLine) == 1 || Math.abs(column - toColumn) == 1) {
            movePatternIsValid = true;
        }

        if (line != toLine || column != toColumn) {
            weAreMove = true;
        }

        if (!outOfBoard && movePatternIsValid && weAreMove) {
            this.check = false;
        }

        return !outOfBoard && movePatternIsValid && weAreMove;
    }

    @Override
    public String getSymbol() {
        return "K";
    }

    public boolean isUnderAttack(ChessBoard chessBoard, int line, int column) {
        for (int i = 0; i < 8; i++) {
            for (int k = 0; k < 8; k++) {
                ChessPiece piece = chessBoard.board[i][k];
                if (piece != null && !piece.getColor().equals(this.color)) {
                    if (piece.canMoveToPosition(chessBoard, i, k, line, column)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
