package edu.upenn.nettercenter.auni.cookcompetition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.models.Student;

public class Utils {
    public static void sortStudentsByName(List<Student> students) {
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                String name1 = student1.getName();
                String name2 = student2.getName();

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
    }

    public static LinkedHashMap<String, List<Student>> partitionByName(List<Student> students) {
        sortStudentsByName(students);
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

    public static LinkedHashMap<String, List<Student>> partitionByTeam(List<Student> students) {
        sortStudentsByName(students);
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                if (student1.getTeam() == null && student2.getTeam() == null) {
                    return 0;
                } else if (student1.getTeam() == null) {
                    return -1;
                } else if (student2.getTeam() == null) {
                    return 1;
                } else {
                    System.out.println(student1.getTeam().getName());
                    return student1.getTeam().getName().trim().compareToIgnoreCase(
                            student2.getTeam().getName().trim());
                }
            }
        });
        LinkedHashMap<String, List<Student>> result = new LinkedHashMap<String, List<Student>>();
        List<Student> studentsWithoutTeam = new ArrayList<Student>();
        for (Student student : students) {
            if (student.getTeam() != null) {
                String k = String.valueOf(student.getTeam().getName());

                if (!result.containsKey(k)) {
                    result.put(k, new ArrayList<Student>());
                }
                result.get(k).add(student);
            } else {
                studentsWithoutTeam.add(student);
            }
        }
        if (studentsWithoutTeam.size() > 0) {
            result.put("No Team", studentsWithoutTeam);
        }
        return result;
    }
}
