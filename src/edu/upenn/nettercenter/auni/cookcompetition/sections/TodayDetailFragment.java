package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.DBMethods;
import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.adapters.ScoreFieldAdapter;
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;
import edu.upenn.nettercenter.auni.cookcompetition.models.Role;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreField;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentRecord;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;

@EFragment(R.layout.fragment_today_detail)
public class TodayDetailFragment extends Fragment implements ScoreFieldAdapter.Callbacks{

	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> dao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = Role.class)
    Dao<Role, Long> roleDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentRecord.class)
    Dao<StudentRecord, Long> studentRecordDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = Event.class)
    Dao<Event, Long> eventDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentScore.class)
    Dao<StudentScore, Long> studentScoreDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = ScoreField.class)
    Dao<ScoreField, Long> scoreFieldDao = null;

    @ViewById(R.id.student_name)
    TextView studentName;

    @ViewById(R.id.student_nickname)
    TextView studentNickName;

    @ViewById(R.id.team_name)
    TextView teamName;

    @ViewById(R.id.student_role)
    Spinner studentRole;

    @ViewById(R.id.score_list)
    ListView scoreList;

    @ViewById(R.id.score_list_total)
    TextView scoreListTotal;
    
    @ViewById(R.id.photo)
    ImageView image;
    
    /**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_EVENT_ID = "event_id";

    /**
	 * The dummy content this fragment is presenting.
	 */
	Student student;
    StudentRecord studentRecord;
    Event todayEvent;


    /**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TodayDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        if (getArguments() != null) {
            try {
                if (getArguments().containsKey(ARG_EVENT_ID)) {
                    todayEvent = eventDao.queryForId(getArguments().getLong(ARG_EVENT_ID));
                } else {
                    todayEvent = Utils.getTodayEvent(eventDao);
                }

                if (getArguments().containsKey(ARG_ITEM_ID)) {
                    student = dao.queryForId(getArguments().getLong(ARG_ITEM_ID));
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
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

	@AfterViews
	void refreshInfo() {
        try {
            if (student != null) {
                File imagePath = Utils.getImage(student.getTeam());
                if (imagePath != null && imagePath.exists()) {
                    image.setImageURI(Uri.parse(imagePath.getAbsolutePath()));
                }
                studentName.setText(student.getName());
                studentNickName.setText(student.getNickname());
                if (student.getTeam() != null) {
                    teamName.setText(student.getTeam().getName());
                } else {
                    teamName.setText(getString(R.string.no_team));
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

    @AfterViews
    void refreshScore() {
        try {
            List<StudentScore> scores = getScores();
            List<ScoreField> scoreFields = getScoreField();
            ListAdapter adapter = new ScoreFieldAdapter(getActivity(), this, scoreFields, scores);
            scoreList.setAdapter(adapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @AfterViews
    void updateTotalScore() {
    	int score = DBMethods.getTotalStudentScoreByEvent(
    			studentScoreDao, Arrays.asList(student), todayEvent);
    	scoreListTotal.setText(Utils.getPtsString(score));
    }

    private List<Role> getRoles() throws SQLException {
        List<Role> roles = roleDao.queryForAll();
        roles.add(0, Role.ROLE_ABSENT);
        return roles;
    }

    private List<StudentScore> getScores() throws SQLException {
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("student_id", student.getId());
        args.put("event_id", todayEvent.getId());
        return studentScoreDao.queryForFieldValues(args);
    }

    private List<ScoreField> getScoreField() throws SQLException {
        return scoreFieldDao.queryForEq("type", ScoreField.FIELD_TYPE_STUDENT);
    }

    @Override
    public void onScoreFieldChanged(ScoreField scoreField, int score) {
        try {
            HashMap<String, Object> args = new HashMap<String, Object>();
            args.put("student_id", student.getId());
            args.put("event_id", todayEvent.getId());
            args.put("scoreField_id", scoreField.getId());
            List<StudentScore> scores = studentScoreDao.queryForFieldValues(args);

            // N/A before
            if (scores.size() == 0) {
                // Do nothing in case of N/A -> N/A
                if (score != 0) {
                    StudentScore s = new StudentScore();
                    s.setStudent(student);
                    s.setEvent(todayEvent);
                    s.setScoreField(scoreField);
                    s.setScore(score);
                    studentScoreDao.create(s);
                }
            } else {
                StudentScore s = scores.get(0);
                s.setScore(score);
                studentScoreDao.update(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateTotalScore();
    }    
}
