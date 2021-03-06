package org.hcilab.projects.stressblocks.tetris;

public class Item {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private int droppedRows = 0;

    public Item(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void stepDown() {
        this.y += 1;
    }

    public void moveRight() {
        this.x += 1;
    }

    public void moveLeft() {
        this.x -= 1;
        if(this.x < 0) {
            this.x = 0;
        }
    }

    public void increaseDroppedRows() {
        this.droppedRows++;
    }

    public int getDroppedRows() {
        return droppedRows;
    }
}