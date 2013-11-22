package edu.upenn.nettercenter.auni.cookcompetition.sections;

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
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;

@EFragment(R.layout.fragment_past_event)
public class PastEventFragment extends Fragment {

    @OrmLiteDao(helper = DatabaseHelper.class, model = Event.class)
    Dao<Event, Long> eventDao;

    @ViewById
    ListView eventList;

    @ViewById
    TextView textNoPastEvent;

    List<Event> pastEvents;

    @AfterViews
    void loadData() {
        try {
            List<Event> events = eventDao.queryBuilder().orderBy("date", false).query();
            Event todayEvent = Utils.getTodayEvent(eventDao);
            pastEvents = new ArrayList<Event>();
            for (Event event : events) {
                if (!event.equals(todayEvent)) pastEvents.add(event);
            }
            if (!pastEvents.isEmpty()) {
                textNoPastEvent.setVisibility(View.GONE);
                ArrayAdapter<Event> adapter = new ArrayAdapter<Event>(
                        getActivity(), android.R.layout.simple_list_item_1, pastEvents
                );
                eventList.setAdapter(adapter);

                eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Event event = pastEvents.get(i);
                        Intent intent = new Intent(getActivity(), PastEventActivity_.class);
                        intent.putExtra(PastEventActivity_.ARG_EVENT_ID, event.getId());
                        intent.putExtra(PastEventActivity_.ARG_EVENT_NAME, event.getName());
                        startActivity(intent);
                    }
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
