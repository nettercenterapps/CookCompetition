package edu.upenn.nettercenter.auni.cookcompetition.models;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class Event {

	@DatabaseField(generatedId = true)
	private Long id;

	@DatabaseField
	private Date date;

	@DatabaseField
	private String name;

	public Event() {
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
