package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.adapters.ScoreFieldAdapter;
import edu.upenn.nettercenter.auni.cookcompetition.adapters.StudentPerformanceItemAdapter;
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreField;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentRecord;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;
import edu.upenn.nettercenter.auni.cookcompetition.models.TeamScore;

@EFragment(R.layout.fragment_today_team_detail)
public class TodayTeamDetailFragment extends Fragment implements ScoreFieldAdapter.Callbacks {

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_EVENT_ID = "event_id";

    @OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
    Dao<Student, Long> studentDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = Team.class)
    Dao<Team, Long> teamDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = Event.class)
    Dao<Event, Long> eventDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentRecord.class)
    Dao<StudentRecord, Long> studentRecordDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentScore.class)
    Dao<StudentScore, Long> studentScoreDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = TeamScore.class)
    Dao<TeamScore, Long> teamScoreDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = ScoreField.class)
    Dao<ScoreField, Long> scoreFieldDao = null;

    @ViewById(R.id.team_name)
    TextView teamName;
    @ViewById(R.id.team_performance_list)
    ListView teamPerformanceList;
    @ViewById(R.id.score_list)
    ListView scoreList;

    Team team;
    Event todayEvent;

    public TodayTeamDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (getArguments() != null) {
                if (getArguments().containsKey(ARG_ITEM_ID)) {
                    team = teamDao.queryForId(getArguments().getLong(ARG_ITEM_ID));
                }
                if (getArguments().containsKey(ARG_EVENT_ID)) {
                    todayEvent = eventDao.queryForId(getArguments().getLong(ARG_EVENT_ID));
                } else {
                    todayEvent = Utils.getTodayEvent(eventDao);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterViews
    void refreshInfo() {
        if (team != null) {
            teamName.setText(team.getName());
            try {
                List<Student> students = studentDao.queryForEq("team_id", team.getId());
                List<Long> studentIds = new ArrayList<Long>();
                for (Student student : students) {
                    studentIds.add(student.getId());
                }
                List<StudentRecord> studentRecords = studentRecordDao.queryBuilder().where()
                        .eq("event_id", todayEvent.getId()).and().in("student_id", studentIds)
                        .query();
                List<StudentScore> studentScores = studentScoreDao.queryBuilder().where()
                        .eq("event_id", todayEvent.getId()).and().in("student_id", studentIds)
                        .query();
                StudentPerformanceItemAdapter adapter =
                        new StudentPerformanceItemAdapter(
                            getActivity(), students, studentRecords, studentScores);
                teamPerformanceList.setAdapter(adapter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterViews
    void refreshScore() {
        try {
            List<TeamScore> scores = getScores();
            List<ScoreField> scoreFields = getScoreField();
            ListAdapter adapter = new ScoreFieldAdapter(getActivity(), this, scoreFields, scores);
            scoreList.setAdapter(adapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<TeamScore> getScores() throws SQLException {
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("team_id", team.getId());
        args.put("event_id", todayEvent.getId());
        return teamScoreDao.queryForFieldValues(args);
    }

    private List<ScoreField> getScoreField() throws SQLException {
        return scoreFieldDao.queryForEq("type", ScoreField.FIELD_TYPE_TEAM);
    }

    @Override
    public void onScoreFieldChanged(ScoreField scoreField, int score) {
        try {
            HashMap<String, Object> args = new HashMap<String, Object>();
            args.put("team_id", team.getId());
            args.put("event_id", todayEvent.getId());
            args.put("scoreField_id", scoreField.getId());
            List<TeamScore> scores = teamScoreDao.queryForFieldValues(args);

            // N/A before
            if (scores.size() == 0) {
                // Do nothing in case of N/A -> N/A
                if (score != 0) {
                    TeamScore s = new TeamScore();
                    s.setTeam(team);
                    s.setEvent(todayEvent);
                    s.setScoreField(scoreField);
                    s.setScore(score);
                    teamScoreDao.create(s);
                }
            } else {
                TeamScore s = scores.get(0);
                s.setScore(score);
                teamScoreDao.update(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
