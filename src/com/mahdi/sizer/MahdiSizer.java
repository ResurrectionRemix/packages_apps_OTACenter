/*=========================================================================
 *
 *  PROJECT:  SlimRoms
 *            Team Slimroms (http://www.slimroms.net)
 *
 *  COPYRIGHT Copyright (C) 2013 Slimroms http://www.slimroms.net
 *            All rights reserved
 *
 *  LICENSE   http://www.gnu.org/licenses/gpl-2.0.html GNU/GPL
 *
 *  AUTHORS:     fronti90
 *  DESCRIPTION: SlimSizer: manage your apps
 *
 *=========================================================================
 */
package com.mahdi.sizer;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mahdi.ota.R;

public class MahdiSizer extends Fragment {
	public class Deleter extends AsyncTask<String, String, Void> {

		private ProgressDialog progress;

		@Override
		protected Void doInBackground(String... params) {
			for (String appName : params) {
				try {
					dos.writeBytes("\n" + "rm -rf '" + systemPath + appName
							+ "'\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			super.onPreExecute();
			try {
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			progress.dismiss();
			showDialog(REBOOT_DIALOG, null, adapter, 0);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (dos == null) {
				try {
					superUser = new ProcessBuilder("su", "-c",
							"/system/xbin/ash").start();
					dos = new DataOutputStream(superUser.getOutputStream());
					dos.writeBytes("\n" + "mount -o remount,rw /system" + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			progress = new ProgressDialog(getView().getContext());
			progress.setTitle(getString(R.string.delete_progress_title));
			progress.setMessage(getString(R.string.delete_progress));
			progress.show();
		}
	}
	private final int STARTUP_DIALOG = 1;
	private final int DELETE_DIALOG = 2;
	private final int DELETE_MULTIPLE_DIALOG = 3;
	private final int REBOOT_DIALOG = 4;
	protected ArrayAdapter<String> adapter;
	private ArrayList<String> mSysApp;
	private boolean startup = true;
	public final String systemPath = "/system/app/";
	protected Process superUser;

	protected DataOutputStream dos;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		if (!prefs.getBoolean("firstTime", false)) {

			final AlertDialog.Builder alert = new AlertDialog.Builder(
					getActivity());

			alert.setMessage(R.string.sizer_message_startup)
					.setTitle(R.string.caution)
					.setCancelable(true)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// action for ok
									dialog.cancel();

									alert.show();
								}
							});
			SharedPreferences.Editor editor = prefs.edit();

			editor.putBoolean("firstTime", true);
			editor.commit();

		}

		// create arraylist of apps not to be removed
		final ArrayList<String> safetyList = new ArrayList<String>();
		safetyList.add("BackupRestoreConfirmation.apk");
		safetyList.add("CertInstaller.apk");
		safetyList.add("Contacts.apk");
		safetyList.add("ContactsProvider.apk");
		safetyList.add("DefaultContainerService.apk");
		safetyList.add("DownloadProvider.apk");
		safetyList.add("DrmProvider.apk");
		safetyList.add("MediaProvider.apk");
		safetyList.add("Mms.apk");
		safetyList.add("PackageInstaller.apk");
		safetyList.add("Phone.apk");
		safetyList.add("Settings.apk");
		safetyList.add("SettingsProvider.apk");
		safetyList.add("MahdiCenter.apk");
		safetyList.add("Superuser.apk");
		safetyList.add("SystemUI.apk");
		safetyList.add("TelephonyProvider.apk");

		// create arraylist from /system/app content
		File system = new File(systemPath);
		String[] sysappArray = system.list();
		mSysApp = new ArrayList<String>(Arrays.asList(sysappArray));

		// remove "apps not to be removed" from list and sort list
		mSysApp.removeAll(safetyList);
		Collections.sort(mSysApp);

		// populate listview via arrayadapter
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_multiple_choice, mSysApp);

		final ListView lv = (ListView) getView().findViewById(
				R.string.listsystem);
		lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		lv.setAdapter(adapter);

		// longclick an entry
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg4, long arg3) {
				// create deletion dialog
				String item2 = lv.getAdapter().getItem(arg4).toString();
				showDialog(DELETE_DIALOG, item2, adapter, 0);
				return false;
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.sizer_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mahdi_sizer, container, false);
		return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete:
			final ListView lv = (ListView) getView().findViewById(
					R.string.listsystem);
			lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
			lv.setAdapter(adapter);

			// check which items are selected
			String item1 = null;
			int itemCounter = 0;
			SparseBooleanArray checked = lv.getCheckedItemPositions();
			for (int i = lv.getCount() - 1; i > 0; i--) {
				if (checked.get(i)) {
					item1 = mSysApp.get(i);
					itemCounter++;
				}
			}
			if (item1 == null) {
				toast(getResources().getString(R.string.sizer_message_noselect));
			} else {
				showDialog(DELETE_MULTIPLE_DIALOG, item1, adapter, itemCounter);
			}
			return true;
		case R.id.save:
			selectDialog(mSysApp, adapter);
			return true;
		default:
			break;
		}

		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		showSuperuserRequest();
	}

	// mount /system as ro on close
	protected void onStop(Bundle savedInstanceState) throws IOException {
		try {
			dos.writeBytes("\n" + "mount -o remount,ro /system" + "\n");
			dos.writeBytes("\n" + "exit" + "\n");
			dos.flush();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public short sdAvailable() {
		// check if sdcard is available
		// taken from developer.android.com
		short mExternalStorageAvailable = 0;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = 2;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = 1;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = 0;
		}
		return mExternalStorageAvailable;
	}

	// profile select dialog
	private void selectDialog(final ArrayList<String> sysAppProfile,
			final ArrayAdapter<String> adapter) {
		AlertDialog.Builder select = new AlertDialog.Builder(getActivity());
		select.setItems(R.array.sizer_profile_array,
				new DialogInterface.OnClickListener() {
					@Override
					@SuppressWarnings("resource")
					public void onClick(DialogInterface dialog, int which) {
						// The 'which' argument contains the index position
						// of the selected item
						short state = sdAvailable();
						File path = new File(Environment
								.getExternalStorageDirectory() + "/Mahdi");
						File savefile = new File(path + "/sizer.stf");
						if (which == 0) {
							// load profile action
							if (state >= 1) {
								String profile;
								try {
									// read savefile and create arraylist
									profile = new Scanner(savefile, "UTF-8")
											.useDelimiter("\\A").next();
									ArrayList<String> profileState = new ArrayList<String>(
											Arrays.asList(profile.split(", ")));
									// create arraylist of unique entries in
									// sysAppProfile (currently installed apps)
									ArrayList<String> deleteList = new ArrayList<String>();
									for (String item : sysAppProfile) {
										if (!profileState.contains(item)) {
											deleteList.add(item);
										}
									}
									// delete all entries in deleteList
									ArrayList<String> itemsList = new ArrayList<String>();
									for (int i = deleteList.size() - 1; i > 0; i--) {
										String item = deleteList.get(i);
										itemsList.add(item);
										// remove list entry
										adapter.remove(item);
									}
									adapter.notifyDataSetChanged();
									new MahdiSizer.Deleter().execute(itemsList
											.toArray(new String[itemsList
													.size()]));
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
							} else {
								toast(getResources().getString(
										R.string.sizer_message_sdnoread));
							}
						} else if (which == 1) {
							// save profile action
							if (state == 2) {
								try {
									// create directory if it doesnt exist
									if (!path.exists()) {
										path.mkdirs();
									}
									// create string from arraylists
									String lists = sysAppProfile.toString();
									lists = lists.replace("][", ",");
									lists = lists.replace("[", "");
									lists = lists.replace("]", "");
									// delete savefile if it exists (overwrite)
									if (savefile.exists()) {
										savefile.delete();
									}
									// create savefile and output lists to it
									FileWriter outstream = new FileWriter(
											savefile);
									BufferedWriter save = new BufferedWriter(
											outstream);
									save.write(lists);
									save.close();
									// check for success
									if (savefile.exists()) {
										toast(getResources()
												.getString(
														R.string.sizer_message_filesuccess));
									} else {
										toast(getResources()
												.getString(
														R.string.sizer_message_filefail));
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								toast(getResources().getString(
										R.string.sizer_message_sdnowrite));
							}
						}
					}
				});
		select.show();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		showSuperuserRequest();
	}

	private void showDialog(int id, final String item,
			final ArrayAdapter<String> adapter, int itemCounter) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		if (id == DELETE_DIALOG) {
			alert.setMessage(R.string.sizer_message_delete)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// action for ok
									// call delete
									new MahdiSizer.Deleter().execute(item);
									// remove list entry
									adapter.remove(item);
									adapter.notifyDataSetChanged();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// action for cancel
									dialog.cancel();
								}
							});
		} else if (id == DELETE_MULTIPLE_DIALOG) {
			String message;
			if (itemCounter == 1) {
				message = getResources().getString(
						R.string.sizer_message_delete_multi_one);
			} else {
				message = getResources().getString(
						R.string.sizer_message_delete_multi);
			}
			alert.setMessage(message)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									final ListView lv = (ListView) getView()
											.findViewById(R.string.listsystem);
									ArrayList<String> itemsList = new ArrayList<String>();
									SparseBooleanArray checked = lv
											.getCheckedItemPositions();
									for (int i = lv.getCount() - 1; i > 0; i--) {
										if (checked.get(i)) {
											String appName = mSysApp.get(i);
											itemsList.add(appName);
											// remove list entry
											lv.setItemChecked(i, false);
											adapter.remove(appName);
										}
									}
									adapter.notifyDataSetChanged();
									new MahdiSizer.Deleter().execute(itemsList
											.toArray(new String[itemsList
													.size()]));
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// action for cancel
									dialog.cancel();
								}
							});
		} else if (id == REBOOT_DIALOG) {
			// create warning dialog
			alert.setMessage(R.string.reboot)
					.setTitle(R.string.caution)
					.setCancelable(true)
					.setPositiveButton(R.string.reboot_ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// action for ok
									try {
										dos.writeBytes("reboot");
										dos.flush();
										dos.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							})
					.setNegativeButton(R.string.reboot_cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// action for cancel
									dialog.cancel();
								}
							});
		}
		// show warning dialog
		alert.show();
	}

	private void showSuperuserRequest() {
		if (this.getUserVisibleHint() && adapter != null && startup) {
			showDialog(STARTUP_DIALOG, null, adapter, 0);
			startup = false;
		}
	}

	public void toast(String text) {
		// easy toasts for all!
		Toast toast = Toast.makeText(getView().getContext(), text,
				Toast.LENGTH_SHORT);
		toast.show();
	}
}
