package edu.upenn.nettercenter.auni.cookcompetition.models;

public class ScoreFieldValue {

    private String caption;
    private int value;
    private int color;

    public ScoreFieldValue(String caption, int value, int color) {
        this.caption = caption;
        this.value = value;
        this.color = color;
    }

    public ScoreFieldValue(String caption, int value) {
        this.caption = caption;
        this.value = value;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
