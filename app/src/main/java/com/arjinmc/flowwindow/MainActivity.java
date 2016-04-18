package com.arjinmc.flowwindow;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btn1, btn2;


	private HomeReceiver homeReceiver;
	
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
				SPUtil.saveFlowWindowStatus(MainActivity.this,true);
				finish();
			}
		});
		
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
				stopService(intent);
				SPUtil.saveFlowWindowStatus(MainActivity.this,false);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(SPUtil.getFlowWindowStatus(this)){
			Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
			intent.setAction(FloatWindowService.STATUS_HIDE);
			startService(intent);
		}
		homeReceiver = new HomeReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(homeReceiver,filter);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if(homeReceiver!=null){
			unregisterReceiver(homeReceiver);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(SPUtil.getFlowWindowStatus(this)){
			Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
			intent.setAction(FloatWindowService.STATUS_SHOW);
			startService(intent);
		}

	}
}
