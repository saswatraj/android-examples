package com.example.activityservicecommunication;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {
	
	private final IBinder mbinder = new MyBinder();
	private boolean isRanging = false;
	private boolean prevValue = false;
	private NotificationManager notificationManager;
	private static final int NOTIFICATION_ID = 123;
	
	public class MyBinder extends Binder{
		MyService getService(){
			return MyService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mbinder;
	}

	@Override
	public void onCreate() {
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		new Thread(){

			@Override
			public void run() {
				while(true){
					if(isRanging!=prevValue){
						postNotification("Rabging Value Changed");
						prevValue = isRanging;
					}
				}
			}

			
		}.start();
		super.onCreate();
	}
	
	public void setRanging(){
		isRanging = true;
	}
	
	public void unsetRanging(){
		isRanging = false;
	}
	
	private void postNotification(String msg) {
	    Intent notifyIntent = new Intent(MyService.this, MyService.class);
	    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    PendingIntent pendingIntent = PendingIntent.getActivities(
	    		MyService.this,
	        0,
	        new Intent[]{notifyIntent},
	        PendingIntent.FLAG_UPDATE_CURRENT);
	    Notification notification = new Notification.Builder(MyService.this)
	        .setSmallIcon(R.drawable.beacon_gray)
	        .setContentTitle("Notify Demo")
	        .setContentText(msg)
	        .setAutoCancel(true)
	        .setContentIntent(pendingIntent)
	        .build();
	    notification.defaults |= Notification.DEFAULT_SOUND;
	    notification.defaults |= Notification.DEFAULT_LIGHTS;
	    notificationManager.notify(NOTIFICATION_ID, notification);
	  }

}
