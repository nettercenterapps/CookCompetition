package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreField;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;

@EFragment(R.layout.twopane)
@OptionsMenu(R.menu.scoreboard)
public class ScoreboardFragment extends Fragment implements ManagementStudentListFragment.Callbacks {
	
	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> dao;
    @OrmLiteDao(helper = DatabaseHelper.class, model = ScoreField.class)
    Dao<ScoreField, Long> scoreFieldDao = null;
	
	ManagementStudentListFragment studentListFragment;
	ScoreboardDetailFragment scoreboardDetailFragment;
	
	Long selectedStudentId;
	Long selectedTeamId;
	
	List<ScoreField> studentScoreFields;
	List<ScoreField> teamScoreFields;
	List<ScoreField> scoreFields;
	
	boolean showTotal = true;

	List<ScoreField> studentSeriesShown = new ArrayList<ScoreField>();
	List<ScoreField> teamSeriesShown = new ArrayList<ScoreField>();
	List<ScoreField> seriesShown = studentSeriesShown;
	
	@AfterViews
	void loadFragments() {
        if (studentListFragment == null) {
            studentListFragment = new ManagementStudentListFragment_();
            studentListFragment.setGroupByTeam(true);
            getChildFragmentManager().beginTransaction()
                .replace(R.id.list_container, studentListFragment)
                .commit();
            studentListFragment.setCallbacks(this);
		    setHasOptionsMenu(true);
        }
	}

	@AfterViews
	void loadScoreFields() {
		try {
			studentScoreFields = scoreFieldDao.queryForEq("type", ScoreField.FIELD_TYPE_STUDENT);
			teamScoreFields = scoreFieldDao.queryForEq("type", ScoreField.FIELD_TYPE_TEAM);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@OptionsItem(R.id.menu_series)
	void chooseSeries() {	
		if (selectedStudentId == null && selectedTeamId == null) return;
		
		final List<ScoreField> selectedSeries = new ArrayList<ScoreField>(seriesShown); 
		
		String[] names = new String[scoreFields.size()];
		boolean[] checked = new boolean[scoreFields.size()];
		for (int i = 0; i < names.length; i++) {
			ScoreField scoreField = scoreFields.get(i);
			names[i] = scoreField.getName();
			checked[i] = selectedSeries.contains(scoreField);
		}
		
		new AlertDialog.Builder(getActivity())
        .setTitle("Select Series")
        .setMultiChoiceItems(names, checked, new DialogInterface.OnMultiChoiceClickListener(){
        	@Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        		ScoreField field = scoreFields.get(which);
        		System.out.println(which + ", " + isChecked);
				if (isChecked) {
					selectedSeries.add(field);
                } else if (selectedSeries.contains(field)){
                	selectedSeries.remove(field);
                }
            }
		 }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		     @Override
		     public void onClick(DialogInterface dialog, int id) {
		    	 seriesShown.clear();
		    	 seriesShown.addAll(selectedSeries);
		    	 refreshSeriesShown();
		     }
		 }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		     @Override
		     public void onClick(DialogInterface dialog, int id) {
		     }
		 }).show();
	}
	
	private void refreshSeriesShown() {
		if (scoreboardDetailFragment != null) {
			scoreboardDetailFragment.setSeriesShown(seriesShown);
			scoreboardDetailFragment.refreshGraph();
		}
	}
	
	@Override
	public void onStudentSelected(Student student) {
		selectedStudentId = student.getId();
		selectedTeamId = null;
		seriesShown = studentSeriesShown;
		scoreFields = studentScoreFields;
		
		Bundle arguments = new Bundle();
		arguments.putLong(ScoreboardDetailFragment_.ARG_STUDENT_ID, selectedStudentId);
		showDetailFragment(arguments);
	}

	@Override
	public void onTeamSelected(Team team) {
		selectedStudentId = null;
		selectedTeamId = team.getId();
		seriesShown = teamSeriesShown;
		scoreFields = teamScoreFields;
		
		Bundle arguments = new Bundle();
		arguments.putLong(ScoreboardDetailFragment_.ARG_TEAM_ID, selectedTeamId);
		showDetailFragment(arguments);
	}		
	
	private void showDetailFragment(Bundle arguments) {
		scoreboardDetailFragment = new ScoreboardDetailFragment_();
		scoreboardDetailFragment.setSeriesShown(seriesShown);
		scoreboardDetailFragment.setArguments(arguments);
		getChildFragmentManager().beginTransaction()
				.replace(R.id.detail_container, scoreboardDetailFragment).commit();
	}
}
