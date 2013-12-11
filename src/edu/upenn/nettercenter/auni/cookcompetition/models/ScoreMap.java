package edu.upenn.nettercenter.auni.cookcompetition.models;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;

public class ScoreMap extends LinkedHashMap<Date, Integer> {
	
	private static final long serialVersionUID = -439128286709023128L;
	private LinkedHashMap<ScoreField, Map<Date, Integer>> scoreByFieldMap =
			new LinkedHashMap<ScoreField, Map<Date, Integer>>();
	
	public ScoreMap(List<? extends Score> scores) throws SQLException {    	
        for (Score score : scores) {
        	Date eventDate = score.getEvent().getDate();
        	if (!this.containsKey(eventDate)) {
        		this.put(eventDate, score.getNumericScore());
        	} else {
				this.put(eventDate, this.get(eventDate) + score.getNumericScore());
        	}
        	
        	if (!scoreByFieldMap.containsKey(score.getScoreField())) {
        		scoreByFieldMap.put(score.getScoreField(), new HashMap<Date, Integer>());
        	}
        	scoreByFieldMap.get(score.getScoreField()).put(
        			score.getEvent().getDate(), score.getNumericScore()
        	);
		}
	}
	
	public Map<Date, Integer> getScoreMapByField(ScoreField scoreField) {
		return scoreByFieldMap.get(scoreField);
	}
	
	public int getTotalScore() {
		int total = 0;
		for (int score : this.values()) {
			total += score;
		}
		return total;
	}
}
