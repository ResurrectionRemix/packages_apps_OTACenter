package com.mahdi.mbq.app.main;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mahdi.center.AboutMahdi;
import com.mahdi.ota.MahdiOTA;
import com.mahdi.ota.R;
import com.mahdi.ota.settings.About;
import com.mahdi.sizer.MahdiSizer;

@SuppressLint("WorldReadableFiles")
public class AwesomeClass extends FragmentActivity {
	public static class CategoriesFragment extends Fragment {

		public static final String ARG_CATEGORY = "category";

		public CategoriesFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_categories,
					container, false);

			int i = getArguments().getInt(ARG_CATEGORY);

			String List = getResources()
					.getStringArray(R.array.MenuDrawerStuff)[i];

			getActivity().setTitle(List);

			return rootView;

		}
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);

			view.isHorizontalFadingEdgeEnabled();
		}
	}

	private static final int PERIOD = 2000;

	Context context;

	// Used later
	Intent intent;

	private long lastPressedTime;

	private String[] mCategories;

	private DrawerLayout mDrawerLayout;

	@SuppressWarnings("unused")
	// y
	private CharSequence mDrawerTitle;

	private ActionBarDrawerToggle mDrawerToggle;

	SharedPreferences mPreferences;

	private CharSequence mTitle;

	TextView textview;

	private ListView mDrawerList;

	Fragment one = new AboutMahdi();
	Fragment two = new MahdiOTA();
	Fragment three = new MahdiSizer();

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void onConfigurationChanged2(Configuration newConfig2) {
		try {
			super.onConfigurationChanged(newConfig2);
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

			}
		} catch (Exception ex) {
		}
	}

	@SuppressLint("CutPasteId")
	// wtf
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.aw_yeah);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mTitle = mDrawerTitle = getTitle();

		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mCategories = getResources().getStringArray(R.array.MenuDrawerStuff);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mCategories));

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);

		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(

		this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close)

		{
			@Override
			public void onDrawerClosed(View view) {

				Drawable icon = null;
				getActionBar().setIcon(icon);

				invalidateOptionsMenu();

			}

			@Override
			public void onDrawerOpened(View drawerView) {

				Drawable icon = null;
				getActionBar().setIcon(icon);

				invalidateOptionsMenu();

			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {

			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.mahdi_center, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			switch (event.getAction()) {
			case KeyEvent.ACTION_DOWN:
				if (event.getDownTime() - lastPressedTime < PERIOD) {
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"Press again to exit.", Toast.LENGTH_SHORT).show();
					lastPressedTime = event.getEventTime();
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected(item)) {

			return true;
		}
		switch (item.getItemId()) {

		case android.R.id.home:
			super.onBackPressed();
			// ^ Yeah, it's THAT simple dude. ;)
			break;

		case R.id.ab_about:
			Intent intentAbout = new Intent(this, About.class);
			startActivity(intentAbout);
			break;

		default:

		}
		;

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	private void selectItem(int position) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		switch (position) {

		case 0:
			ft.replace(R.id.content_frame, one);
			break;

		case 1:
			ft.replace(R.id.content_frame, two);
			break;

		case 2:
			ft.replace(R.id.content_frame, three);
			break;

		}

		ft.commit();

		mDrawerList.setItemChecked(position, true);

		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
}
