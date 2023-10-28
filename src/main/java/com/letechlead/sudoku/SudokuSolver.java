package com.letechlead.sudoku;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SudokuSolver {

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    private final int[][] grid;
    private final int gridSize;
    private final boolean isDiagonal;

    public SudokuSolver(int[][] grid, boolean isDiagonal) throws Exception {

        if (grid == null || grid.length < 4 || Arrays.stream(grid).anyMatch((col) ->
                col == null || col.length < 4 || col.length != grid.length)) {
            throw new Exception("Grid cannot be empty, less than 4 in dimensions, or of mismatching dimensions.");
        }

        this.isDiagonal = isDiagonal;
        this.gridSize = grid.length;
        this.grid = grid;
    }

    enum DIAGONAL {
        LEFT,
        RIGHT,
        NONE;
    }

    private void verifySudokuNumberValidity(int number) throws Exception {
        if (number < 1 || number > gridSize) throw new Exception("Not a valid Sudoku number.");
    }

    /**
     * @param index on x and/or y axis's
     * @throws Exception in case of invalid location.
     */
    private void verifyLocationValidity(int... index) throws Exception {
        if (index[0] < 1 || index[0] > gridSize || (index.length > 1 && (index[1] < 1 || index[1] > gridSize)))
            throw new Exception("Invalid row and/or column number(s).");
    }

    public boolean existInRow(int number, int rowNum) throws Exception {
        verifySudokuNumberValidity(number);
        verifyLocationValidity(rowNum);
        int rowMappedIndex = rowNum - 1;
        return Arrays.stream(grid).anyMatch((col) -> col[rowMappedIndex] == number);
    }

    public boolean existInColumn(int number, int columnNum) throws Exception {
        verifySudokuNumberValidity(number);
        verifyLocationValidity(columnNum);
        return Arrays.stream(grid[columnNum - 1]).anyMatch((sudokuNum) -> sudokuNum == number);
    }

    public boolean existInRowOrColumn(int number, int rowNum, int columnNum) throws Exception {
        return existInRow(number, rowNum) || existInColumn(number, columnNum);
    }

    /**
     * @param number    number to be checked.
     * @param diagonal  left or right, optional if row and column numbers are provided.
     * @param rowNum    row number, optional if diagonal type is provided.
     * @param columnNum column number, optional if diagonal type is provided.
     * @return boolean, whether provided number exists in a diagonal or not.
     * @throws Exception in case of unrecognized diagonal.
     */
    public boolean existInDiagonal(int number, DIAGONAL diagonal, Integer rowNum, Integer columnNum) throws Exception {
        verifySudokuNumberValidity(number);
        if (Stream.of(diagonal, rowNum, columnNum).allMatch(Objects::isNull))
            throw new Exception("Diagonal or location is mandatory.");
        if (diagonal == null) {
            if (rowNum == null || columnNum == null) throw new Exception("Invalid row or column number.");
            verifyLocationValidity(rowNum, columnNum);
            diagonal = whichDiagonal(rowNum, columnNum);
        }
        if (diagonal == DIAGONAL.NONE) return false;
        switch (diagonal) {
            case LEFT:
                return Stream.iterate(0, index -> ++index).limit(gridSize).anyMatch(index -> grid[index][index] == number);
            case RIGHT:
                return Stream.iterate(0, rowIndex -> ++rowIndex).limit(gridSize).anyMatch(rowIndex ->
                        grid[rowIndex][gridSize - rowIndex - 1] == number);
            default:
                throw new Exception("Unrecognized diagonal.");
        }
    }

    /**
     * @param rowNum    row number
     * @param columnNum column number
     * @return Type of diagonal, left diagonal starts from upper left side, and right diagonal starts from upper right side.
     */
    private DIAGONAL whichDiagonal(int rowNum, int columnNum) {
        if (rowNum == columnNum) return DIAGONAL.LEFT;
        //TODO review below line
        if (rowNum == (gridSize - columnNum + 1)) return DIAGONAL.RIGHT;
        return DIAGONAL.NONE;
    }

    public boolean existInBlock(int number, int blockWidth, int blockHeight, int blockNum) throws Exception {
        verifySudokuNumberValidity(number);
        //TODO
        return false;
    }

    public boolean isPossible(int number, int rowNum, int columnNum, int blockWidth, int blockHeight) throws Exception {
        int blockNum = (grid[0].length / blockWidth) + (columnNum % (grid.length / blockWidth));
        return !existInRowOrColumn(number, rowNum, columnNum) && !existInBlock(number, blockWidth, blockHeight, blockNum)
                && (isDiagonal && !existInDiagonal(number, null, rowNum, columnNum));
    }

    public void printGrid() {
        //TODO
    }

    public String getPrintableGrid() {
        //TODO
        return null;
    }

    public int[][] getGrid() {
        return grid;
    }
}
