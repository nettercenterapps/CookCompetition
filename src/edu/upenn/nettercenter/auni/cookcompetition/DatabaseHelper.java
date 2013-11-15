package edu.upenn.nettercenter.auni.cookcompetition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import edu.upenn.nettercenter.auni.cookcompetition.models.Event;
import edu.upenn.nettercenter.auni.cookcompetition.models.Role;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreField;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentRecord;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;
import edu.upenn.nettercenter.auni.cookcompetition.models.TeamScore;

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
			TableUtils.createTable(connectionSource, Team.class);
			TableUtils.createTable(connectionSource, Event.class);
			TableUtils.createTable(connectionSource, Role.class);
			TableUtils.createTable(connectionSource, StudentRecord.class);
            TableUtils.createTable(connectionSource, ScoreField.class);
            TableUtils.createTable(connectionSource, StudentScore.class);
            TableUtils.createTable(connectionSource, TeamScore.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

        PreloadedData.load(this);
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
	}
}
