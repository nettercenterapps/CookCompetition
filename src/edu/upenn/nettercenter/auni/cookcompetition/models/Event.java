package edu.upenn.nettercenter.auni.cookcompetition.models;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

public class Event {

	@DatabaseField(generatedId = true)
	private Long id;

	@DatabaseField
	private Date date;

	@DatabaseField
	private String name;

	public Event() {
	}

    public Event(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", date=" + date +
                ", name='" + name + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
