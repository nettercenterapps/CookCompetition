
package edu.upenn.nettercenter.auni.cookcompetition;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.upenn.nettercenter.auni.cookcompetition.sections.ManagementFragment_;
import edu.upenn.nettercenter.auni.cookcompetition.sections.PastEventFragment_;
import edu.upenn.nettercenter.auni.cookcompetition.sections.ScoreboardFragment_;
import edu.upenn.nettercenter.auni.cookcompetition.sections.SettingsFragment_;
import edu.upenn.nettercenter.auni.cookcompetition.sections.TeamFragment_;
import edu.upenn.nettercenter.auni.cookcompetition.sections.TodayFragment_;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity implements ActionBar.TabListener {

	private static Map<String, Class<?>> sections = new LinkedHashMap<String, Class<?>>();
	
	static {
		sections.put("Today", TodayFragment_.class);
        sections.put("History", PastEventFragment_.class);
		sections.put("Student", ManagementFragment_.class);
		sections.put("Team", TeamFragment_.class);
		sections.put("Scoreboard", ScoreboardFragment_.class);
		sections.put("Settings", SettingsFragment_.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		if (intent != null && intent.getBooleanExtra("exit", false)) {
			finish();
		}
	}
	
	@AfterViews
	void setUpActionBar() {
		ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            for (String name : sections.keySet()) {
                actionBar.addTab(actionBar.newTab()
                        .setText(name)
                        .setTabListener(this)
                );
            }
        }
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        String fragmentName = sections.get(tab.getText().toString()).getName();
        Fragment fragment = getFragmentManager().findFragmentByTag(fragmentName);
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fragmentName);
            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentName);
        } else {
            fragmentTransaction.attach(fragment);
        }
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        String fragmentName = sections.get(tab.getText().toString()).getName();
        Fragment fragment = getFragmentManager().findFragmentByTag(fragmentName);
        if (fragment != null) {
            fragmentTransaction.detach(fragment);
        }
    }

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

}
