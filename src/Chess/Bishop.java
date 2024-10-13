package Chess;

public class Bishop extends ChessPiece {

    public Bishop(String color) {
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

        if (Math.abs(line - toLine) == Math.abs(column - toColumn)) {
            movePatternIsValid = true;
        }

        if (line != toLine && column != toColumn) {
            weAreMove = true;
        }

        return !outOfBoard && movePatternIsValid && weAreMove;
    }

    @Override
    public String getSymbol() {
        return "B";
    }
}
