package edu.upenn.nettercenter.auni.cookcompetition.models;

import java.util.List;

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
    
    public int getNumericScore() {
    	if (getScore() == 0) return 0;
    	
    	List<ScoreFieldValue> values = getScoreField().getScoreFieldType().getValues();
    	ScoreFieldValue value = values.get(getScore() - 1);
    	return value.getValue();
    }
}
