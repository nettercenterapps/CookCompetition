package edu.upenn.nettercenter.auni.cookcompetition.models;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;

public class ScoreMap extends LinkedHashMap<Event, Integer> {
	
	private static final long serialVersionUID = -439128286709023128L;
	private LinkedHashMap<ScoreField, Map<Event, Integer>> scoreByFieldMap =
			new LinkedHashMap<ScoreField, Map<Event, Integer>>();
	
	public ScoreMap(List<? extends Score> scores) throws SQLException {    	
        for (Score score : scores) {
        	Event event = score.getEvent();
        	if (!this.containsKey(event)) {
        		this.put(event, score.getNumericScore());
        	} else {
				this.put(event, this.get(event) + score.getNumericScore());
        	}
        	
        	if (!scoreByFieldMap.containsKey(score.getScoreField())) {
        		scoreByFieldMap.put(score.getScoreField(), new HashMap<Event, Integer>());
        	}
        	scoreByFieldMap.get(score.getScoreField()).put(
        			score.getEvent(), score.getNumericScore()
        	);
		}
	}
	
	public Map<Event, Integer> getScoreMapByField(ScoreField scoreField) {
		return scoreByFieldMap.get(scoreField);
	}
	
	public int getTotalScore() {
		int total = 0;
		for (int score : this.values()) {
			total += score;
		}
		return total;
	}
	
	public void combine(ScoreMap scoreMap) {
		// combine total score
		for (Map.Entry<Event, Integer> entry : scoreMap.entrySet()) {
			if (this.containsKey(entry.getKey())) {
				this.put(entry.getKey(), this.get(entry.getKey()) + entry.getValue());
			} else {
				this.put(entry.getKey(), entry.getValue());
			}
		}
		
		// combine scoreByFieldMap
		for (Map.Entry<ScoreField, Map<Event, Integer>> entry : scoreMap.scoreByFieldMap.entrySet()) {
			if (!this.scoreByFieldMap.containsKey(entry.getKey())) {
				this.scoreByFieldMap.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + "\nscoreByFieldMap=" + scoreByFieldMap.toString();
	}
}
