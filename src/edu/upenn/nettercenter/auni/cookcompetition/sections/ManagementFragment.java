package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.upenn.nettercenter.auni.cookcompetition.R;

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
public class ManagementFragment extends Fragment implements ManagementStudentListFragment.Callbacks {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_student_twopane,
				container, false);

		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ManagementStudentListFragment f = new ManagementStudentListFragment();
		getChildFragmentManager().beginTransaction()
			.replace(R.id.student_list_container, f)
			.commit();
		f.setCallbacks(this);
	}

	/**
	 * Callback method from {@link ManagementStudentListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		Bundle arguments = new Bundle();
		arguments.putString(ManagementStudentDetailFragment.ARG_ITEM_ID, id);
		ManagementStudentDetailFragment fragment = new ManagementStudentDetailFragment();
		fragment.setArguments(arguments);
		getChildFragmentManager().beginTransaction()
				.replace(R.id.student_detail_container, fragment).commit();

	}
}
