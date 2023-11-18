package com.codegym.task.task35.task3513;

import java.util.*;

public class Model {

    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];

    protected int score;
    protected int maxTile;

    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();

    private boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
        this.score = 0;
        this.maxTile = 0;
    }

    private List<Tile> getEmptyTiles(){
        List<Tile> emptyTilesList = new ArrayList<>();

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].isEmpty()) emptyTilesList.add(gameTiles[i][j]);
            }
        }
        return emptyTilesList;
    }

    private void addTile(){
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            int index = (int) (Math.random() * emptyTiles.size()) % emptyTiles.size();
            Tile emptyTile = emptyTiles.get(index);
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    public void resetGameTiles(){
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private boolean consolidateTiles(Tile[] tiles) {
        boolean changed = false;
        for (int j = 0; j < tiles.length; j++)
            for (int i = 1; i < tiles.length; i++) {
                if (tiles[i].value > 0 && tiles[i - 1].value == 0) {
                    tiles[i - 1].value = tiles[i].value;
                    tiles[i].value = 0;
                    changed = true;
                }
            }
        return changed;
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean changed = false;
        for (int i = 0; i < tiles.length - 1; i++) {
            if (tiles[i].value > 0 && tiles[i].value == tiles[i + 1].value) {
                tiles[i].value *= 2;
                tiles[i + 1].value = 0;
                score += tiles[i].value;
                maxTile = maxTile < tiles[i].value ? tiles[i].value : maxTile;
                changed = true;
            }
        }
        consolidateTiles(tiles);
        return changed;
    }

    private void regroupTiles(Tile[] tiles, int i) {
        for (int j = i; j < tiles.length - 1; j++) {
            tiles[j].value = tiles[j + 1].value;
        }
        tiles[tiles.length - 1] = new Tile(0);
    }

    private Tile[][] rotateClockwise(Tile[][] tiles) {
        final int N = tiles.length;
        Tile[][] result = new Tile[N][N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                result[c][N-1-r] = tiles[r][c];
            }
        }
        return result;
    }

    public void left(){
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean moveFlag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (consolidateTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }
        if (moveFlag) {
            addTile();
        }
        isSaveNeeded = true;
    }

    public void right(){
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }

    public void up(){
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        left();
        gameTiles = rotateClockwise(gameTiles);
    }

    public void down() {
        saveState(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }

    private int getEmptyTilesCount() {
        return getEmptyTiles().size();
    }

    private boolean isFull() {
        return getEmptyTilesCount() == 0;
    }

    public boolean canMove(){

        if (!isFull()) {
            return true;
        }
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                Tile t = gameTiles[x][y];
                if ((x < FIELD_WIDTH - 1 && t.value == gameTiles[x + 1][y].value)
                        || ((y < FIELD_WIDTH - 1) && t.value == gameTiles[x][y + 1].value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveState(Tile[][] gameTiles){
        Tile[][] savedBoard = new Tile[FIELD_WIDTH][FIELD_WIDTH];

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                savedBoard[i][j] = new Tile(gameTiles[i][j].value);
            }
        }

        previousStates.push(savedBoard);
        previousScores.push(this.score);
        isSaveNeeded = false;
    }

    public void rollback(){
        if (!previousStates.isEmpty() && !previousScores.isEmpty()){
            this.gameTiles = previousStates.pop();
            this.score = previousScores.pop();
        }
    }
    public void randomMove(){
        int random = (int) ((Math.random() * 100) % 4);

        switch (random){
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    public boolean hasBoardChanged(){
        Tile[][] previousGameBoard = previousStates.peek();

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value != previousGameBoard[i][j].value) return true;
            }
        }
        return false;
    }

    private MoveFitness getMoveFitness(Move move){
        move.move();
        MoveFitness moveToCompare = new MoveFitness(getEmptyTilesCount(), this.score, move);
        if (!hasBoardChanged()) return new MoveFitness(-1, 0, move);
        rollback();

        return moveToCompare;
    }

    public void autoMove(){
        PriorityQueue<MoveFitness> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.offer(getMoveFitness(this::left));
        queue.offer(getMoveFitness(this::right));
        queue.offer(getMoveFitness(this::up));
        queue.offer(getMoveFitness(this::down));
        queue.peek().getMove().move();
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }
}
