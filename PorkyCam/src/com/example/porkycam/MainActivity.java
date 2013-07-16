package com.example.porkycam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.*;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	TimerTask mTimerTask;
	final Handler handler = new Handler();
	Timer t = new Timer();
	TextView hTextView;
	TableRow hTableRow;
	Button hButton, hButtonStop;
	private int nCounter = 0;
	MediaPlayer _shootMP = null;
	private Camera mCamera;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		hTextView = (TextView)findViewById(R.id.textView2);
		hButton = (Button)findViewById(R.id.toggleButton1);
		mCamera = getCameraInstance();
	}
	
	//Get instance of camera object
	public static Camera getCameraInstance() {
		Camera c =null;
		try {
			c = Camera.open(); //attempt to get instance
		}
		catch (Exception e) {
			// Cam unavailable
		}
		return c; //return null if unavailable
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onToggleClick(View view){
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            doTimerTask();
        } else {
            stopTask();
        }
    }
    
    private PictureCallback mPicture = new PictureCallback() {

	    
		@Override
	    public void onPictureTaken(byte[] data, Camera camera) {

	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            Log.d("PorkyCam", "Error creating media file, check storage permissions: ");
	            return;
	        }

	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	        } catch (FileNotFoundException e) {
	            Log.d("PorkyCam", "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d("PorkyCam", "Error accessing file: " + e.getMessage());
	        }
	    }
	};
   
	public void doTimerTask(){
    	Log.d("TIMER","TimerTask Started");
    	
    	mTimerTask = new TimerTask() {
    		public void run() {
    			handler.post(new Runnable() {
    				public void run() {
    					nCounter++;
    					shootSound();
    					mCamera.takePicture(null, null, mPicture);
    					
    					//update TextView
    					if (hTextView != null) {
    						hTextView.setText("Porks eaten: " + nCounter);
    					} else {
    						Log.d("ERROR", "hTextView is NULL");
    					}
    					Log.d("TIMER", "TimerTask run " + nCounter);
    				}
    			});		
    		}};
    		
    	t.schedule(mTimerTask,  500,10000);
    }
    	
    public void stopTask(){
    			
   		if(mTimerTask!=null) {
  			if (hTextView != null) {
    			hTextView.setText("Pork canceled after: " + nCounter + " photos");
  			} else {
    			Log.d("ERROR", "hTextView is NULL");
    		}
   		}
    	Log.d("TIMER", "timer canceled");
    	mTimerTask.cancel();
    			
    }
    
    public void shootSound() {
    	AudioManager meng = (AudioManager)
    	getBaseContext().getSystemService(Context.AUDIO_SERVICE);
    	int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
    			
    	if (volume != 0) {
    		if (_shootMP == null)
    			_shootMP = MediaPlayer.create(getBaseContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
    		if (_shootMP != null)
    			_shootMP.start();
    	}
    }
    		
    public static final int MEDIA_TYPE_IMAGE = 1;

    /** Create a file Uri for saving an image or video */
  	private static Uri getOutputMediaFileUri(int type){
  		return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.

    	File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PorkyCam");
    	// This location works best if you want the created images to be shared
    	// between applications and persist after your app has been uninstalled.

    	// Create the storage directory if it does not exist
    	if (! mediaStorageDir.exists()){
    		if (! mediaStorageDir.mkdirs()){
                Log.d("PorkyCam", "failed to create directory");
   		            return null;
  	        }
  	    }
    		    
       // Create a media file name
       String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(type));
       File mediaFile;
    		   
       if (type == MEDIA_TYPE_IMAGE){
   	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
       } else {
   	        return null;
       }

  	   return mediaFile;
   	}
}

    

