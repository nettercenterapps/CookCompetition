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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.Score;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreField;
import edu.upenn.nettercenter.auni.cookcompetition.models.ScoreFieldValue;

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
                holder.scoreFieldName = (TextView) convertView.findViewById(R.id.score_field_name);
                holder.scoreCircle = (ImageView) convertView.findViewById(R.id.score_circle);
                holder.scoreSpinner = (Spinner) convertView.findViewById(R.id.score_spinner);
                holder.scoreCheckBox = (CheckBox) convertView.findViewById(R.id.score_checkbox);
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ScoreField scoreField = scoreFields.get(position);
        Score score = getScore(scoreField);
        if (holder != null) {
            holder.scoreFieldName.setText(scoreField.getName());

            List<String> valueNames = new ArrayList<String>();
            if (scoreField.getScoreFieldType().getValues().size() > 1) {
                holder.scoreCheckBox.setVisibility(View.GONE);
                holder.scoreCircle.setVisibility(View.VISIBLE);
                holder.scoreSpinner.setVisibility(View.VISIBLE);

                valueNames.add("(N/A)");
                for (ScoreFieldValue scoreFieldValue : scoreField.getScoreFieldType().getValues()) {
                    valueNames.add(scoreFieldValue.getCaption());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        R.layout.score_spinner_item,
                        valueNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.scoreSpinner.setAdapter(adapter);

                GradientDrawable d = (GradientDrawable) holder.scoreCircle.getDrawable();
                if (score != null) {
                    updateCircleColor(d, scoreField, score.getScore());
                    holder.scoreSpinner.setSelection(score.getScore());
                } else {
                    updateCircleColor(d, scoreField, 0);
                }

                final ViewHolder finalHolder = holder;
                holder.scoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        GradientDrawable d = (GradientDrawable) finalHolder.scoreCircle.getDrawable();
                        updateCircleColor(d, scoreField, i);
                        callbacks.onScoreFieldChanged((ScoreField) getItem(position), i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            } else {
                holder.scoreSpinner.setVisibility(View.GONE);
                holder.scoreCircle.setVisibility(View.GONE);
                holder.scoreCheckBox.setVisibility(View.VISIBLE);
                boolean checked = score != null && score.getScore() != 0;
                holder.scoreCheckBox.setChecked(checked);
                holder.scoreCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        int score = b ? 1 : 0;
                        callbacks.onScoreFieldChanged((ScoreField) getItem(position), score);
                    }
                });
            }
        }
        return convertView;
    }

    private void updateCircleColor(GradientDrawable d, ScoreField scoreField, int i) {
        List<ScoreFieldValue> values = scoreField.getScoreFieldType().getValues();
        if (i > 0 && values.size() >= i) {
            d.setColor(values.get(i - 1).getColor());
        } else {
            d.setColor(Color.TRANSPARENT);
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
        private CheckBox scoreCheckBox;
    }
}