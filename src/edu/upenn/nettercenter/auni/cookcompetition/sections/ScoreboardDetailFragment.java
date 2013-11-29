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
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
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
	private List<ScoreField> scoreFields;
	private Map<ScoreField, Map<Date, Integer>> scoreByFieldMap;
	private int maxScore;
	private LinkedHashMap<Date, Integer> scoreMap;

	private boolean showTotal = true;
	private List<ScoreField> seriesShown = new ArrayList<ScoreField>();
	
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
        
        initScoreData();
        refreshGraph(rootView);
        
        return rootView;
    }

	private void initScoreData() {
		HashMap<String, Object> args = new HashMap<String, Object>();
        args.put("student_id", mItem.getId());
        List<StudentScore> records;
        try {
        	scoreFields = scoreFieldDao.queryForEq("type", ScoreField.FIELD_TYPE_STUDENT);
        	scoreByFieldMap = new LinkedHashMap<ScoreField, Map<Date, Integer>>();
        	
        	maxScore = 0;        	
        	for (ScoreField scoreField : scoreFields) {
        		scoreByFieldMap.put(scoreField, new HashMap<Date, Integer>());
        		
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
            
            scoreMap = new LinkedHashMap<Date, Integer>(); 
            for (StudentScore studentScore : records) {
            	List<ScoreFieldValue> values = studentScore.getScoreField().getScoreFieldType().getValues();
            	ScoreFieldValue value = values.get(studentScore.getScore() - 1);
            	Date eventDate = studentScore.getEvent().getDate();
            	if (!scoreMap.containsKey(eventDate)) {
            		scoreMap.put(eventDate, value.getValue());
            	} else {
            		scoreMap.put(eventDate, scoreMap.get(eventDate) + value.getValue());
            	}
            	
            	scoreByFieldMap.get(studentScore.getScoreField()).put(
            			studentScore.getEvent().getDate(), value.getValue()
            	);
			}
            
                        	
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	public void refreshGraph() {
		refreshGraph(getView());
	}
	
	private void refreshGraph(View rootView) {
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
			
		    List<Map<Date, Integer>> dataMaps = new ArrayList<Map<Date, Integer>>();
		    List<String> dataMapNames = new ArrayList<String>();
		    dataMaps.add(scoreMap);
		    dataMapNames.add("Total");
		    for (ScoreField scoreField : seriesShown) {
		    	dataMaps.add(scoreByFieldMap.get(scoreField));
		    	dataMapNames.add(scoreField.getName());
		    }
		    
		    int[] seriesColors = new int[] {
		    	getResources().getColor(android.R.color.holo_blue_light),
		    	getResources().getColor(android.R.color.holo_green_light),
		    	getResources().getColor(android.R.color.holo_orange_light),
		    	getResources().getColor(android.R.color.holo_red_light),
		    	getResources().getColor(android.R.color.holo_purple),
		    	getResources().getColor(android.R.color.holo_blue_dark),
		    	getResources().getColor(android.R.color.holo_green_dark),
		    	getResources().getColor(android.R.color.holo_orange_dark),
		    	getResources().getColor(android.R.color.holo_red_dark),
		    	getResources().getColor(android.R.color.holo_blue_bright),
		    };
		    
		    List<GraphViewSeries> seriesList = new ArrayList<GraphViewSeries>();
		    for (int i = 0; i < dataMaps.size(); i++) {
		    	Map<Date, Integer> map = dataMaps.get(i);
		    	String mapName = dataMapNames.get(i);
		    	
				int k = 0;
			    List<GraphViewData> dataList = new ArrayList<GraphViewData>();
			    for (Date date: dateList) {
			    	if (map.containsKey(date) && map.get(date) > 0) {
			    		dataList.add(new GraphViewData(k, map.get(date)));
			    	}
					k++;
			    }
			    GraphViewData[] dataArray = new GraphViewData[dataList.size()];
			    dataList.toArray(dataArray);
			    
			    int color = seriesColors[i % seriesColors.length];
			    GraphViewSeries series = new GraphViewSeries(
			    		mapName,
			    		new GraphViewSeriesStyle(color, 2),
			    		dataArray);
			    seriesList.add(series);
		    }

		    GraphView graphView = new LineGraphView(
				      getActivity()
				      , "Scores of " + mItem.getName()
				);
		    
		    for (GraphViewSeries series : seriesList) {
		    	graphView.addSeries(series);
		    }
		    graphView.setManualYAxisBounds(maxScore, 0);
		    graphView.getGraphViewStyle().setNumVerticalLabels(9);
		    graphView.getGraphViewStyle().setGridColor(Color.LTGRAY);
		    graphView.getGraphViewStyle().setTextSize(getResources().getDimension(R.dimen.chart_text_size));
		    graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
		    graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK); 
		    graphView.setShowLegend(true);
            graphView.setLegendAlign(LegendAlign.BOTTOM);
            graphView.setLegendWidth(getResources().getDimension(R.dimen.chart_legend_width));
		    graphView.setHorizontalLabels(dateLabels);
		    
			LinearLayout l = (LinearLayout) rootView.findViewById(R.id.scoreboard_detail);		
		    l.removeAllViews();
		    l.addView(graphView);
		}
	}

	public void setSeriesShown(List<ScoreField> seriesShown) {
		this.seriesShown = seriesShown;
	}

	public void setShowTotal(boolean showTotal) {
		this.showTotal = showTotal;
	}
}
