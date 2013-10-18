package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsMenu;

import edu.upenn.nettercenter.auni.cookcompetition.R;

@EActivity(R.layout.activity_management_add_student)
@OptionsMenu(R.menu.activity_management_add_student)
public class ManagementAddStudentActivity extends Activity {

	@AfterViews
	void setUpActionBar() {
		ActionBar actionBar = getActionBar();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(R.layout.action_bar_add_student, null);
        View doneItem = customActionBarView.findViewById(R.id.menu_add_student_done);
        doneItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addStudentDone();
			}
		});
        
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView);
	}

	void addStudentDone() {
		finish();
	}
}
