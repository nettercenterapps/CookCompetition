package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;

@EFragment(R.layout.listview_with_padding)
public class SettingsFragment extends Fragment {

	private static Map<String, Class<? extends Activity>> activities = 
			new LinkedHashMap<String, Class<? extends Activity>>();
	
	static {
		activities.put("Data Backup & Restore", DataBackupActivity_.class);
	}

    @ViewById
    ListView list;

    @AfterViews
    void loadData() {
    	final List<String> names = new ArrayList<String>(activities.keySet());
    	list.setAdapter(new ArrayAdapter<String>(getActivity(),
        		android.R.layout.simple_list_item_1, names));
    	list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				String name = names.get(position);
				Class<?> activityClass = activities.get(name);
				if (activityClass != null) {
					startActivity(new Intent(getActivity(), activityClass));
				}
			}
		});
    }
}
