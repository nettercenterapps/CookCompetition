package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.adapters.SeparatedListAdapter;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;

@EFragment
public class ManagementStudentListFragment extends ListFragment {

	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> dao;

	private Callbacks mCallbacks = sDummyCallbacks;
    private SeparatedListAdapter adapter;
	
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
	public ManagementStudentListFragment() {
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

        if (getListView() != null) {
            getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        }
		refreshList();
	}
	
	public void setCallbacks(Callbacks callbacks) {
		this.mCallbacks = callbacks;
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

        if (adapter != null) {
            long studentId = ((Student) adapter.getItem(position)).getId();
            mCallbacks.onItemSelected(studentId);
        }
	}

	@SuppressWarnings("ConstantConditions")
    public void refreshList() {
		try {
            List<Student> students = getStudents();
            adapter = new SeparatedListAdapter(getActivity());
            LinkedHashMap<String,List<Student>> studentMap = Student.partitionByName(students);
            for (Map.Entry<String, List<Student>> entry : studentMap.entrySet()) {
                ArrayAdapter<Student> listAdapter = new ArrayAdapter<Student>(getActivity(),
                        android.R.layout.simple_list_item_activated_1,
                        android.R.id.text1, entry.getValue());
                adapter.addSection(entry.getKey(), listAdapter);
            }
            setListAdapter(adapter);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private List<Student> getStudents() throws SQLException {		
		return dao.queryBuilder().selectColumns("id", "name").query();
	}
}
