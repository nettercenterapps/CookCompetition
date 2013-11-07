package edu.upenn.nettercenter.auni.cookcompetition.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class StudentRecord {

	@DatabaseField(generatedId = true)
	private Long id;

	@DatabaseField(foreign = true)
	private Student student;

	@DatabaseField(foreign = true)
	private Event event;

	@DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
	private Role role;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
