package Chess;

public class Horse extends ChessPiece {

    public Horse (String color) {
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

        if ((Math.abs(line - toLine) == 2 && Math.abs(column - toColumn) == 1) || (Math.abs(line - toLine) == 1 && Math.abs(column - toColumn) == 2)) {
            movePatternIsValid = true;
        }

        if (line != toLine && column != toColumn) {
            weAreMove = true;
        }

        return !outOfBoard && movePatternIsValid && weAreMove;
    }

    @Override
    public String getSymbol() {
        return "H";
    }
}

