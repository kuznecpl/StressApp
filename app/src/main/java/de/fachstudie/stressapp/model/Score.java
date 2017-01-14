package de.fachstudie.stressapp.model;

import android.provider.BaseColumns;

/**
 * Created by Sanjeev on 14.01.2017.
 */

public class Score {

    private int id;
    private int value;

    public Score(int id, int value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static class ScoreEntry implements BaseColumns {
        public static final String TABLE_NAME = "score";
        public static final String VALUE = "value";
    }
}
