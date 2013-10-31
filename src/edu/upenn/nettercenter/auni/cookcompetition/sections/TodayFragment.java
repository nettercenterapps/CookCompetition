package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;

@EFragment(R.layout.fragment_today)
public class TodayFragment extends Fragment implements ManagementStudentListFragment.Callbacks {
    @OrmLiteDao(helper = DatabaseHelper.class, model = Event.class)
    Dao<Event, Long> eventDao = null;

    ManagementStudentListFragment listFragment;
    TodayDetailFragment detailFragment;

    @ViewById
    ViewSwitcher switcher;

    @ViewById
    EditText eventName;

    @ViewById
    Button createEvent;

    Long selectedItemId;

    @AfterViews
    void determineViewShown() {
        int index = Utils.getTodayEvent(eventDao) == null ? 0 : 1;
        switcher.setDisplayedChild(index);
        try {
            System.out.println(eventDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterViews
    void loadCreateEventView() {
        final String hintText = "Event on " + new SimpleDateFormat("MMM d, yyyy").format(new Date());
        eventName.setHint(hintText);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = eventName.getText().toString().trim();
                if (name.isEmpty()) {
                    name = hintText;
                }
                Event event = new Event(name);
                event.setDate(Utils.getDateOfToday());
                try {
                    eventDao.create(event);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                determineViewShown();
            }
        });
    }

    @AfterViews
    void loadFragments() {
        if (listFragment == null) {
            listFragment = new ManagementStudentListFragment_();
            listFragment.setGroupByTeam(true);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.list_container, listFragment)
                    .commit();
            listFragment.setCallbacks(this);
//            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume");
        if (listFragment != null) {
            listFragment.refreshList();
        }
    }

    @Override
    public void onItemSelected(Long id) {
        selectedItemId = id;

        Bundle arguments = new Bundle();
        arguments.putLong(TodayDetailFragment_.ARG_ITEM_ID, id);
        detailFragment = new TodayDetailFragment_();
        detailFragment.setArguments(arguments);
//        detailFragment.setTo
        getChildFragmentManager().beginTransaction()
                .replace(R.id.detail_container, detailFragment).commit();
    }
}
