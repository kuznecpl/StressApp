package de.fachstudie.stressapp.tetris;

public class Block extends Item {
    private final int[][] SQUARE = new int[][]{{1, 1}, {1, 1}};
    private final int[][][] SQUARE_ROTATIONS = {SQUARE, SQUARE, SQUARE, SQUARE};
    private final int[][] SQUARE_SHIFTS = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};

    //See http://tetris.wikia.com/wiki/SRS
    private final int[][] L_1 = new int[][]{{1, 0}, {1, 0}, {1, 1}};
    private final int[][] L_2 = new int[][]{{1, 1, 1}, {1, 0, 0}};
    private final int[][] L_3 = new int[][]{{1, 1}, {0, 1}, {0, 1}};
    private final int[][] L_4 = new int[][]{{0, 0, 1}, {1, 1, 1}};
    private final int[][][] L_ROTATIONS = {L_1, L_2, L_3, L_4};
    private final int[][] L_SHIFTS = {{1, -1}, {-1, 0}, {0, 0}, {0, 1}};

    private final int[][] T_1 = new int[][]{{1, 1, 1}, {0, 1, 0}};
    private final int[][] T_2 = new int[][]{{0, 1}, {1, 1}, {0, 1}};
    private final int[][] T_3 = new int[][]{{0, 1, 0}, {1, 1, 1}};
    private final int[][] T_4 = new int[][]{{1, 0}, {1, 1}, {1, 0}};
    private final int[][][] T_ROTATIONS = {T_1, T_2, T_3, T_4};
    private final int[][] T_SHIFTS = {{-1, 0}, {0, 0}, {0, 1}, {1, -1}};

    private final int[][] shift;
    private Shape type;
    private int[][][] shape;
    private int rotationIndex = 0;

    public Block(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.type = Shape.L;
        this.shape = L_ROTATIONS;
        this.shift = L_SHIFTS;
    }

    public Block(int x, int y, int width, int height, Shape shape) {
        super(x, y, width, height);

        this.type = shape;
        switch (shape) {
            case SQUARE:
                this.shape = SQUARE_ROTATIONS;
                this.shift = SQUARE_SHIFTS;
                break;
            case L:
                this.shape = L_ROTATIONS;
                this.shift = L_SHIFTS;
                break;
            case T:
                this.shape = T_ROTATIONS;
                this.shift = T_SHIFTS;
                break;
            default:
                this.shape = T_ROTATIONS;
                this.shift = T_SHIFTS;
                break;
        }
    }

    public Shape getType() {
        return type;
    }

    public int[][] getShape() {
        return shape[rotationIndex % 4];
    }

    @Override
    public int getHeight() {
        return shape[rotationIndex % 4].length;
    }

    @Override
    public int getWidth() {
        return shape[rotationIndex % 4][0].length;
    }

    public void rotate() {
        this.y += shift[(rotationIndex) % 4][0];
        this.x += shift[(rotationIndex) % 4][1];
        this.rotationIndex++;

        if (this.x < 0) {
            this.x = 0;
        }
    }

    public void simulateStepDown(int[][] state) {
        this.setY(getY() + 1);
        for (int j = getY(); j < getY() + getHeight() && j < state.length; j++) {
            for (int i = getX(); i < getX() + getWidth() && i < state[j].length; i++) {
                int yOffset = j - getY();
                int xOffset = i - getX();
                if (getShape()[yOffset][xOffset] == 1) {
                    state[j][i]++;
                }
            }
        }
        this.setY(getY() - 1);
    }

    public enum Shape {
        SQUARE, L, T
    }
}
