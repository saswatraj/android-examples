package com.example.activityservicecommunication;

import com.example.activityservicecommunication.MyService.MyBinder;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private final String serviceName = "com.example.activityservicecommunication.MyService";
	private boolean isRanging = false;
	
	MyService myservice; //stores the current instance of the Service
	boolean isBound = false; //value to check if the current instance is present
	
	/**
	 * This connection is given which assigns our service class with the instance of the 
	 * running service when the activity binds to the service and sets the value of the bound
	 * variable as true.
	 */
	private ServiceConnection myConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder binder = (MyBinder) service;
			myservice = binder.getService();
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
		}
		
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isMonitoringServiceRunning()){
        	//donot fire up
        	Toast.makeText(getApplicationContext(), "Service already running", Toast.LENGTH_SHORT).show();
        }else{
        	//fire up the service
        	Toast.makeText(getApplicationContext(), "Starting Service", Toast.LENGTH_SHORT).show();
        	Intent serviceIntent = new Intent(getApplicationContext(),MyService.class);
        	bindService(serviceIntent,myConnection,Context.BIND_AUTO_CREATE);
        }
        //simulate the changing values of the variables
        new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				isRanging = true;
				if(isBound){
					Toast.makeText(getApplicationContext(), "You should get Notifcation now", Toast.LENGTH_SHORT).show();
					myservice.setRanging();
				}
				new Handler().postDelayed(new Runnable(){

					@Override
					public void run() {
						isRanging = false;
						if(isBound){
							Toast.makeText(getApplicationContext(), "You should get Notifcation now", Toast.LENGTH_SHORT).show();
							myservice.unsetRanging();
						}
					}
					
				}, 5000);
			}
        	
        }, 5000);
    }
    

	@Override
	protected void onStop() {
		super.onStop();
		if(isBound){
			unbindService(myConnection);
			isBound = false;
		}
	}




	private boolean isMonitoringServiceRunning(){
    	ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
    	for(RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE)){
    		Log.d("MESSAGE",service.service.getClassName());
    		if(serviceName.equals(service.service.getClassName())){
    			return true;
    		}
    	}
    	return false;
    }
}
