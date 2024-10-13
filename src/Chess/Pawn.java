package Chess;

public class Pawn extends ChessPiece {

    public Pawn(String color) {
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

        if (color.equals("White") && column == toColumn && (((Math.abs(line - toLine) == 2 ) && line == 1) || (Math.abs(line - toLine) == 1)) && line < toLine) {
            movePatternIsValid = true;
        } else if (color.equals("Black") && column == toColumn && (((Math.abs(line - toLine) == 2 ) && line == 6) || (Math.abs(line - toLine) == 1)) && line > toLine) {
            movePatternIsValid = true;
        }

        if (line != toLine || column != toColumn) {
            weAreMove = true;
        }

        return !outOfBoard && movePatternIsValid && weAreMove;
    }

    @Override
    public String getSymbol() {
        return "P";
    }

}
