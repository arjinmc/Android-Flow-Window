package com.arjinmc.flowwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

public class FloatWindowService extends Service implements OnTouchListener,OnClickListener{

	private FrameLayout mFloatLayout;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mLayoutParams;

	public static final String STATUS_SHOW = "STATUS_SHOW";
	public static final String STATUS_HIDE = "STATUS_HIDE";

	private float mTouchStartY = 50.0f;
	private float mTouchStartX = 0.0f;

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
			if(action.equals(STATUS_SHOW)){
				if(mFloatLayout==null){
					addFloatView();
				}
			}else if(action.equals(STATUS_HIDE)){
				removeFloatView();
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
	public void onClick(View v) {
		Intent mainItent = new Intent(FloatWindowService.this,MainActivity.class);
		mainItent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(mainItent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float y = event.getRawY() - 25;
		float x = event.getRawX() - 25;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchStartY = event.getY();
			mTouchStartX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			updateViewPosition(x,y);
			break;
		case MotionEvent.ACTION_UP:
			updateViewPosition(x,y);
			savePoint(x,y);
			break;
		}
		return false;
	}

	private void updateViewPosition(float x,float y) {
		mLayoutParams.y = (int) (y - mTouchStartY);
		mLayoutParams.x = (int) (x - mTouchStartX);
		mWindowManager.updateViewLayout(mFloatLayout, mLayoutParams);
	}


	private float getScreenWith(){
		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);
		return Float.valueOf(dm.widthPixels);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		removeFloatView();
	}
}
