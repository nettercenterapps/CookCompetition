
package edu.upenn.nettercenter.auni.cookcompetition;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.Menu;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;

import edu.upenn.nettercenter.auni.cookcompetition.sections.*;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity implements ActionBar.TabListener {

	private static Map<String, Class<?>> sections = new LinkedHashMap<String, Class<?>>();
	
	static {
		sections.put("Event", EventFragment_.class);
		sections.put("Management", ManagementFragment_.class);
	}

	@AfterViews
	void setUpActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (String name : sections.keySet()) {
			actionBar.addTab(actionBar.newTab()
					.setText(name)
					.setTabListener(this)
			);			
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {		
		try {
			Fragment fragment = (Fragment) sections.get(tab.getText()).newInstance();
			fragmentTransaction.replace(R.id.fragment_container, fragment);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

}
