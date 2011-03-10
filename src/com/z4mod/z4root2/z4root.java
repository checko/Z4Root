package com.z4mod.z4root2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.md.updator.R;

public class z4root extends Activity {

	Button rootbutton;
	TextView detailtext;
	public static final String PREFS_NAME = "z4rootprefs";
	public static final String PREFS_ADS = "AdsEnabled";
	public static final String PREFS_MODE = "rootmode";
	public static final int MODE_PERMROOT = 0;
	public static final int MODE_TEMPROOT = 1;
	public static final int MODE_UNROOT = 2;
	final static String VERSION = "1.3.0";
	boolean forceunroot = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.z4root);

		detailtext = (TextView) findViewById(R.id.detailtext);

		Intent i = new Intent(z4root.this, Phase1.class);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(PREFS_MODE, MODE_PERMROOT);
		editor.commit();
		startActivity(i);
		finish();
	}

	private void realUpdate() {

	}

}
