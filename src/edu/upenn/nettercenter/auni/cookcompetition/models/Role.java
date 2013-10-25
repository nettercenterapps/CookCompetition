package edu.upenn.nettercenter.auni.cookcompetition.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Role {

    public static final Role ROLE_ABSENT = new Role("Absent");
    static {
        ROLE_ABSENT.id = 0L;
    }

	@DatabaseField(generatedId = true)
	private long id;

	@DatabaseField
	private String name;

	public Role() {
	}

	public Role(String name) {
		this.name = name;
	}

    public long getId() {
        return id;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Override
    public String toString() {
        return name;
    }

}
