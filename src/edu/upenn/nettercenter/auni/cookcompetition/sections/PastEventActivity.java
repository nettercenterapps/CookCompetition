package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;

import edu.upenn.nettercenter.auni.cookcompetition.R;

@EActivity(R.layout.activity_main)
public class PastEventActivity extends Activity {

    public static final String ARG_EVENT_ID = "event_id";
    public static final String ARG_EVENT_NAME = "event_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long eventId = getIntent().getLongExtra(ARG_EVENT_ID, 0);
        String eventName = getIntent().getStringExtra(ARG_EVENT_NAME);
        if (eventId != 0) {
            if (getActionBar() != null) {
                getActionBar().setDisplayShowTitleEnabled(true);
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setTitle(eventName);
            }

            Fragment fragment = new TodayFragment_();
            Bundle args = new Bundle();
            args.putLong(ARG_EVENT_ID, eventId);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    @OptionsItem(android.R.id.home)
    void up() {
        finish();
    }
}
