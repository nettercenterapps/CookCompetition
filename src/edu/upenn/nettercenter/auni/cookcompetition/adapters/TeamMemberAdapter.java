package edu.upenn.nettercenter.auni.cookcompetition.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import edu.upenn.nettercenter.auni.cookcompetition.R;

public class TeamMemberAdapter extends SeparatedListAdapter {

    public TeamMemberAdapter(Context context) {
        super(context);
        headers = new ArrayAdapter<String>(context, R.layout.team_list_header);
    }

    @Override
    public boolean isEnabled(int position) {
        if (getItemViewType(position) != TYPE_SECTION_HEADER) return true;
        String header = (String) getItem(position);
        String headerString = context.getString(R.string.no_team);
        return !header.equals(headerString);
    }
}
