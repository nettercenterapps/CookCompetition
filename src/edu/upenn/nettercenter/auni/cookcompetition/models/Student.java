package edu.upenn.nettercenter.auni.cookcompetition.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

@DatabaseTable
public class Student {

	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String nickname;
    @DatabaseField(canBeNull = true, foreign = true)
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

    public static LinkedHashMap<String, List<Student>> partitionByName(List<Student> students) {
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                String name1 = student1.name;
                String name2 = student2.name;

                if (name1 == null && name2 == null) {
                    return 0;
                } else if (name1 == null) {
                    return -1;
                } else if (name2 == null) {
                    return 1;
                } else {
                    return name1.trim().compareToIgnoreCase(name2.trim());
                }
            }
        });
        LinkedHashMap<String, List<Student>> result = new LinkedHashMap<String, List<Student>>();
        for (Student student : students) {
            String c = String.valueOf(student.getName().charAt(0));

            if (!result.containsKey(c)) {
                result.put(c, new ArrayList<Student>());
            }
            result.get(c).add(student);
        }
        return result;
    }
}
