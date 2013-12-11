package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

import edu.upenn.nettercenter.auni.cookcompetition.DBMethods;
import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.adapters.StudentActivityItemAdapter;
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentRecord;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;

/**
 * A fragment representing a single Student detail screen. This fragment is
 * either contained in a {@link StudentListActivity} in two-pane mode (on
 * tablets) or a {@link StudentDetailActivity} on handsets.
 */
@EFragment(R.layout.fragment_student_detail)
public class ManagementStudentDetailFragment extends Fragment {

    @OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
    Dao<Student, Long> dao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentScore.class)
    Dao<StudentScore, Long> studentScoreDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentRecord.class)
    Dao<StudentRecord, Long> studentRecordDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = Event.class)
    Dao<Event, Long> eventDao = null;

    
    @ViewById
    ImageView photo;    
    @ViewById
    TextView studentName;
    @ViewById
    TextView studentNickname;
    @ViewById(R.id.is_active)
    TextView isActive;
    @ViewById
    TextView teamName;
    @ViewById
    TextView totalScore;
    @ViewById
    TextView achievement;
    @ViewById
    ListView activityList;
    
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

    @AfterViews
    void loadStudent() {
    	if (mItem != null) {
            File imagePath = Utils.getImage(mItem.getTeam());
            if (imagePath != null && imagePath.exists()) {
                photo.setImageURI(Uri.parse(imagePath.getAbsolutePath()));
            }
            
            studentName.setText(mItem.getName());
            if (mItem.getNickname() != null) {
            	studentNickname.setText(mItem.getNickname());
            }
            if (mItem.isActive()) {
                isActive.setText("Active");                
            } else {
            	isActive.setText("Inactive");                                
            }
            if (mItem.getTeam() != null) {
                teamName.setText(mItem.getTeam().getName());
            } else {
                teamName.setText(getString(R.string.no_team));
            }
            int score = DBMethods.getTotalStudentScore(studentScoreDao, Arrays.asList(mItem));
            totalScore.setText(Utils.getLongScoreString(0, score));
            achievement.setText("x " + score / Utils.BADGE_POINT_RATIO);
            
            try {
            	List<Event> events = eventDao.queryBuilder().orderBy("date", false).query();
                List<StudentRecord> studentRecords = studentRecordDao.queryBuilder().where()
                        .eq("student_id", mItem.getId())
                        .query();
                List<StudentScore> studentScores = studentScoreDao.queryBuilder().where()
                        .eq("student_id", mItem.getId())
                        .query();
                StudentActivityItemAdapter adapter =
                        new StudentActivityItemAdapter(
                            getActivity(), mItem, events, studentRecords, studentScores);
                activityList.setAdapter(adapter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
