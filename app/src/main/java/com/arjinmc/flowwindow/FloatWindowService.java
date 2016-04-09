package com.arjinmc.flowwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

public class FloatWindowService extends Service implements OnTouchListener,View.OnClickListener{

	private FrameLayout mFloatLayout;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLayoutParams;
	private int delta;
	private Timer mTimer;

	public static final String STATUS_SHOW = "STATUS_SHOW";
	public static final String STATUS_HIDE = "STATUS_HIDE";

	private float mTouchStartY = 50.0f;
	private float mTouchStartX = 0.0f;

	private String currentAction;

	private final int MSG_SHOW = 0x201;
	private final int MSG_HIDE = 0x202;

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case MSG_HIDE:
					removeFloatView();
					break;
				case MSG_SHOW:
					addFloatView();
					break;
			}
		}
	};

	//save last position x,y
	private final String FLOAT_WINDOW_POSITION = "FLOAT_WINDOW_POSITION";
	private void savePoint(float x,float y){
		SharedPreferences sp = getSharedPreferences();
		SharedPreferences.Editor  ed = sp.edit();
		ed.putFloat("x",x);
		ed.putFloat("y",y);
		ed.commit();
	}

	private SharedPreferences getSharedPreferences(){
		return getApplicationContext().getSharedPreferences(FLOAT_WINDOW_POSITION,Context.MODE_PRIVATE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if(intent!=null){
			String action = intent.getAction();
			currentAction = action;
			if(action.equals(STATUS_SHOW)){
				if(mTimer == null){
					mTimer = new Timer();
					mTimer.schedule(new RefreshTask(), 0, 1000);
				}
				handler.sendEmptyMessage(MSG_SHOW);
			}else if(action.equals(STATUS_HIDE)){
				handler.sendEmptyMessage(MSG_HIDE);
				if(mTimer!=null){
					mTimer.cancel();
					mTimer = null;
				}
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	private void addFloatView(){
		if(mFloatLayout != null){
			return;
		}
		mLayoutParams = new WindowManager.LayoutParams();
		mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		mLayoutParams.type = LayoutParams.TYPE_PHONE;
		mTouchStartX = getSharedPreferences().getFloat("x",getScreenWith());
		mTouchStartY = getSharedPreferences().getFloat("y",50.0f);

		mLayoutParams.format = PixelFormat.RGBA_8888;
		mLayoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		mLayoutParams.gravity = Gravity.TOP|Gravity.LEFT;
		mLayoutParams.x = (int)mTouchStartX;
		mLayoutParams.y = (int)mTouchStartY;
		mLayoutParams.width = LayoutParams.WRAP_CONTENT;
		mLayoutParams.height = LayoutParams.WRAP_CONTENT;
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		mFloatLayout = (FrameLayout) inflater.inflate(R.layout.widget_flow_window, null);
		
		mWindowManager.addView(mFloatLayout, mLayoutParams);;

		delta = mFloatLayout.getLeft()-mFloatLayout.getRight()/4;
		mFloatLayout.setOnTouchListener(this);
		mFloatLayout.setOnClickListener(this);
	}
	
	private void removeFloatView() {
		if(mFloatLayout != null){
			mWindowManager.removeView(mFloatLayout);
			mFloatLayout = null;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float y = event.getRawY()- delta;
		float x = event.getRawX()- delta;
		int firstX = 0;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchStartY = event.getY();
			mTouchStartX = event.getX();

			firstX = (int) event.getX();

		case MotionEvent.ACTION_MOVE:
			updateViewPosition(x,y);

		case MotionEvent.ACTION_UP:

			int secondX = (int) event.getX();
			int distance = Math.abs(secondX-firstX);

			if (distance <= 80) {
				savePoint(x,y);
				return false;
			}else {
				updateViewPosition(x,y);
				savePoint(x,y);
				return true;
			}

		}
		return false;

	}

	private void updateViewPosition(float x,float y) {
		if(mFloatLayout!=null){
			mLayoutParams.y = (int) (y - mTouchStartY);
			mLayoutParams.x = (int) (x - mTouchStartX);
			mWindowManager.updateViewLayout(mFloatLayout, mLayoutParams);
		}
	}


	private float getScreenWith(){
		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		return Float.valueOf(dm.widthPixels);
	}


	class RefreshTask extends TimerTask {

		@Override
		public void run() {
			if(currentAction!=null && currentAction.equals(STATUS_SHOW)){
				handler.sendEmptyMessage(MSG_SHOW);
			}
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mTimer!=null){
			mTimer.cancel();
			mTimer = null;
		}
		removeFloatView();
	}

	@Override
	public void onClick(View v) {
		Intent mainItent = new Intent(FloatWindowService.this,MainActivity.class);
		mainItent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(mainItent);
	}
}
