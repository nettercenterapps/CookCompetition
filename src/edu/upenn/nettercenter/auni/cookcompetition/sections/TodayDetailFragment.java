package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;
import edu.upenn.nettercenter.auni.cookcompetition.models.Role;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentRecord;

@EFragment(R.layout.fragment_today_detail)
public class TodayDetailFragment extends Fragment {

	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> dao = null;

    @OrmLiteDao(helper = DatabaseHelper.class, model = Role.class)
    Dao<Role, Long> roleDao = null;

    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentRecord.class)
    Dao<StudentRecord, Long> studentRecordDao = null;

    @OrmLiteDao(helper = DatabaseHelper.class, model = Event.class)
    Dao<Event, Long> eventDao = null;

    @ViewById(R.id.student_name)
    TextView studentName;

    @ViewById(R.id.student_nickname)
    TextView studentNickName;

    @ViewById(R.id.team_name)
    TextView teamName;

    @ViewById(R.id.student_role)
    Spinner studentRole;

    /**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Student student;

    private StudentRecord studentRecord;


    /**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TodayDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			try {
				student = dao.queryForId(getArguments().getLong(ARG_ITEM_ID));
                Event todayEvent = Utils.getTodayEvent(eventDao);

                HashMap<String, Object> args = new HashMap<String, Object>();
                args.put("student_id", student.getId());
                args.put("event_id", todayEvent.getId());
                List<StudentRecord> records = studentRecordDao.queryForFieldValues(args);
                if (records.size() > 0) {
                    studentRecord = records.get(0);
                } else {
                    studentRecord = new StudentRecord();
                    studentRecord.setStudent(student);
                    studentRecord.setEvent(todayEvent);
                }
            } catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@AfterViews
	void refresh() {
        try {
            if (student != null) {
                studentName.setText(student.getName());
                studentNickName.setText(student.getNickname());
                if (student.getTeam() != null) {
                    teamName.setText(student.getTeam().getName());
                } else {
                    teamName.setText("No Team");
                }
                ArrayAdapter<Role> adapter;
                final List<Role> roles = getRoles();
                adapter = new ArrayAdapter<Role>(
                            getActivity(), R.layout.spinner_item, roles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                studentRole.setAdapter(adapter);

                if (studentRecord.getRole() == null) {
                    studentRole.setSelection(0);
                } else {
                    for (int i = 0; i < roles.size(); i++) {
                        Role role = roles.get(i);
                        if (studentRecord.getRole().getId() == role.getId()) {
                            studentRole.setSelection(i);
                        }
                    }
                }

                studentRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            studentRecord.setRole(null);
                        } else {
                            studentRecord.setRole(roles.get(i));
                        }
                        try {
                            studentRecordDao.createOrUpdate(studentRecord);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private List<Role> getRoles() throws SQLException {
        List<Role> roles = roleDao.queryForAll();
        roles.add(0, Role.ROLE_ABSENT);
        return roles;
    }
}
