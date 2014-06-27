package net.danielkvist.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

/**
 * This class handles Dropbox authentication, downloading and uploading files.
 * To start an authentication process, call initAuthentication and in an
 * onResume method you call resumeAuthentication.
 * 
 * @author Daniel Kvist
 * 
 */
public class DropboxHandler {
	private static final String APP_KEY = "";
	private static final String APP_SECRET = "";
	private static final String PREFERENCE_NAME = "receiptTrackerDropbox";
	private static final String PREFERENCE_KEY = "key";
	private static final String PREFERENCE_SECRET = "secret";
	private static final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	private DropboxAPI<AndroidAuthSession> dbAPI;
	private Context context;
	private String uploadedPath;
	private File downloadedFile;
	private AndroidAuthSession session;

	/**
	 * Constructor which sets up the basic parts of the api
	 * 
	 * @param context
	 */
	public DropboxHandler(Context context) {
		this.context = context;
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		dbAPI = new DropboxAPI<AndroidAuthSession>(session);
	}

	/**
	 * Starts the auth process
	 */
	public void initAuthentication() {
		dbAPI.getSession().startAuthentication(context);
	}

	/**
	 * Should be called from onResume when the user comes back from the auth
	 */
	public void resumeAuthentication() {
		if (dbAPI != null && dbAPI.getSession().authenticationSuccessful()) {
			try {
				dbAPI.getSession().finishAuthentication();
				AccessTokenPair tokens = dbAPI.getSession()
						.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);

				Setting setting = new Setting();
				setting.setName(Setting.SETTING_STORAGE);
				setting.setValue(Setting.SETTING_STORAGE_CLOUD);
				Communicator communicator = new Communicator(context);
				communicator.saveSetting(setting);
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}
	}

	/**
	 * Creates a new session with old keys
	 * 
	 * @return the new session
	 */
	public AndroidAuthSession newSession() {
		AccessTokenPair access = retreiveKeys();
		session = dbAPI.getSession();
		session.setAccessTokenPair(access);
		return session;
	}

	/**
	 * Remove auth tokens
	 */
	public void deAuthenticate() {
		clearKeys();
	}

	/**
	 * Check if current session is valid
	 * 
	 * @return true if the session is valid
	 */
	public boolean isValidSession() {
		return retreiveKeys() != null;
	}

	/**
	 * Triggers an async task to upload the file
	 * 
	 * @param pathToFile
	 */
	public void uploadFile(String pathToFile) {
		AsyncUploader worker = new AsyncUploader();
		worker.execute(pathToFile);
	}

	/**
	 * Triggers an async task to download the file
	 * 
	 * @param pathToFile
	 */
	public void downloadFile(String pathToFile) {
		AsyncDownloader worker = new AsyncDownloader();
		worker.execute(pathToFile);
	}

	/**
	 * Stores the keys and secret in share preferences. Note that this settings
	 * is device specific so we do not want it in the database in the future
	 * when syncing is going to be implemented.
	 * 
	 * @param key
	 * @param secret
	 */
	private void storeKeys(String key, String secret) {
		SharedPreferences myPrefs = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = myPrefs.edit();
		prefsEditor.putString(PREFERENCE_KEY, key);
		prefsEditor.putString(PREFERENCE_SECRET, secret);
		prefsEditor.commit();
	}

	/**
	 * Clears the keys out of shared prefs
	 */
	private void clearKeys() {
		SharedPreferences myPrefs = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = myPrefs.edit();
		prefsEditor.clear();
		prefsEditor.commit();
	}

	/**
	 * Retreives the keys or null
	 * 
	 * @return an AccessTokenPair with key and secret or null
	 */
	private AccessTokenPair retreiveKeys() {
		SharedPreferences myPrefs = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		String key = myPrefs.getString(PREFERENCE_KEY, null);
		String secret = myPrefs.getString(PREFERENCE_SECRET, null);
		if (key != null && secret != null) {
			return new AccessTokenPair(key, secret);
		}
		return null;
	}

	/**
	 * Uploads a file to Dropbox
	 * 
	 * @author Daniel Kvist
	 * 
	 */
	private class AsyncUploader extends AsyncTask<String, Integer, Entry> {
		@Override
		protected Entry doInBackground(String... params) {
			Entry entry = null;
			FileInputStream inputStream = null;
			String pathToFile = params[0];
			try {
				File file = new File(pathToFile);
				String fileName = file.getName();
				inputStream = new FileInputStream(file);
				entry = dbAPI.putFile("/" + fileName, inputStream,
						file.length(), null, null);
			} catch (DropboxUnlinkedException e) {
				Log.e("Dropbox", "User has unlinked.");
			} catch (DropboxException e) {
				Log.e("Dropbox",
						"Something went wrong while uploading: "
								+ e.getMessage());
			} catch (FileNotFoundException e) {
				Log.e("Dropbox", "File not found.");
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						Log.w("Dropbox", e.getMessage());
					}
				}
			}
			return entry;
		}

		@Override
		protected void onPostExecute(Entry result) {
			super.onPostExecute(result);
			if (result != null) {
				uploadedPath = result.path;
				Log.i("Dropbox", "The uploaded file's rev is: " + result.rev);
			}
		}
	}

	/**
	 * Downloads a file from Dropbox
	 * 
	 * @author Daniel Kvist
	 * 
	 */
	private class AsyncDownloader extends AsyncTask<String, Integer, File> {

		@Override
		protected File doInBackground(String... params) {
			File file = null;
			FileOutputStream outputStream = null;
			String pathToFile = params[0];
			try {
				file = new File(pathToFile);
				outputStream = new FileOutputStream(file);
				String fileName = file.getName();
				DropboxFileInfo info = dbAPI.getFile(fileName, null,
						outputStream, null);
				Log.i("DbExampleLog",
						"The file's rev is: " + info.getMetadata().rev);
			} catch (DropboxException e) {
				Log.e("DbExampleLog",
						"Something went wrong while downloading: "
								+ e.getMessage());
			} catch (FileNotFoundException e) {
				Log.e("DbExampleLog", "File not found.");
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return file;
		}

		@Override
		protected void onPostExecute(File result) {
			super.onPostExecute(result);
			downloadedFile = result;
		}

	}

}
