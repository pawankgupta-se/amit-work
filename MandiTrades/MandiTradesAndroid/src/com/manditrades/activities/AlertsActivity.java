package com.manditrades.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;

import com.manditrades.R;
import com.manditrades.fragments.CreateAlertFragment;
import com.manditrades.jsonwrapper.AlertList;

public class AlertsActivity extends FragmentActivity {

	private Context context;
	private SharedPreferences preferences;
	private AlertList alertList;
	private ListView alertsLV;
	private int position;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_alert);
		context = this;

		setFragment(position);
	}

	private void setFragment(int position) {
		Fragment fragment = null;
		fragment = new CreateAlertFragment();

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.frame_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
