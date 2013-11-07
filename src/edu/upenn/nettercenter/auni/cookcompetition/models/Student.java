package edu.upenn.nettercenter.auni.cookcompetition.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Student {

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField
	private String firstName;
	@DatabaseField
	private String lastInitial;
	@DatabaseField
	private String nickname;
    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
	private Team team;
	@DatabaseField
	private boolean isActive;

	public Student() {
	}
	
	public Student(String firstName, String lastInitial, String nickname) {
		this.firstName = firstName;
		this.lastInitial = lastInitial;
		this.nickname = nickname;
		this.isActive = true;
	}

    public Student(long id) {
        this.id = id;
    }

    public Long getId() {
		return id;
	}
	
	public String getFirstName() {
	    return firstName;
	}
	
	public String getName() {
		return firstName + " " + lastInitial + ".";
	}
	
	public String getNickname() {
	    return nickname;
	}
	
	public Team getTeam() {
	    return team;
	}
	
	public String getLastInitial() {
	    return lastInitial;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
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
	
	public void setLastInitial(String lastInitial) {
	    this.lastInitial = lastInitial;
	}

	@Override
	public String toString() {
		return getName();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != null ? !id.equals(student.id) : student.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
