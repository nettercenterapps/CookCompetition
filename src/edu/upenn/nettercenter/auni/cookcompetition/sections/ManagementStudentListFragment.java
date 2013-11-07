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
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.adapters.SeparatedListAdapter;
import edu.upenn.nettercenter.auni.cookcompetition.adapters.TeamMemberAdapter;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;

@EFragment
public class ManagementStudentListFragment extends ListFragment {

    private boolean groupByTeam;
    @OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> dao;
    @OrmLiteDao(helper = DatabaseHelper.class, model = Team.class)
    Dao<Team, Long> teamDao;

	private Callbacks mCallbacks = sDummyCallbacks;
    private SeparatedListAdapter adapter;

    public void setGroupByTeam(boolean groupByTeam) {
        this.groupByTeam = groupByTeam;
    }

    /**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
        public void onStudentSelected(Student student);
        public void onTeamSelected(Team team);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onStudentSelected(Student student) {
		}

        @Override
        public void onTeamSelected(Team team) {
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
            Object object = adapter.getItem(position);
            if (object instanceof Student) {
                mCallbacks.onStudentSelected((Student) object);
            } else if (object instanceof String) {
                try {
                    List<Team> teamList = teamDao.queryForEq("name", object);
                    if (!teamList.isEmpty()) {
                        mCallbacks.onTeamSelected(teamList.get(0));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}

	@SuppressWarnings("ConstantConditions")
    public void refreshList() {
		try {
            List<Student> students = getStudents();
            LinkedHashMap<String,List<Student>> studentMap;
            if (groupByTeam) {
                studentMap = Utils.partitionByTeam(getActivity(), students);
                adapter = new TeamMemberAdapter(getActivity());
            } else {
                studentMap = Utils.partitionByName(students);
                adapter = new SeparatedListAdapter(getActivity());
            }
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

    public void setSelectedStudent(Student student) {
        int position = adapter.getPosition(student);
        if (position != -1) {
            System.out.println(position);
            getListView().setItemChecked(position, true);
            mCallbacks.onStudentSelected(student);
        }
    }

	private List<Student> getStudents() throws SQLException {
		return dao.queryForAll();
	}
}
