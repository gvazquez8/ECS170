public class alphabeta_917307919 extends AIModule{

    int max;
    int min;
    int bestMoveSoFar;
    final int maxDepth = 10;
    boolean setup = false;
    int columns[];

    @Override
    public void getNextMove(GameStateModule game) {
        // we want to evaluate columns going from middle to outer columns so do first time setup.
        if (!setup) {
            columns = new int[game.getWidth()];
            int offset = columns.length % 2 - 1;
            for (int k = 0, i = columns.length/2, j = i+offset; k < columns.length-1; i--,j++, k++) {
                if (i == j) { columns[k] = i; }
                else { columns[k] = i; columns[k+1] = j; k++; }
            }
            max = game.getActivePlayer();
            min = (max == 1) ? 2 : 1;
            setup = true;
        }
        this.alphabeta(game, 0, true, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        this.chosenMove = this.bestMoveSoFar; // assign it to chosenMove to confirm action.
    }

    public double alphabeta(GameStateModule game, int currentDepth, boolean isMaxPlayer, double alpha, double beta) {
        currentDepth++;
        if (this.terminate) {
            return 0;
        }

        if (currentDepth == maxDepth) {
            return evaluate(game);
        }

        if (isMaxPlayer) {
            double maxValueSoFar = Double.NEGATIVE_INFINITY;
            for (int col = 0; col < game.getWidth(); col++) {
                if (game.canMakeMove(col)) {
                    game.makeMove(col);
                    double temp = Math.max(maxValueSoFar, alphabeta(game, currentDepth, false, alpha, beta));
                    game.unMakeMove();
                    if (temp > maxValueSoFar) {
                        maxValueSoFar = temp;
                        if (currentDepth == 1) { this.bestMoveSoFar = col; }
                        alpha = Math.max(alpha, maxValueSoFar);
                    }
                    if (maxValueSoFar >= beta) { return maxValueSoFar; }
                }
            }
            return maxValueSoFar;
        }
        else {
            double minValueSoFar = Double.POSITIVE_INFINITY;
            for (int col = 0; col < game.getWidth(); col++) {
                if (game.canMakeMove(col)) {
                    game.makeMove(col);
                    double temp = Math.min(minValueSoFar, alphabeta(game, currentDepth, true, alpha, beta));
                    game.unMakeMove();
                    if (temp < minValueSoFar) {
                        minValueSoFar = temp;
                        beta = Math.min(beta, minValueSoFar);
                    }
                    if (minValueSoFar <= alpha) { return minValueSoFar; }
                }
            }
            return minValueSoFar;
        }
    }

    public double evaluate(GameStateModule game) {
        if (game.isGameOver()) { // if the game is over, we either won or lost.
            return game.getWinner() == this.max ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        if (opponentWillWin(game)) { return -1; }
        double maxScore = countPotentialHorizontalWins(game) + countPotentialVerticalWins(game);
        maxScore += countPotentialDiagonalWins(game);
        double minScore = countPotentialHorizontalLosses(game) + countPotentialVerticalLosses(game);
        minScore += countPotentialDiagonalLosses(game);
        return maxScore - minScore;
    }

    public boolean opponentWillWin(GameStateModule game) {
        for (int col : columns) {
            if (game.canMakeMove(col)) {
                game.makeMove(col);
                if (game.isGameOver()) {
                    game.unMakeMove();
                    return true;
                }
                game.unMakeMove();
            }
        }
        return false;
    }

    public int countPotentialHorizontalWins(GameStateModule game) {
        int connectTwoWins = 0, connectThreeWins = 0;
        int tile0,tile1,tile2,tile3;
        for (int row = 0; row < game.getHeight(); row++) {
            for (int col = 0; col < game.getWidth()-3; col++) {
                tile0 = game.getAt(col, row);
                if (tile0 == max) {
                    tile1 = game.getAt(col+1,row);
                    tile2 = game.getAt(col+2,row);
                    tile3 = game.getAt(col+3,row);
                    int countBlanks = 0;
                    int countMax = 1;
                    countBlanks += (tile1 == 0 ? 1 : 0) + (tile2 == 0 ? 1 : 0) + (tile3 == 0 ? 1 : 0);
                    countMax += (tile1 == max ? 1 : 0) + (tile2 == max ? 1 : 0) + (tile3 == max ? 1 : 0);
                    if (countMax == 3 && countBlanks == 1) { connectThreeWins++; }
                    if (countMax == 2 && countBlanks == 2) { connectTwoWins++; }
                }
            }
        }
        return 10*connectThreeWins + 2*connectTwoWins;
    }

    public int countPotentialHorizontalLosses(GameStateModule game) {
        int connectTwoWins = 0, connectThreeWins = 0;
        int tile0,tile1,tile2,tile3;
        for (int row = 0; row < game.getHeight(); row++) {
            for (int col = 0; col < game.getWidth()-3; col++) {
                tile0 = game.getAt(col, row);
                if (tile0 == min) {
                    tile1 = game.getAt(col+1,row);
                    tile2 = game.getAt(col+2,row);
                    tile3 = game.getAt(col+3,row);
                    int countBlanks = 0;
                    int countMax = 1;
                    countBlanks += (tile1 == 0 ? 1 : 0) + (tile2 == 0 ? 1 : 0) + (tile3 == 0 ? 1 : 0);
                    countMax += (tile1 == min ? 1 : 0) + (tile2 == min ? 1 : 0) + (tile3 == min ? 1 : 0);
                    if (countMax == 3 && countBlanks == 1) { connectThreeWins++; }
                    if (countMax == 2 && countBlanks == 2) { connectTwoWins++; }
                }
            }
        }
        return 10*connectThreeWins + 2*connectTwoWins;
    }

    public int countPotentialVerticalWins(GameStateModule game) {
        int connectTwoWins = 0, connectThreeWins = 0;
        int tile0,tile1,tile2;
        for (int col : columns) {
            if (game.getHeightAt(col) != game.getHeight() && game.getHeightAt(col) != 0) {
                int latestTileHeight = game.getHeightAt(col)-1;
                tile0 = game.getAt(col, latestTileHeight);

                if (tile0 == max) {
                    tile1 = game.getAt(col, latestTileHeight-1);
                    tile2 = game.getAt(col, latestTileHeight-2);
                    if (tile0 == tile1 && tile0 == tile2) { connectThreeWins++; }
                    else if (tile0 == tile1) { connectTwoWins++; }
                }
            }
        }
        return 10*connectThreeWins + 2*connectTwoWins;
    }

    public int countPotentialVerticalLosses(GameStateModule game) {
        int connectTwoWins = 0, connectThreeWins = 0;
        int tile0,tile1,tile2;
        for (int col : columns) {
            if (game.getHeightAt(col) != game.getHeight() && game.getHeightAt(col) != 0) {
                int latestTileHeight = game.getHeightAt(col)-1;
                tile0 = game.getAt(col, latestTileHeight);

                if (tile0 == min) {
                    tile1 = game.getAt(col, latestTileHeight-1);
                    tile2 = game.getAt(col, latestTileHeight-2);
                    if (tile0 == tile1 && tile0 == tile2) { connectThreeWins++; }
                    else if (tile0 == tile1) { connectTwoWins++; }
                }
            }
        }
        return 10*connectThreeWins + 2*connectTwoWins;
    }

    public int countPotentialDiagonalWins(GameStateModule game) {
        int connectTwoWins = 0, connectThreeWins = 0;
        int middleColumn = game.getWidth() / 2;
        // left diag
        int tile0,tile1,tile2,tile3;
        // right diag
        int tile4,tile5,tile6,tile7;
        for (int row = 0; row < game.getHeightAt(middleColumn); row++) {
            tile0 = game.getAt(middleColumn, row);
            if (tile0 == max) {
                for (int dx = -3; dx <= 0; dx++) {
                    int countMax = 0;
                    int countBlanks = 0;
                    // check bottom left
                    tile0 = game.getAt(middleColumn+dx, row+dx);
                    tile1 = game.getAt(middleColumn+dx+1, row+dx+1);
                    tile2 = game.getAt(middleColumn+dx+2, row+dx+2);
                    tile3 = game.getAt(middleColumn+dx+3, row+dx+3);
                    if ((row+dx >= 0) && (row+dx+3 < game.getHeight())) {
                        countBlanks = (tile0==0?1:0)+(tile1==0?1:0)+(tile2 ==0?1:0)+(tile3==0?1:0);
                        countMax = (tile0==max?1:0)+(tile1==max?1:0)+(tile2 ==max?1:0)+(tile3==max?1:0);
                    }
                    if (countBlanks == 2 && countMax == 2) { connectTwoWins++; }
                    if (countMax == 3 && countBlanks == 1) { connectThreeWins++; }
                    countBlanks = 0;
                    countMax = 0;
                    // check top left
                    tile4 = game.getAt(middleColumn+dx, row-dx);
                    tile5 = game.getAt(middleColumn+dx+1, row-dx-1);
                    tile6 = game.getAt(middleColumn+dx+2, row-dx-2);
                    tile7 = game.getAt(middleColumn+dx+3, row-dx-3);
                    if ((row-dx < game.getHeight()) && (row-dx-3 >= 0)) {
                        countBlanks = (tile4==0?1:0)+(tile5==0?1:0)+(tile6 ==0?1:0)+(tile7==0?1:0);
                        countMax = (tile4==max?1:0)+(tile5==max?1:0)+(tile6 ==max?1:0)+(tile7==max?1:0);
                    }
                    if (countBlanks == 2 && countMax == 2) { connectTwoWins++; }
                    if (countMax == 3 && countBlanks == 1) { connectThreeWins++; }
                }
            }
        }
        return 10*connectThreeWins+2*connectTwoWins;
    }

    public int countPotentialDiagonalLosses(GameStateModule game) {
        int connectTwoWins = 0, connectThreeWins = 0;
        int middleColumn = game.getWidth() / 2;
        // left diag
        int tile0,tile1,tile2,tile3;
        // right diag
        int tile4,tile5,tile6,tile7;
        for (int row = 0; row < game.getHeightAt(middleColumn); row++) {
            tile0 = game.getAt(middleColumn, row);
            if (tile0 == min) {
                for (int dx = -3; dx <= 0; dx++) {
                    int countMax = 0;
                    int countBlanks = 0;
                    // check bottom left
                    tile0 = game.getAt(middleColumn+dx, row+dx);
                    tile1 = game.getAt(middleColumn+dx+1, row+dx+1);
                    tile2 = game.getAt(middleColumn+dx+2, row+dx+2);
                    tile3 = game.getAt(middleColumn+dx+3, row+dx+3);
                    if ((row+dx >= 0) && (row+dx+3 < game.getHeight())) {
                        countBlanks = (tile0==0?1:0)+(tile1==0?1:0)+(tile2 ==0?1:0)+(tile3==0?1:0);
                        countMax = (tile0==min?1:0)+(tile1==min?1:0)+(tile2 ==min?1:0)+(tile3==min?1:0);
                    }
                    if (countBlanks == 2 && countMax == 2) { connectTwoWins++; }
                    if (countMax == 3 && countBlanks == 1) { connectThreeWins++; }
                    countBlanks = 0;
                    countMax = 0;
                    // check top left
                    tile4 = game.getAt(middleColumn+dx, row-dx);
                    tile5 = game.getAt(middleColumn+dx+1, row-dx-1);
                    tile6 = game.getAt(middleColumn+dx+2, row-dx-2);
                    tile7 = game.getAt(middleColumn+dx+3, row-dx-3);
                    if ((row-dx < game.getHeight()) && (row-dx-3 >= 0)) {
                        countBlanks = (tile4==0?1:0)+(tile5==0?1:0)+(tile6 ==0?1:0)+(tile7==0?1:0);
                        countMax = (tile4==min?1:0)+(tile5==min?1:0)+(tile6 ==min?1:0)+(tile7==min?1:0);
                    }
                    if (countBlanks == 2 && countMax == 2) { connectTwoWins++; }
                    if (countMax == 3 && countBlanks == 1) { connectThreeWins++; }
                }
            }
        }
        return 10*connectThreeWins+2*connectTwoWins;
    }
}
