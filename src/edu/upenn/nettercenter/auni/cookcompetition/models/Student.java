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
	private boolean isActive;

	public Student() {
	}
	
	public Student(String name) {
		this.name = name;
		this.isActive = true;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
