package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
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
@OptionsMenu(R.menu.team)
public class TeamFragment extends Fragment implements TeamListFragment.Callbacks {
	
	@OrmLiteDao(helper = DatabaseHelper.class, model = Team.class)
	Dao<Team, Long> dao;
	
	TeamListFragment listFragment;
	
	Long selectedItemId;
	
	@AfterViews
	void loadFragments() {
		listFragment = new TeamListFragment_();
		getChildFragmentManager().beginTransaction()
			.replace(R.id.list_container, listFragment)
			.commit();
		listFragment.setCallbacks(this);
		setHasOptionsMenu(true);
	}

	/**
	 * Callback method from {@link ManagementStudentListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Long id) {
		selectedItemId = id;
		
		Bundle arguments = new Bundle();
		arguments.putLong(TeamDetailFragment_.ARG_ITEM_ID, id);
		Fragment fragment = new TeamDetailFragment_();
		fragment.setArguments(arguments);
		getChildFragmentManager().beginTransaction()
				.replace(R.id.detail_container, fragment).commit();
	}
	
	@OptionsItem(R.id.menu_team_add)
	void addTeam() {
		Intent intent = new Intent(getActivity(), TeamAddTeamActivity_.class);
		startActivityForResult(intent, 1);
	}
	
	@OptionsItem(R.id.menu_team_edit)
	void editStudent() {
		if (selectedItemId == null) return;
		
		Intent intent = new Intent(getActivity(), TeamAddTeamActivity_.class);
		intent.putExtra("itemId", selectedItemId);
		startActivityForResult(intent, 2);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		listFragment.refreshList();
	}
	
	@OptionsItem(R.id.menu_team_delete)
	void deleteStudent() {
		if (selectedItemId == null) return; 
		
		new AlertDialog.Builder(getActivity())
			.setMessage("Delete this team?")
		    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    	@Override
			    public void onClick(DialogInterface dialog, int which) {
			        try {
						dao.deleteById(selectedItemId);
						listFragment.refreshList();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			    }
		    })
		    .setNegativeButton(android.R.string.cancel, null)
		    .show();
	}
}
