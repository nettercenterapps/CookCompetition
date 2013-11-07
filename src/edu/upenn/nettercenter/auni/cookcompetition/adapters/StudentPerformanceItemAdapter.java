package edu.upenn.nettercenter.auni.cookcompetition.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentRecord;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;

public class StudentPerformanceItemAdapter extends BaseAdapter {

    private final Context context;
    private final List<Student> students;
    private final List<StudentRecord> studentRecords;
    private final List<StudentScore> studentScores;
    private final LayoutInflater inflater;

    public StudentPerformanceItemAdapter(Context context,
                                         List<Student> students,
                                         List<StudentRecord> studentRecords,
                                         List<StudentScore> studentScores) {
        this.context = context;
        this.students = students;
        this.studentRecords = studentRecords;
        this.studentScores = studentScores;
        inflater = LayoutInflater.from(context);

        Utils.sortStudentsByName(this.students);
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.student_performance_item, null);
            if (convertView != null) {
                holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(R.id.text2);
                holder.scoreCircleContainer = (LinearLayout) convertView.findViewById(R.id.score_circle_container);
                convertView.setTag(holder);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder != null) {
            Student student = students.get(position);
            StudentRecord studentRecord = findStudentRecord(student);
            String role = "Absent";
            if (studentRecord != null && studentRecord.getRole() != null) {
                role = studentRecord.getRole().getName();
            }
            holder.text1.setText(student.getName());
            holder.text2.setText(role);

            List<StudentScore> studentScoreList = findStudentScore(student);
            System.out.println(student);
            System.out.println(studentScoreList);
            holder.scoreCircleContainer.removeAllViews();
            for (StudentScore studentScore : studentScoreList) {
                if (studentScore.getScore() != 0) {
                    ImageView circle = new ImageView(context);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    circle.setLayoutParams(layoutParams);
                    circle.setPadding(Utils.dpToPixel(context, 2), 0, 0, 0);
                    circle.setImageResource(R.drawable.circle);
                    GradientDrawable d = (GradientDrawable) circle.getDrawable();
                    updateCircleColor(d, studentScore.getScore());
                    holder.scoreCircleContainer.addView(circle);
                }
            }
        }

        return convertView;
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
                d.setColor(Color.TRANSPARENT);
            }
        }
    }

    private StudentRecord findStudentRecord(Student student) {
        for (StudentRecord studentRecord : studentRecords) {
            if (studentRecord.getStudent().getId().equals(student.getId())) return studentRecord;
        }
        return null;
    }

    private List<StudentScore> findStudentScore(Student student) {
        List<StudentScore> result = new ArrayList<StudentScore>();
        for (StudentScore studentScore : studentScores) {
            if (studentScore.getStudent().getId().equals(student.getId())) result.add(studentScore);
        }
        return result;
    }

    private static class ViewHolder {
        private TextView text1;
        private TextView text2;
        private LinearLayout scoreCircleContainer;
    }
}
