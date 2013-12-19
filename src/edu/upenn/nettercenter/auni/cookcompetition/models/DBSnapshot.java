package edu.upenn.nettercenter.auni.cookcompetition.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import edu.upenn.nettercenter.auni.cookcompetition.DatabaseHelper;
import edu.upenn.nettercenter.auni.cookcompetition.Utils;

public class DBSnapshot {

	private static final String METADATA_FILENAME = "metadata.json";
	private static final String DATABASE_FILENAME = "database.db";
	private static final String BACKUP_DIR = "CookCompetition_Backup";
	private static final String FILE_EXT = ".zip";

	public static void restoreFromSnapshot(Context context, String name) {
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		int currentDBVersion = databaseHelper.getReadableDatabase().getVersion(); 
		
		File tempDir = context.getCacheDir();
		File zipFile = new File(getFullPath(name));
		byte[] buffer = new byte[2048];
		try {
			ZipInputStream zipInputStream = new ZipInputStream(
					new FileInputStream(zipFile));
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while (zipEntry != null) {
				String filename = zipEntry.getName();
				File newFile = new File(tempDir, filename);
				FileOutputStream fileOutputStream = new FileOutputStream(
						newFile);
				int bytesRead;
				while ((bytesRead = zipInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, bytesRead);
				}
				fileOutputStream.close();
				zipEntry = zipInputStream.getNextEntry();
			}
			zipInputStream.closeEntry();
			zipInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File metadataFile = new File(tempDir, METADATA_FILENAME);
		File databaseFile = new File(tempDir, DATABASE_FILENAME);
		if (metadataFile.exists() && databaseFile.exists()) {
			try {
				JSONObject metadata = loadJSON(metadataFile);
				int dbVersion = metadata.getInt("dbVersion");
				if (metadata.getInt("dbVersion") == currentDBVersion) {
					File destFile = new File(databaseHelper.getReadableDatabase().getPath());
					Utils.copyFile(databaseFile, destFile);
				} else {
					throw new RuntimeException("Unsupported database version: " + dbVersion);
				}
			} catch (JSONException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException("Unable to copy database file.");
			}
		}

	}

	public static boolean deleteSnapshot(String name) {
		File file = new File(getFullPath(name));
		return file.delete();
	}

	public static String getFullPath(String name) {
		File backupDir = new File(Environment.getExternalStorageDirectory(),
				BACKUP_DIR);
		File file = new File(backupDir, name + FILE_EXT);
		return file.getAbsolutePath();
	}

	public static List<String> getSnapshotList() {
		File backupDir = new File(Environment.getExternalStorageDirectory(),
				BACKUP_DIR);
		List<String> filenames = new ArrayList<String>();
		if (backupDir.exists() && backupDir.isDirectory()) {
			for (File file : backupDir.listFiles()) {
				if (file.isFile() && file.getName().endsWith(FILE_EXT)) {
					filenames.add(file.getName().substring(0,
							file.getName().length() - FILE_EXT.length()));
				}
			}
		}
		return filenames;
	}

	public static void createSnapshot(Context context) {
		JSONObject metadata = getMetadata(context);

		File metadataFile = null;
		try {
			metadataFile = File.createTempFile("metadata", null,
					context.getCacheDir());
			FileOutputStream fileOutputStream = new FileOutputStream(
					metadataFile);
			fileOutputStream.write(metadata.toString().getBytes());
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String backupFilename = null;
		List<File> files = new ArrayList<File>();
		List<String> filenames = new ArrayList<String>();
		try {
			long timeStamp = metadata.getLong("time");
			backupFilename = "Backup_"
					+ new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
							.format(new Date(timeStamp)) + "_" + timeStamp
					+ FILE_EXT;

			files.add(new File(metadata.getString("originalPath")));
			filenames.add(DATABASE_FILENAME);
			if (metadataFile != null) {
				files.add(metadataFile);
				filenames.add(METADATA_FILENAME);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		File backupDir = new File(Environment.getExternalStorageDirectory(),
				BACKUP_DIR);
		if (!backupDir.exists()) {
			boolean success = backupDir.mkdirs();
			if (!success) {
				throw new RuntimeException("Failed to create directory: " + backupDir);
			}
		}

		byte[] buffer = new byte[2048];
		try {
			File zipFile = new File(backupDir, backupFilename);
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			ZipOutputStream zipOutputStream = new ZipOutputStream(
					fileOutputStream);

			for (int i = 0; i < files.size(); i++) {
				String filename = filenames.get(i);
				File file = files.get(i);

				ZipEntry zipEntry = new ZipEntry(filename);
				zipOutputStream.putNextEntry(zipEntry);

				FileInputStream in = new FileInputStream(file);
				int bytesRead;
				while ((bytesRead = in.read(buffer)) > 0) {
					zipOutputStream.write(buffer, 0, bytesRead);
				}
				in.close();
			}

			zipOutputStream.closeEntry();
			zipOutputStream.close();

			Log.i("CookCompetition", "Snapshot created: " + zipFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static JSONObject getMetadata(Context context) {
		JSONObject metadata = new JSONObject();

		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		try {
			metadata.put("originalPath", databaseHelper.getReadableDatabase()
					.getPath());
			metadata.put("dbVersion", databaseHelper.getReadableDatabase()
					.getVersion());
			metadata.put("time", System.currentTimeMillis());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return metadata;
	}

	private static JSONObject loadJSON(File file) throws JSONException {
		StringBuilder contents = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					contents.append(line);
					contents.append("\n");
				}
			} finally {
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new JSONObject(contents.toString());
	}
}
