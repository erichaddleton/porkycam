package com.example.porkycam;

import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	
	private Timer camTimer;
	
	@Override
	public void onCreate(Bundle MainActivity) {
		super.onCreate(MainActivity);
		setContentView(R.layout.activity_main);
		final MediaPlayer mp = MediaPlayer.create(this, R.raw.camclick.mp3);

		camTimer = new Timer();
		camTimer.schedule(new TimerTask() {			
			@Override
			public void run() {
				TimerMethod();
			}
			
		}, 0, 1000);
	}
	
	private void TimerMethod()
	{
		//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.
		this.runOnUiThread(Timer_Tick);
	}
	
	
	private Runnable Timer_Tick = new Runnable() {
		public void run() {
		
			//This method runs in the same thread as the UI.    	       
		
			//Do something to the UI thread here
			mp.start();	
		
	
		}
	};
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void startCamera(View view) {
    	
    
    }
}
    

