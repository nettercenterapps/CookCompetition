package edu.upenn.nettercenter.auni.cookcompetition;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.Arrays;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.models.Role;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreField;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreFieldType;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;

public class PreloadedData {

    private static DatabaseHelper helper;
    public static void load(DatabaseHelper helper) {
        PreloadedData.helper = helper;

        createStudents();
        createRoles();
        createScoreFields();
    }

    private static void createStudents() {
        RuntimeExceptionDao<Student, Long> dao = helper.getRuntimeExceptionDao(Student.class);
        List<Student> students = Arrays.asList(
                new Student("Imani", "A", null),
                new Student("Antonio", "C", null),
                new Student("Nhyera", "C", null),
                new Student("Amoura", "C", null),
                new Student("Jaquin", "C", null),
                new Student("Nydia", "H", null),
                new Student("Tajiyanah", "J", null),
                new Student("Vincent", "L", null),
                new Student("Chloe", "M", null),
                new Student("Sanaa", "M", null),
                new Student("Tia", "S", null),
                new Student("Tyshyne", "S", null),
                new Student("Treasure", "W", null)
        );

        for (Student student : students) {
            dao.create(student);
        }
    }

    private static void createRoles() {
        RuntimeExceptionDao<Role, Long> dao = helper.getRuntimeExceptionDao(Role.class);
        List<Role> roles = Arrays.asList(
                new Role("Cooking - Whole Grains"),
                new Role("Cooking - Beans"),
                new Role("Cooking - Fruit"),
                new Role("Cooking - Slaw "),
                new Role("Cooking - Mash"),
                new Role("Enrichment - Nutrition"),
                new Role("Enrichment - Menu"),
                new Role("Enrichment - Sculpture"),
                new Role("Enrichment - Presentation"),
                new Role("Enrichment - Photo")
        );

        for (Role role : roles) {
            dao.create(role);
        }
    }

    private static void createScoreFields() {
        RuntimeExceptionDao<ScoreField, Long> dao = helper.getRuntimeExceptionDao(ScoreField.class);
        List<ScoreField> scoreFields = Arrays.asList(
                new ScoreField("Mastering Activity", ScoreField.FIELD_TYPE_STUDENT, ScoreFieldType.GOLD_SILVER_BRONZE),
                new ScoreField("Behavior", ScoreField.FIELD_TYPE_STUDENT, ScoreFieldType.GOLD_SILVER_BRONZE),
                new ScoreField("Clean-up", ScoreField.FIELD_TYPE_STUDENT, ScoreFieldType.GOLD_SILVER_BRONZE),
                new ScoreField("Best Dish", ScoreField.FIELD_TYPE_TEAM, ScoreFieldType.CHECK_BOX),
                new ScoreField("Best Performance", ScoreField.FIELD_TYPE_TEAM, ScoreFieldType.CHECK_BOX),
                new ScoreField("Weekly BONUS Quiz", ScoreField.FIELD_TYPE_TEAM, ScoreFieldType.CHECK_BOX)
        );

        for (ScoreField scoreField : scoreFields) {
            dao.create(scoreField);
        }
    }
}
