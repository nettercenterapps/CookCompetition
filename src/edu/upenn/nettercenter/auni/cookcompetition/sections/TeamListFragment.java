package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.sql.SQLException;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;

@EFragment
public class TeamListFragment extends ListFragment {

	@OrmLiteDao(helper = DatabaseHelper.class, model = Team.class)
	Dao<Team, Long> dao;

	private Callbacks mCallbacks = sDummyCallbacks;
	private List<Team> teams;
	
	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(Long id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(Long id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TeamListFragment() {
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		setActivateOnItemClick(true);
		refreshList();
	}
	
	public void setCallbacks(Callbacks callbacks) {
		this.mCallbacks = callbacks;
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		mCallbacks.onItemSelected(teams.get(position).getId());
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}
	
	public void refreshList() {
		try {
			teams = getItems();
			setListAdapter(new ArrayAdapter<Team>(getActivity(),
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, teams));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private List<Team> getItems() throws SQLException {		
		return dao.queryForAll();
	}
}
