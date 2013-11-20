package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Fragment;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;

@EFragment(R.layout.twopane)
public class ScoreboardFragment extends Fragment implements ManagementStudentListFragment.Callbacks {
	
	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> dao;
	
	ManagementStudentListFragment studentListFragment;
	
	Long selectedStudentId;
	
	@AfterViews
	void loadFragments() {
        if (studentListFragment == null) {
            studentListFragment = new ManagementStudentListFragment_();
            getChildFragmentManager().beginTransaction()
                .replace(R.id.list_container, studentListFragment)
                .commit();
            studentListFragment.setCallbacks(this);
		    setHasOptionsMenu(true);
        }
	}

	@Override
	public void onStudentSelected(Student student) {
		selectedStudentId = student.getId();
		
		Bundle arguments = new Bundle();
		arguments.putLong(ScoreboardDetailFragment_.ARG_ITEM_ID, selectedStudentId);
		Fragment fragment = new ScoreboardDetailFragment_();
		fragment.setArguments(arguments);
		getChildFragmentManager().beginTransaction()
				.replace(R.id.detail_container, fragment).commit();
	}

	@Override
	public void onTeamSelected(Team team) {
		// TODO Auto-generated method stub
		
	}		
}
