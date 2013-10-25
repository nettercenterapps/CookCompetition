package edu.upenn.nettercenter.auni.cookcompetition.sections;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.OrmLiteDao;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.Role;
import edu.upenn.nettercenter.auni.cookcompetition.models.Student;

@EFragment(R.layout.fragment_today_detail)
public class TodayDetailFragment extends Fragment {

	@OrmLiteDao(helper = DatabaseHelper.class, model = Student.class)
	Dao<Student, Long> dao = null;

    @OrmLiteDao(helper = DatabaseHelper.class, model = Role.class)
    Dao<Role, Long> roleDao = null;

	@ViewById(R.id.student_name)
    TextView studentName;

    @ViewById(R.id.student_nickname)
    TextView studentNickName;

    @ViewById(R.id.team_name)
    TextView teamName;

    @ViewById(R.id.student_role)
    Spinner studentRole;

    /**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Student mItem;

	private List<Student> students;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TodayDetailFragment() {
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

	@AfterViews
	void refresh() {
        try {
            if (mItem != null) {
                studentName.setText(mItem.getName());
                studentNickName.setText(mItem.getNickname());
                if (mItem.getTeam() != null) {
                    teamName.setText(mItem.getTeam().getName());
                } else {
                    teamName.setText("No Team");
                }
                ArrayAdapter<Role> adapter;
                final List<Role> roles = getRoles();
                adapter = new ArrayAdapter<Role>(
                            getActivity(), R.layout.spinner_item, roles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                studentRole.setAdapter(adapter);

                if (mItem.getRole() == null) {
                    studentRole.setSelection(0);
                } else {
                    for (int i = 0; i < roles.size(); i++) {
                        Role role = roles.get(i);
                        if (mItem.getRole().getId() == role.getId()) {
                            studentRole.setSelection(i);
                        }
                    }
                }

                studentRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i == 0) {
                            mItem.setRole(null);
                        } else {
                            mItem.setRole(roles.get(i));
                        }
                        try {
                            dao.update(mItem);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private List<Role> getRoles() throws SQLException {
        List<Role> roles = roleDao.queryForAll();
        roles.add(0, Role.ROLE_ABSENT);
        return roles;
    }
}
