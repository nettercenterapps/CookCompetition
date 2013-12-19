package edu.upenn.nettercenter.auni.cookcompetition.sections;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import edu.upenn.nettercenter.auni.cookcompetition.MainActivity_;
import edu.upenn.nettercenter.auni.cookcompetition.R;
import edu.upenn.nettercenter.auni.cookcompetition.models.DBSnapshot;

@EActivity(R.layout.listview_with_padding)
@OptionsMenu(R.menu.data_backup)
public class DataBackupActivity extends Activity {

	@ViewById
	ListView list;
	private List<String> backupFileList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getActionBar() != null) {
			getActionBar().setDisplayShowTitleEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.data_backup_context_menu, menu);
		menu.setHeaderTitle("Snapshot");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		String name = backupFileList.get(info.position);

		switch (item.getItemId()) {
		case R.id.menu_restore_snapshot:
			loadSnapshot(name);
			return true;
		case R.id.menu_export_snapshot:
			String path = DBSnapshot.getFullPath(name);

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
			intent.setType("*/*");
			startActivity(Intent.createChooser(intent, "Export to..."));
			return true;
		case R.id.menu_delete_snapshot:
			DBSnapshot.deleteSnapshot(name);
			loadList();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@AfterViews
	@UiThread
	void loadList() {
		backupFileList = DBSnapshot.getSnapshotList();
		list.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, backupFileList));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				l.showContextMenuForChild(v);
			}
		});
		registerForContextMenu(list);
	}

	@OptionsItem(R.id.menu_create_db_snapshot)
	@Background
	void createSnapshot() {
		try {
			DBSnapshot.createSnapshot(this);
			loadList();
		} catch (RuntimeException e) {
			showError(e);
		}
	}

	@Background
	void loadSnapshot(String name) {
		try {
			DBSnapshot.restoreFromSnapshot(this, name);
			onLoadSnapshotFinished();
		} catch (RuntimeException e) {
			showError(e);
		}
	}

	@UiThread
	void showError(RuntimeException e) {
		Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
	}
	
	@UiThread
	void onLoadSnapshotFinished() {
		new AlertDialog.Builder(this)
				.setTitle("Database Restore")
				.setMessage(
						"Successfully restored the database from the snapshot. \n"
								+ "Please relaunch the application to finish the process. \n\n"
								+ "The application will now exit.")
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						exitApplication();
					}
				}).show();
	}

	void exitApplication() {
		Intent exitIntent = new Intent(this, MainActivity_.class);
		exitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		exitIntent.putExtra("exit", true);
		startActivity(exitIntent);
		finish();
	}

	@OptionsItem(android.R.id.home)
	void up() {
		finish();
	}
}
