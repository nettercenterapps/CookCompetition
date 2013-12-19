package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;
import edu.upenn.nettercenter.auni.cookcompetition.models.Score;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreField;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreFieldValue;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreMap;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;
import edu.upenn.nettercenter.auni.cookcompetition.models.TeamScore;

@EFragment
public class ScoreboardDetailFragment extends Fragment {

    @OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
    Dao<Student, Long> studentDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = Team.class)
    Dao<Team, Long> teamDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = StudentScore.class)
    Dao<StudentScore, Long> studentScoreDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = TeamScore.class)
    Dao<TeamScore, Long> teamScoreDao = null;
    @OrmLiteDao(helper = DatabaseHelper.class, model = ScoreField.class)
    Dao<ScoreField, Long> scoreFieldDao = null;
    
    public static final String ARG_STUDENT_ID = "student_id";
    public static final String ARG_TEAM_ID = "team_id";

    Student student;
    Team team;
	List<ScoreField> scoreFields;
	int maxScore;
	private ScoreMap scoreMap;

	boolean showTotal = true;
	List<ScoreField> seriesShown = new ArrayList<ScoreField>();
	
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScoreboardDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (getArguments().containsKey(ARG_STUDENT_ID)) {
            	student = studentDao.queryForId(getArguments().getLong(ARG_STUDENT_ID));
            } else if (getArguments().containsKey(ARG_TEAM_ID)) {
            	team = teamDao.queryForId(getArguments().getLong(ARG_TEAM_ID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        try {
        	List<ScoreField> studentScoreFields = scoreFieldDao.queryForEq("type", ScoreField.FIELD_TYPE_STUDENT);
        	List<ScoreField> teamScoreFields = scoreFieldDao.queryForEq("type", ScoreField.FIELD_TYPE_TEAM);
        	int studentMaxScore = countMaxScore(studentScoreFields);
        	int teamMaxScore = countMaxScore(teamScoreFields);
        	
        	List<? extends Score> records;
        	if (student != null) {
        		records = studentScoreDao.queryBuilder()
        								 .where().eq("student_id", student.getId())
        								 .query();
                scoreMap = new ScoreMap(records);
                maxScore = studentMaxScore;
        	} else {
        		records = teamScoreDao.queryBuilder()
						 				 .where().eq("team_id", team.getId())
						 				 .query();
        		scoreMap = new ScoreMap(records);
        		maxScore = teamMaxScore;
        	}
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}

	private int countMaxScore(List<ScoreField> scoreFields) {
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
		return maxScore;
	}

	public void refreshGraph() {
		refreshGraph(getView());
	}
	
	private void refreshGraph(View rootView) {
		if (scoreMap.size() <= 2) {
			TextView textView = (TextView) rootView.findViewById(R.id.insufficient_data);
			textView.setVisibility(View.VISIBLE);
		} else {
			ArrayList<Event> eventList = new ArrayList<Event>();
			eventList.addAll(scoreMap.keySet());
			Collections.sort(eventList, new Comparator<Event>() {
				@Override
				public int compare(Event lhs, Event rhs) {
					return lhs.getDate().compareTo(rhs.getDate());
				}
			});
			
			ArrayList<String> dateLabelList = new ArrayList<String>();
		    for (Event event : eventList) {
		    	dateLabelList.add(Utils.getShortDate(event.getDate()));
		    }
		    String[] dateLabels = new String[scoreMap.size()];
		    dateLabelList.toArray(dateLabels);
			
		    List<Map<Event, Integer>> dataMaps = new ArrayList<Map<Event, Integer>>();
		    List<String> dataMapNames = new ArrayList<String>();
		    dataMaps.add(scoreMap);
		    dataMapNames.add("Total");
		    for (ScoreField scoreField : seriesShown) {
		    	dataMaps.add(scoreMap.getScoreMapByField(scoreField));
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
		    	Map<Event, Integer> map = dataMaps.get(i);
		    	String mapName = dataMapNames.get(i);
		    	
				int k = 0;
			    List<GraphViewData> dataList = new ArrayList<GraphViewData>();
			    for (Event event: eventList) {
			    	if (map.containsKey(event) && map.get(event) > 0) {
			    		dataList.add(new GraphViewData(k, map.get(event)));
			    	}
					k++;
			    }
			    GraphViewData[] dataArray = new GraphViewData[dataList.size()];
			    dataList.toArray(dataArray);
			    
			    int color = seriesColors[i % seriesColors.length];
			    GraphViewSeries series = new GraphViewSeries(
			    		mapName,
			    		new GraphViewSeriesStyle(color, getResources().getDimensionPixelSize(R.dimen.line_thickness)),
			    		dataArray);
			    seriesList.add(series);
		    }

		    String name;
		    if (student != null) {
		    	name = student.getName();
		    } else {
		    	name = team.getName();
		    }
		    GraphView graphView = new LineGraphView(
				      getActivity()
				      , "Scores of " + name
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
