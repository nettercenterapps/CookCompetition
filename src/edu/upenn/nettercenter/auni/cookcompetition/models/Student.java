package edu.upenn.nettercenter.auni.cookcompetition.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Student {

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String nickname;
    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
	private Team team;
	@DatabaseField
	private boolean isActive;

	public Student() {
	}
	
	public Student(String name, String nickname) {
		this.name = name;
		this.nickname = nickname;
		this.isActive = true;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getNickname() {
	    return nickname;
	}
	
	public Team getTeam() {
	    return team;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setNickname(String nickname) {
	    this.nickname = nickname;
	}
	
	public void setTeam(Team team) {
	    this.team = team;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return name;
	}

}
