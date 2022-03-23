package com.lifegame.gameoflifejfx;

public class Grid {
    Cell[][] cellGrid;

    public Grid(int rows, int cols) {
        cellGrid = new Cell[rows][cols];

        // generates a 2D array of dead cells
        for (int i = 0; i < cellGrid.length; i++) {
            for (int j = 0; j < cellGrid[0].length; j++) {
                Cell temp = new Cell();
                cellGrid[i][j] = temp;
            }
        }

        randomize();

        for (int i = 0; i < cellGrid.length; i++) {
            for (int j = 0; j < cellGrid[0].length; j++) {
                if (i % 2 == 0)
                    cellGrid[i][j].setRGB(new short[]{0, 255, 0});
                else
                    cellGrid[i][j].setRGB(new short[]{0, 0, 255});
            }
        }
    }

    public void randomize() {
        for (Cell[] row : cellGrid) {
            for (Cell temp : row) {
                double chance = Math.random();

                // probability of a cell being alive
                temp.setLifeState(chance < .25);
            }
        }
    }

    /*
     *  1. Any live cell with fewer than two live neighbours dies, as if by underpopulation.
     *  2. Any live cell with two or three live neighbours lives on to the next generation.
     *  3. Any live cell with more than three live neighbours dies, as if by overpopulation.
     *  4. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
     */
    public void update() {
        // I need to set the # of neighbors first
        // and then after I've updated every cell's #
        // I can kill or spawn cells

        for (int i = 0; i < cellGrid.length; i++) {
            for (int j = 0; j < cellGrid[0].length; j++) {
                Cell[] neighbors = getNeighbors(i, j);
                int numNeighbors = 0;

                for (Cell neighbor : neighbors) {
                    if (neighbor != null)
                        numNeighbors++;
                }

                cellGrid[i][j].setNumOfNeighbors(numNeighbors);

                // the "offspring" of cells average the RGB values of their "parents"
                if (cellGrid[i][j].getNumOfNeighbors() == 3 && !cellGrid[i][j].getLifeState()) {
                    short avgR = (short) ((neighbors[0].getRGB()[0] + neighbors[1].getRGB()[0] + neighbors[2].getRGB()[0]) / 3);
                    short avgG = (short) ((neighbors[0].getRGB()[1] + neighbors[1].getRGB()[1] + neighbors[2].getRGB()[1]) / 3);
                    short avgB = (short) ((neighbors[0].getRGB()[2] + neighbors[1].getRGB()[2] + neighbors[2].getRGB()[2]) / 3);

                    cellGrid[i][j].setRGB(new short[]{avgR, avgG, avgB});
                }
            }
        }

        // the change of state has to be a separate loop
        // because if I do it all in the same loop, it'll mess up
        // the # of neighbors for the remaining cells
        for (int i = 0; i < cellGrid.length; i++) {
            for (int j = 0; j < cellGrid[0].length; j++) {
                Cell cell = cellGrid[i][j];

                if (cell.getLifeState()) {    // if the cell is alive
                    if (cell.getNumOfNeighbors() < 2) {     // underpopulation
                        cell.setLifeState(false);
                    } else if (cell.getNumOfNeighbors() > 3) {   //overpopulation
                        cell.setLifeState(false);
                    }
                } else {   // if the cell is dead
                    if (cell.getNumOfNeighbors() == 3) {   // reproduction
                        cell.setLifeState(true);
                    }
                }
            }
        }
    }

    // the method needs to return an array of neighbors, in order to make the "DNA" feature work
    public Cell[] getNeighbors(int row, int col) {

        //ArrayList<Cell> neighbors = new ArrayList<>();

        Cell[] neighbors = new Cell[8];     // ArrayList is computationally expensive.
        // Considering I have to use this method for
        // every single cell I have to over-optimize it.

        int i = 0;                          // using i to make so that we only fill the necessary indices

        if (row != 0) {
            //directly above
            if (cellGrid[row - 1][col].getLifeState()) {
                neighbors[i] = cellGrid[row - 1][col];
                i++;
            }

            if (col != 0) {
                //diagonal top left
                if (cellGrid[row - 1][col - 1].getLifeState()) {
                    neighbors[i] = cellGrid[row - 1][col - 1];
                    i++;
                }
                //left
                if (cellGrid[row][col - 1].getLifeState()) {
                    neighbors[i] = cellGrid[row][col - 1];
                    i++;
                }
            }

            if (col != cellGrid[0].length - 1) {
                //diagonal top right
                if (cellGrid[row - 1][col + 1].getLifeState()) {
                    neighbors[i] = cellGrid[row - 1][col + 1];
                    i++;
                }
                //right
                if (cellGrid[row][col + 1].getLifeState()) {
                    neighbors[i] = cellGrid[row][col + 1];
                    i++;
                }
            }
        }


        if (row != cellGrid.length - 1) {
            //directly below
            if (cellGrid[row + 1][col].getLifeState()) {
                neighbors[i] = cellGrid[row + 1][col];
                i++;
            }

            if (col != 0) {
                //diagonal bottom left
                if (cellGrid[row + 1][col - 1].getLifeState()) {
                    neighbors[i] = cellGrid[row + 1][col - 1];
                    i++;
                }
            }

            if (col != cellGrid[0].length - 1) {
                //diagonal bottom left
                if (cellGrid[row + 1][col + 1].getLifeState()) {
                    neighbors[i] = cellGrid[row + 1][col + 1];
                }
            }
        }
        return neighbors;
    }

    public Cell[][] getCellGrid() {
        return cellGrid;
    }
}
