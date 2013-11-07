package edu.upenn.nettercenter.auni.cookcompetition.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.Score;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreField;

/**
 * @author siyusong
 *
 */
public class ScoreFieldAdapter extends BaseAdapter {
    private Context context;
    private Callbacks callbacks;
    private final List<ScoreField> scoreFields;
    private final List<? extends Score> scores;
    private LayoutInflater mInflater;

    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onScoreFieldChanged(ScoreField scoreField, int score);
    }


    public ScoreFieldAdapter(Context context, Callbacks callbacks,
                             List<ScoreField> scoreFields, List<? extends Score> scores) {
        this.context = context;
        this.callbacks = callbacks;
        this.scoreFields = scoreFields;
        this.scores = scores;

        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }

    /**
     * The number of items in the list is determined by the number of speeches
     * in our array.
     *
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        return scoreFields.size();
    }

    /**
     * Since the data comes from an array, just returning the index is sufficent
     * to get at the data. If we were using a more complex data structure, we
     * would return whatever object represents one row in the list.
     *
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        return scoreFields.get(position);
    }

    /**
     * Use the array index as a unique id.
     *
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Make a view to hold each row.
     *
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.score_item, null);
            if (convertView != null) {
                holder = new ViewHolder();
                holder.scoreFieldName =
                        (TextView) convertView.findViewById(R.id.score_field_name);
                holder.scoreCircle = (ImageView) convertView.findViewById(R.id.score_circle);
                holder.scoreSpinner =
                        (Spinner) convertView.findViewById(R.id.score_spinner);
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScoreField scoreField = scoreFields.get(position);
        Score score = getScore(scoreField);
        if (holder != null) {
            holder.scoreFieldName.setText(scoreField.getName());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    R.layout.score_spinner_item,
                    Arrays.asList("(N/A)", "Gold", "Silver", "Bronze")
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.scoreSpinner.setAdapter(adapter);

            GradientDrawable d = (GradientDrawable) holder.scoreCircle.getDrawable();
            if (score != null) {
                updateCircleColor(d, score.getScore());
                holder.scoreSpinner.setSelection(score.getScore());
            } else {
                clearCircleColor(d);
            }

            final ViewHolder finalHolder = holder;
            holder.scoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    GradientDrawable d = (GradientDrawable) finalHolder.scoreCircle.getDrawable();
                    updateCircleColor(d, i);
                    callbacks.onScoreFieldChanged((ScoreField) getItem(position), i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
        return convertView;
    }

    private void clearCircleColor(GradientDrawable d) {
        d.setColor(Color.TRANSPARENT);
    }

    private void updateCircleColor(GradientDrawable d, int i) {
        if (d != null) {
            if (i == 1) {
                d.setColor(Color.parseColor("#FFD700"));
            } else if (i == 2) {
                d.setColor(Color.parseColor("#C0C0C0"));
            } else if (i == 3) {
                d.setColor(Color.parseColor("#B8860B"));
            } else {
                clearCircleColor(d);
            }
        }
    }

    public Score getScore(ScoreField scoreField) {
        for (Score score : scores) {
            if (score.getScoreField().getId() == scoreField.getId()) {
                return score;
            }
        }
        return null;
    }

    static class ViewHolder {
        private TextView scoreFieldName;
        private ImageView scoreCircle;
        private Spinner scoreSpinner;
    }
}