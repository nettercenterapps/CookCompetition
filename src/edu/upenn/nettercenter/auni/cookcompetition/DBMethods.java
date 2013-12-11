package edu.upenn.nettercenter.auni.cookcompetition;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import edu.upenn.nettercenter.auni.cookcompetition.models.Event;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreMap;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;
import edu.upenn.nettercenter.auni.cookcompetition.models.TeamScore;

public class DBMethods {
	public static int getTotalStudentScoreByEvent(
			Dao<StudentScore, Long> studentScoreDao,
			List<Student> students,
			Event event) {
		int total = 0;
		try {
			for (Student student : students) {			
				List<StudentScore> scores = studentScoreDao.queryBuilder()
					 	.where().eq("student_id", student.getId())
					 	.and().eq("event_id", event.getId())
					 	.query();
				ScoreMap scoreMap = new ScoreMap(scores);
				total += scoreMap.getTotalScore();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return total;
	}
	
	public static int getTotalTeamScoreByEvent(
			Dao<TeamScore, Long> teamScoreDao,
			Team team,
			Event event) {
		int total = 0;
		try {
			List<TeamScore> scores = teamScoreDao.queryBuilder()
				 	.where().eq("team_id", team.getId())
				 	.and().eq("event_id", event.getId())
				 	.query();
			ScoreMap scoreMap = new ScoreMap(scores);
			total = scoreMap.getTotalScore();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return total;
	}
	
	public static int getTotalStudentScore(Dao<StudentScore, Long> studentScoreDao, List<Student> students) {
		int total = 0;
		try {
			for (Student student : students) {			
				List<StudentScore> scores = studentScoreDao.queryBuilder()
					 	.where().eq("student_id", student.getId())
					 	.query();
				ScoreMap scoreMap = new ScoreMap(scores);
				total += scoreMap.getTotalScore();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return total;
	}
	
	public static int getTotalTeamScore(Dao<TeamScore, Long> teamScoreDao, Team team) {
		int total = 0;
		try {
			List<TeamScore> scores = teamScoreDao.queryBuilder()
				 	.where().eq("team_id", team.getId())
				 	.query();
			ScoreMap scoreMap = new ScoreMap(scores);
			total = scoreMap.getTotalScore();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return total;
	}
}
