package edu.upenn.nettercenter.auni.cookcompetition;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Environment;
import android.util.TypedValue;

import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import edu.upenn.nettercenter.auni.cookcompetition.models.Event;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;

public class Utils {
    
    private static File imageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "CookCompetition");
    
    public static void sortStudentsByName(List<Student> students) {
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                String name1 = student1.getName();
                String name2 = student2.getName();

                if (name1 == null && name2 == null) {
                    return 0;
                } else if (name1 == null) {
                    return -1;
                } else if (name2 == null) {
                    return 1;
                } else {
                    return name1.trim().compareToIgnoreCase(name2.trim());
                }
            }
        });
    }

    public static LinkedHashMap<String, List<Student>> partitionByName(List<Student> students) {
        sortStudentsByName(students);
        LinkedHashMap<String, List<Student>> result = new LinkedHashMap<String, List<Student>>();
        for (Student student : students) {
            String c = String.valueOf(student.getName().charAt(0));

            if (!result.containsKey(c)) {
                result.put(c, new ArrayList<Student>());
            }
            result.get(c).add(student);
        }
        return result;
    }

    public static LinkedHashMap<String, List<Student>> partitionByTeam(Context context, List<Student> students) {
        sortStudentsByName(students);
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                if (student1.getTeam() == null && student2.getTeam() == null) {
                    return 0;
                } else if (student1.getTeam() == null) {
                    return -1;
                } else if (student2.getTeam() == null) {
                    return 1;
                } else {
                    System.out.println(student1.getTeam().getName());
                    return student1.getTeam().getName().trim().compareToIgnoreCase(
                            student2.getTeam().getName().trim());
                }
            }
        });
        LinkedHashMap<String, List<Student>> result = new LinkedHashMap<String, List<Student>>();
        List<Student> studentsWithoutTeam = new ArrayList<Student>();
        for (Student student : students) {
            if (student.getTeam() != null) {
                String k = String.valueOf(student.getTeam().getName());

                if (!result.containsKey(k)) {
                    result.put(k, new ArrayList<Student>());
                }
                result.get(k).add(student);
            } else {
                studentsWithoutTeam.add(student);
            }
        }
        if (studentsWithoutTeam.size() > 0) {
            result.put(context.getString(R.string.no_team), studentsWithoutTeam);
        }
        return result;
    }

    public static Date getDateOfToday() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static String getShortDate(Date date) {
    	return new SimpleDateFormat("MM/dd", Locale.ENGLISH).format(date);
    }
    
    public static Event getTodayEvent(Dao<Event, Long> eventDao) {
        try {
            Date today = Utils.getDateOfToday();
            List<Event> events = eventDao.queryForEq("date", today);
            if (events.size() > 0) {
                return events.get(0);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Reference: http://stackoverflow.com/a/8113368/2230331
    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // The final scaling will be the bigger of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    public static File getImage(Team t) {
        if (t != null) {
            File f = new File(imageDir, t.getName() + ".jpg");  
            if(f.exists()) {
                return f;
            }
        }
        return null;
    }

    public static int dpToPixel(Context context, int dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()
        );
    }
}
