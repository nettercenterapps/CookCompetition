package edu.upenn.nettercenter.auni.cookcompetition.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;

public class TeamPerformanceItemAdapter extends BaseAdapter {

	private final Context context;
	private final List<Event> events;
	private final List<Integer> scores;
	private final List<Integer> teamScores;
	private final List<Integer> studentScores;
	private final LayoutInflater inflater;

	public TeamPerformanceItemAdapter(Context context, List<Event> events,
			List<Integer> scores, List<Integer> teamScores,
			List<Integer> studentScores) {
		this.context = context;
		this.events = events;
		this.scores = scores;
		this.teamScores = teamScores;
		this.studentScores = studentScores;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return scores.size();
	}

	@Override
	public Object getItem(int position) {
		return scores.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.team_performance_item_single_line, null);
			if (convertView != null) {
				holder = new ViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.text1);
				holder.score = (TextView) convertView.findViewById(R.id.score);
				convertView.setTag(holder);
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (holder != null) {
			holder.text1.setText(events.get(position).getName());
			holder.score.setText(Utils.getScoreString(
					teamScores.get(position), studentScores.get(position)));
		}

		return convertView;
	}

	private static class ViewHolder {
		private TextView text1;
		private TextView score;
	}
}
