package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentRecord;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;
import android.os.Bundle;
import android.app.Fragment;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

@EFragment
public class ScoreboardDetailFragment extends Fragment {

    @OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
    Dao<Student, Long> dao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentScore.class)
    Dao<StudentScore, Long> studentScoreDao = null;
    
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
    public ScoreboardDetailFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_scoreboard_detail,
                container, false);

        LinearLayout l = (LinearLayout) rootView.findViewById(R.id.scoreboard_detail);
        
        // TODO: create show the graph
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        XYSeries series = new XYSeries("Student Score History");
        //for (int i = 0; i < 11; i++) {
        //    series.add(i,  i);
        //}
        
        
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("student_id", mItem.getId());
        List<StudentScore> records;
        try {
            records = studentScoreDao.queryForFieldValues(args);
            if (records.size() > 0) {
                int i = 0;
                for (StudentScore s : records) {
                    i ++;
                    series.add(s.getScore(), i);
                }
            } else {
                series.add(0, 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        
        
        dataset.addSeries(series);
        
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.RED);
        renderer.addSeriesRenderer(r);
        
        l.addView(ChartFactory.getLineChartView(getActivity(), dataset, renderer));
        
        return rootView;
    }
}
