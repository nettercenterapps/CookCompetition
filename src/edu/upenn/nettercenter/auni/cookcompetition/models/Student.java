package edu.upenn.nettercenter.auni.cookcompetition.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {

	/**
	 * An array of items.
	 */
	public static List<Student> ITEMS = new ArrayList<Student>();

	/**
	 * A map of sample items, by ID.
	 */
	public static Map<String, Student> ITEM_MAP = new HashMap<String, Student>();

	static {
		// Add 3 sample items.
		addItem(new Student("1", "Item 1"));
		addItem(new Student("2", "Item 2"));
		addItem(new Student("3", "Item 3"));
	}

	private static void addItem(Student item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	public String id;
	public String content;

	public Student(String id, String content) {
		this.id = id;
		this.content = content;
	}

	@Override
	public String toString() {
		return content;
	}
}
