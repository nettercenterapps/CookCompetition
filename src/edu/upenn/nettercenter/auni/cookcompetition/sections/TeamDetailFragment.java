package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

import edu.upenn.nettercenter.auni.cookcompetition.DBMethods;
import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;
import edu.upenn.nettercenter.auni.cookcompetition.adapters.TeamPerformanceItemAdapter;
import edu.upenn.nettercenter.auni.cookcompetition.models.Event;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.StudentScore;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;
import edu.upenn.nettercenter.auni.cookcompetition.models.TeamScore;

@EFragment
public class TeamDetailFragment extends Fragment {
	
	@OrmLiteDao(helper = DatabaseHelper.class, model = Team.class)
	Dao<Team, Long> dao = null;

	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> studentDao = null;

	@OrmLiteDao(helper = DatabaseHelper.class, model = StudentScore.class)
    Dao<StudentScore, Long> studentScoreDao = null;
	
	@OrmLiteDao(helper = DatabaseHelper.class, model = TeamScore.class)
    Dao<TeamScore, Long> teamScoreDao = null;
	
	@OrmLiteDao(helper = DatabaseHelper.class, model = Event.class)
    Dao<Event, Long> eventDao = null;

	ListView studentList;
    ImageView image;
    TextView totalScore;
    ListView recentActivitiesList;
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Team mItem;
	
	private List<Student> students;

    private File imageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "CookCompetition");

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TeamDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			try {
				mItem = dao.queryForId(getArguments().getLong(ARG_ITEM_ID));
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String imageFilePath = getPath(uri);

            if (!imageDir.exists()) {
                boolean success = imageDir.mkdirs();
                if (!success) {
                    Log.e("CookCompetiton", "Failed to create directory: " + imageDir);
                    return;
                }
            }

            try {
                File source = new File(imageFilePath);
                File destination = new File(imageDir, mItem.getName() + ".jpg");
                boolean success = true;
                if (destination.exists()) success = destination.delete();
                success = success && destination.createNewFile();
                FileOutputStream destFileOutputStream = new FileOutputStream(destination);
                if (source.exists() && success) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                    Bitmap newBitmap = Utils.scaleCenterCrop(bitmap, 450, 450);
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, destFileOutputStream);
                    destFileOutputStream.close();

                    image.setImageBitmap(newBitmap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPath(Uri uri) {
        String path = null;

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        if (path != null) return path;
        else return uri.getPath();
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.team_image_context_menu, menu);
		menu.setHeaderTitle("Team Image");
	}
    
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_change:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
			return true;
		case R.id.menu_remove:
            File file = new File(imageDir, mItem.getName() + ".jpg");
            file.delete();
			image.setImageResource(R.drawable.ic_contact_picture_180_holo_light);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_team_detail,
				container, false);

        image = (ImageView) rootView.findViewById(R.id.photo);
        image.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
            	v.showContextMenu();
            }
        });
        registerForContextMenu(image);

		studentList = (ListView) rootView.findViewById(R.id.student_list);
		recentActivitiesList = (ListView) rootView.findViewById(R.id.team_performance_list);
		
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.team_name))
					.setText(mItem.getName());

            File imagePath = new File(imageDir, mItem.getName() + ".jpg");
            if (imagePath.exists()) {
                //image has same name as team, so if image exists, set it to team
                image.setImageURI(Uri.parse(imagePath.getAbsolutePath()));
            }
            
            totalScore = (TextView) rootView.findViewById(R.id.total_score);
            reloadStudents();
        }

		studentList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Make short tap also be able to start the contextual action bar.
            // Note that the actual implementation is pretty shallow - this is because that
            // this onItemClick will only be called in case of [0 selected -> 1 selected].
            // Once there is at least one item selected, onItemClick event will be handled by
            // ActionMode in MultiChoiceModeListener.
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                studentList.setItemChecked(i, true);
            }
        });
		studentList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
			 @Override
			    public void onItemCheckedStateChanged(ActionMode mode, int position,
			                                          long id, boolean checked) {
			    }

			    @Override
			    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			        // Respond to clicks on the actions in the CAB
			        switch (item.getItemId()) {
			            case R.id.menu_select_delete:
			                try {
			                	 SparseBooleanArray checked = studentList.getCheckedItemPositions();
			                     for (int pos = 0; pos < checked.size(); pos++) {
                                     if(checked.valueAt(pos)) {
                                         Student student = students.get(pos);
                                         student.setTeam(null);
                                         studentDao.update(student);
			                         }
			                     }
								reloadStudents();
								mode.finish();
								return true;
							} catch (SQLException e) {
								e.printStackTrace();
							}
			            default:
			                return false;
			        }
			    }

			    @Override
			    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			        MenuInflater inflater = mode.getMenuInflater();
			        inflater.inflate(R.menu.select_menu, menu);
			        return true;
			    }

			    @Override
			    public void onDestroyActionMode(ActionMode mode) {
			    }

			    @Override
			    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			        return true;
			    }
			});
		
		return rootView;
	}
	
	void reloadStudents() {
		try {
			students = studentDao.queryBuilder().where().eq("team_id", mItem).query();
			studentList.setAdapter(new ArrayAdapter<Student>(getActivity(),
					android.R.layout.simple_list_item_multiple_choice,
					android.R.id.text1, students));
            
            int teamScore = DBMethods.getTotalTeamScore(teamScoreDao, mItem);
            int studentScore = DBMethods.getTotalStudentScore(studentScoreDao, students);
            totalScore.setText(Utils.getLongScoreString(teamScore, studentScore));
            
            List<Event> events = eventDao.queryBuilder().orderBy("date", false).limit(3L).query();
            List<Integer> teamScores = new ArrayList<Integer>();
            List<Integer> studentScores = new ArrayList<Integer>();
            List<Integer> scores = new ArrayList<Integer>();
            for (Event event : events) {
            	int eventTeamScore = DBMethods.getTotalTeamScoreByEvent(teamScoreDao, mItem, event);
            	int eventStudentScore = DBMethods.getTotalStudentScoreByEvent(studentScoreDao, students, event);
            	teamScores.add(eventTeamScore);
            	studentScores.add(eventStudentScore);
            	scores.add(eventTeamScore + eventStudentScore);
            }
            recentActivitiesList.setAdapter(
            		new TeamPerformanceItemAdapter(getActivity(), events, scores, teamScores, studentScores));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
