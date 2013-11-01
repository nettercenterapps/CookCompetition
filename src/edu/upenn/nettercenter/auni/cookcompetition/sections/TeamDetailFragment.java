package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;
import edu.upenn.nettercenter.auni.cookcompetition.models.Team;

@EFragment
public class TeamDetailFragment extends Fragment {
	
	@OrmLiteDao(helper = DatabaseHelper.class, model = Team.class)
	Dao<Team, Long> dao = null;

	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> studentDao = null;

	ListView studentList;

    ImageView photo;
	
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
                File destination = new File(imageDir, mItem.getName());
                System.out.println("Copying " + source.getAbsolutePath() + " to " +
                        destination.getAbsolutePath());
                if (source.exists()) {
                    copyFile(source, destination);
                    ImageView image = ((ImageView) getView().findViewById(R.id.photo));
                    image.setImageURI(Uri.parse(destination.getAbsolutePath()));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyFile(File source, File destination) throws IOException {
        FileChannel src = new FileInputStream(source).getChannel();
        FileChannel dst = new FileOutputStream(destination).getChannel();
        dst.transferFrom(src, 0, src.size());
        src.close();
        dst.close();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_team_detail,
				container, false);

        ImageView image = (ImageView) rootView.findViewById(R.id.photo);
        image.setOnClickListener(new ImageView.OnClickListener() {
        	@Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 1);
            }
        });

		studentList = (ListView) rootView.findViewById(R.id.student_list);

		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.team_name))
					.setText(mItem.getName());

            File imagePath = new File(imageDir, mItem.getName());
            if (imagePath.exists()) {
                //image has same name as team, so if image exists, set it to team
                image.setImageURI(Uri.parse(imagePath.getAbsolutePath()));
            }
        }

		studentList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		studentList.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			 @Override
			    public void onItemCheckedStateChanged(ActionMode mode, int position,
			                                          long id, boolean checked) {
			        // Here you can do something when items are selected/de-selected,
			        // such as update the title in the CAB
			    }

			    @Override
			    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			        // Respond to clicks on the actions in the CAB
			        switch (item.getItemId()) {
			            case R.id.menu_select_delete:
			               // deleteSelectedItems();
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
			        // Inflate the menu for the CAB
			        MenuInflater inflater = mode.getMenuInflater();
			        inflater.inflate(R.menu.select_menu, menu);
			        return true;
			    }

			    @Override
			    public void onDestroyActionMode(ActionMode mode) {
			        // Here you can make any necessary updates to the activity when
			        // the CAB is removed. By default, selected items are deselected/unchecked.
			    }

			    @Override
			    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			        // Here you can perform updates to the CAB due to
			        // an invalidate() request
			        return false;
			    }
			});
		
		return rootView;
	}
	
	@AfterViews
	void reloadStudents() {
		try {
			students = studentDao.queryBuilder().where().eq("team_id", mItem).query();
			studentList.setAdapter(new ArrayAdapter<Student>(getActivity(),
					android.R.layout.simple_list_item_activated_1,
					android.R.id.text1, students));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
