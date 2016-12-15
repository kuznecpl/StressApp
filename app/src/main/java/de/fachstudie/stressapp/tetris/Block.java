package de.fachstudie.stressapp.tetris;

import android.util.Log;

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

    private final int[][] I_1 = new int[][]{{1, 1, 1, 1}};
    private final int[][] I_2 = new int[][]{{1}, {1}, {1}, {1}};
    private final int[][][] I_ROTATIONS = {I_1, I_2, I_1, I_2};
    private final int[][] I_SHIFTS = {{-1, 2}, {2, -2}, {-2, 1}, {1, -1}};

    private final int[][] J_1 = new int[][]{{1, 0, 0}, {1, 1, 1}};
    private final int[][] J_2 = new int[][]{{1, 1}, {1, 0}, {1, 0}};
    private final int[][] J_3 = new int[][]{{1, 1, 1}, {0, 0, 1}};
    private final int[][] J_4 = new int[][]{{0, 1}, {0, 1}, {1, 1}};
    private final int[][][] J_ROTATIONS = {J_1, J_2, J_3, J_4};
    private final int[][] J_SHIFTS = {{0, 1}, {1, -1}, {-1, 1}, {0, -1}};

    private final int[][] S_1 = new int[][]{{0, 1, 1}, {1, 1, 0}};
    private final int[][] S_2 = new int[][]{{1, 0}, {1, 1}, {0, 1}};
    private final int[][][] S_ROTATIONS = {S_1, S_2, S_1, S_2};
    private final int[][] S_SHIFTS = {{-1, 1}, {1, -1}, {-2, 0}, {1, 0}};

    private final int[][] Z_1 = new int[][]{{1, 1, 0}, {0, 1, 1}};
    private final int[][] Z_2 = new int[][]{{0, 1}, {1, 1}, {1, 0}};
    private final int[][][] Z_ROTATIONS = {Z_1, Z_2, Z_1, Z_2};
    private final int[][] Z_SHIFTS = {{2, 1}, {-1, -1}, {1, 0}, {-2, 0}};

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
            case I:
                this.shape = I_ROTATIONS;
                this.shift = I_SHIFTS;
                break;
            case J:
                this.shape = J_ROTATIONS;
                this.shift = J_SHIFTS;
                break;
            case S:
                this.shape = S_ROTATIONS;
                this.shift = S_SHIFTS;
                break;
            case Z:
                this.shape = Z_ROTATIONS;
                this.shift = Z_SHIFTS;
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
        rotate(1);
    }

    public void rotate(int direction) {
        if (direction > 0) {
            this.y += shift[(rotationIndex) % 4][0];
            this.x += shift[(rotationIndex) % 4][1];
        } else {
            this.y -= shift[(rotationIndex - 1) % 4][0];
            this.x -= shift[(rotationIndex - 1) % 4][1];
        }
        this.rotationIndex += direction;

        Log.d("RotationIndex", rotationIndex + "");

        if (this.x < 0) {
            this.x = 0;
        }
    }

    public void simulateRotate(int[][] state) {
        this.rotate(1);
        computeOverlaps(state);
        this.rotate(-1);
    }

    private void computeOverlaps(int[][] state) {
        for (int j = getY(); j < getY() + getHeight() && j < state.length; j++) {
            for (int i = getX(); i < getX() + getWidth() && j >= 0 && i < state[j].length; i++) {
                int yOffset = j - getY();
                int xOffset = i - getX();
                if (getShape()[yOffset][xOffset] == 1) {
                    if (state[j][i] > 0) {
                        state[j][i] = -1;
                    }
                }
            }
        }
    }

    public void simulateStepDown(int[][] state) {
        this.setY(getY() + 1);
        computeOverlaps(state);
        this.setY(getY() - 1);
    }

    public void simulateStepRight(int[][] state) {
        this.moveRight();
        computeOverlaps(state);
        this.moveLeft();
    }

    public void simulateStepLeft(int[][] state) {
        this.moveLeft();
        computeOverlaps(state);
        this.moveRight();
    }

    public enum Shape {
        SQUARE(1), L(2), T(3), I(4), J(5), S(6), Z(7);

        private int n;

        Shape(int n) {
            this.n = n;
        }

        public int getN() {
            return n;
        }
    }
}