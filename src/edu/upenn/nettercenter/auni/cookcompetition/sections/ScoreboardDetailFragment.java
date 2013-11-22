package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreField;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreFieldValue;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;

@EFragment
public class ScoreboardDetailFragment extends Fragment {

    @OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
    Dao<Student, Long> dao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentScore.class)
    Dao<StudentScore, Long> studentScoreDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = ScoreField.class)
    Dao<ScoreField, Long> scoreFieldDao = null;

    
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
//        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//        XYSeries series = new XYSeries("Student Score History");
        //for (int i = 0; i < 11; i++) {
        //    series.add(i,  i);
        //}
        
        
        HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("student_id", mItem.getId());
        List<StudentScore> records;
        try {
        	List<ScoreField> scoreFields = scoreFieldDao.queryForEq("type", ScoreField.FIELD_TYPE_STUDENT);
        	int maxScore = 0;
        	for (ScoreField scoreField : scoreFields) {
        		List<ScoreFieldValue> values = scoreField.getScoreFieldType().getValues();
        		ScoreFieldValue maxValue = values.get(0);
        		for (ScoreFieldValue scoreFieldValue : values) {
					if (scoreFieldValue.getValue() > maxValue.getValue()) {
						maxValue = scoreFieldValue;
					}
				} 
        		maxScore += maxValue.getValue();
			}
        	
            records = studentScoreDao.queryBuilder()
            		 	.where().eq("student_id", mItem.getId())
            		 	.query();
            
            LinkedHashMap<Date, Integer> scoreMap = new LinkedHashMap<Date, Integer>(); 
            for (StudentScore studentScore : records) {
            	List<ScoreFieldValue> values = studentScore.getScoreField().getScoreFieldType().getValues();
            	ScoreFieldValue value = values.get(studentScore.getScore() - 1);
            	Date eventDate = studentScore.getEvent().getDate();
            	if (!scoreMap.containsKey(eventDate)) {
            		scoreMap.put(eventDate, value.getValue());
            	} else {
            		scoreMap.put(eventDate, scoreMap.get(eventDate) + value.getValue());
            	}            	
			}
            
            if (scoreMap.size() <= 2) {
            	TextView textView = (TextView) rootView.findViewById(R.id.insufficient_data);
            	textView.setVisibility(View.VISIBLE);
            } else {
            	ArrayList<Date> dateList = new ArrayList<Date>();
            	dateList.addAll(scoreMap.keySet());
            	Collections.sort(dateList);
            	
            	ArrayList<String> dateLabelList = new ArrayList<String>();
                for (Date date : dateList) {
                	dateLabelList.add(Utils.getShortDate(date));
                }
                String[] dateLabels = new String[scoreMap.size()];
                dateLabelList.toArray(dateLabels);
            	
            	int i = 0;
                GraphViewData[] data = new GraphViewData[scoreMap.size()];
                for (Date date: dateList) {
            		data[i] = new GraphViewData(i, scoreMap.get(date));
    				i++;
                }
                
                int color = getResources().getColor(android.R.color.black);
                GraphViewSeries series = new GraphViewSeries(data);
                series.getStyle().color = getResources().getColor(android.R.color.holo_blue_light);
            	GraphView graphView = new LineGraphView(
            	      getActivity()
            	      , "Scores of " + mItem.getName()
            	);
            	graphView.addSeries(series);
                graphView.setManualYAxisBounds(maxScore, 0);
                graphView.getGraphViewStyle().setNumVerticalLabels(9);
                graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
                graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.chart_text_size));
                graphView.getGraphViewStyle().setVerticalLabelsColor(color);
                graphView.getGraphViewStyle().setHorizontalLabelsColor(color); 
                
                graphView.setHorizontalLabels(dateLabels);
                l.addView(graphView);
            }
                        	
        } catch (SQLException e) {
            e.printStackTrace();
        }

        
        
//        dataset.addSeries(series);
//        
//        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
//        XYSeriesRenderer r = new XYSeriesRenderer();
//        r.setColor(Color.RED);
//        renderer.addSeriesRenderer(r);
//        
//        l.addView(ChartFactory.getLineChartView(getActivity(), dataset, renderer));
//        
        return rootView;
    }
}
