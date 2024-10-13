package Chess;

public class Rook extends ChessPiece {

    public Rook (String color) {
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

        if ((Math.abs(line - toLine) == 0 && Math.abs(column - toColumn) != 0) || (Math.abs(line - toLine) != 0 && Math.abs(column - toColumn) == 0)) {
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
        return "R";
    }
}
