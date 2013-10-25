package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Fragment;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;

import edu.upenn.nettercenter.auni.cookcompetition.R;

@EFragment(R.layout.twopane)
public class TodayFragment extends Fragment implements ManagementStudentListFragment.Callbacks {
    ManagementStudentListFragment listFragment;
    TodayDetailFragment detailFragment;

    Long selectedItemId;

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
        getChildFragmentManager().beginTransaction()
                .replace(R.id.detail_container, detailFragment).commit();
    }
}
