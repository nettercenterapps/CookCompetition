package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

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
public class ManagementFragment extends Fragment implements ManagementStudentListFragment.Callbacks {
	
	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> dao;
	
	ManagementStudentListFragment studentListFragment;
	
	Long selectedStudentId;
	
	private SearchView searchView;

	ManagementStudentDetailFragment detailFragment;
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.management, menu);
		MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
		searchView = (SearchView) searchMenuItem.getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				studentListFragment.refreshList(newText);
				return false;
			}
		});
		searchView.setOnCloseListener(new OnCloseListener() {
			@Override
			public boolean onClose() {
				studentListFragment.refreshList();
				return false;
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_student_add) {
			addStudent();
			return true;
		} else if (item.getItemId() == R.id.menu_student_edit) {
			editStudent();
			return true;
		} else if (item.getItemId() == R.id.menu_student_delete) {
			deleteStudent();
			return true;
		}
		return false;
	}
	
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

	void addStudent() {
		Intent intent = new Intent(getActivity(), ManagementAddStudentActivity_.class);
		startActivityForResult(intent, 1);
	}
	
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
        detailFragment = new ManagementStudentDetailFragment_();
        detailFragment.setArguments(arguments);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.detail_container, detailFragment).commit();
    }

    @Override
    public void onTeamSelected(Team team) {
    }
}
