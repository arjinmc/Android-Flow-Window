package com.arjinmc.flowwindow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btn1, btn2;

	private final String FLOW_WINDOW_STATUS = "FLOW_WINDOW_STATUS";

	private void saveFlowWindowStatus(boolean isOpen){
		SharedPreferences sharedPreferences = getSharedPreferences(FLOW_WINDOW_STATUS,MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("isOpen",isOpen);
		editor.commit();
	}

	private boolean getFlowWindowStatus(){
		SharedPreferences sharedPreferences = getSharedPreferences(FLOW_WINDOW_STATUS,MODE_PRIVATE);
		return sharedPreferences.getBoolean("isOpen",false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		
		btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
				intent.setAction(FloatWindowService.STATUS_SHOW);
				startService(intent);
				saveFlowWindowStatus(true);
				finish();
			}
		});
		
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
				stopService(intent);
				saveFlowWindowStatus(false);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(getFlowWindowStatus()){
			Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
			intent.setAction(FloatWindowService.STATUS_HIDE);
			startService(intent);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(getFlowWindowStatus()){
			Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
			intent.setAction(FloatWindowService.STATUS_SHOW);
			startService(intent);
		}

	}
}
