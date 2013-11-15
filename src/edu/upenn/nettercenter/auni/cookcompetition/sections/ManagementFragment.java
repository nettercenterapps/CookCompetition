package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;

/**
 * An fragment for representing the management section of the app. 
 * 
 * This fragment presents the list of items and item details side-by-side using two
 * vertical panes. 
 * <p>
 * This fragment uses nested fragment feature in Android 4.2. The list of items is a
 * {@link ManagementStudentListFragment} and the item details (if present) is a
 * {@link ManagementStudentDetailFragment}. 
 * <p>
 * This activity also implements the required
 * {@link ManagementStudentListFragment.Callbacks} interface to listen for item
 * selections.
 */
@EFragment(R.layout.twopane)
@OptionsMenu(R.menu.management)
public class ManagementFragment extends Fragment implements ManagementStudentListFragment.Callbacks {
	
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

	@OptionsItem(R.id.menu_student_add)
	void addStudent() {
		Intent intent = new Intent(getActivity(), ManagementAddStudentActivity_.class);
		startActivityForResult(intent, 1);
	}
	
	@OptionsItem(R.id.menu_student_edit)
	void editStudent() {
		if (selectedStudentId == null) return;
		
		Intent intent = new Intent(getActivity(), ManagementAddStudentActivity_.class);
		intent.putExtra("studentId", selectedStudentId);
		startActivityForResult(intent, 2);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		studentListFragment.refreshList();
        if (data != null) {
            final long id = data.getLongExtra("id", -1L);
            if (id != -1) {
                new Handler().post(new Runnable() {
                    public void run() {
                    studentListFragment.setSelectedStudent(new Student(id));
                    }
                });
            }
        }
	}
	
	@OptionsItem(R.id.menu_student_delete)
	void deleteStudent() {
		if (selectedStudentId == null) return; 
		
		new AlertDialog.Builder(getActivity())
			.setMessage("Delete this student?")
		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    	@Override
			    public void onClick(DialogInterface dialog, int which) {
			        try {
						dao.deleteById(selectedStudentId);
						studentListFragment.refreshList();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			    }
		    })
		    .setNegativeButton(android.R.string.cancel, null)
		    .show();
	}

    @Override
    public void onStudentSelected(Student student) {
        selectedStudentId = student.getId();

        Bundle arguments = new Bundle();
        arguments.putLong(ManagementStudentDetailFragment_.ARG_ITEM_ID, selectedStudentId);
        Fragment fragment = new ManagementStudentDetailFragment_();
        fragment.setArguments(arguments);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment).commit();
    }

    @Override
    public void onTeamSelected(Team team) {
    }
}
