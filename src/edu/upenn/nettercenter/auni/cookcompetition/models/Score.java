package edu.upenn.nettercenter.auni.cookcompetition.models;

import com.j256.ormlite.field.DatabaseField;

public abstract class Score {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    private Event event;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    private ScoreField scoreField;

    @DatabaseField
    private int score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public ScoreField getScoreField() {
        return scoreField;
    }

    public void setScoreField(ScoreField scoreField) {
        this.scoreField = scoreField;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
