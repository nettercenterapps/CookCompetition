package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.sql.SQLException;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Student detail screen. This fragment is
 * either contained in a {@link StudentListActivity} in two-pane mode (on
 * tablets) or a {@link StudentDetailActivity} on handsets.
 */
@EFragment
public class ManagementStudentDetailFragment extends Fragment {
	
	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> dao = null;

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Student mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ManagementStudentDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			try {
				mItem = dao.queryForId(getArguments().getLong(ARG_ITEM_ID));
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_student_detail,
				container, false);

		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.student_name))
					.setText(mItem.getName());
		}

		return rootView;
	}
}
