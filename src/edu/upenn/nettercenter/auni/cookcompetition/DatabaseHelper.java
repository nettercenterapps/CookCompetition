package edu.upenn.nettercenter.auni.cookcompetition;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import edu.upenn.nettercenter.auni.cookcompetition.models.Student;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "CookCompetition.db";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Student.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

		RuntimeExceptionDao<Student, Long> dao = getRuntimeExceptionDao(Student.class);
		List<Student> testStudents = Arrays.asList(
					new Student("Albert Gross"),
					new Student("Jamie Massey"),
					new Student("Pam Horton"),
					new Student("David Elliott"),
					new Student("Patricia Bennett")
				);
		for (Student student : testStudents) {
			dao.create(student);
		}
				
		Log.i(DatabaseHelper.class.getName(), "created dummy entries in onCreate");
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
	}
}
