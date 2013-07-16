package com.example.porkycam;

import java.io.File;
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
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	TimerTask mTimerTask;
	final Handler handler = new Handler();
	Timer t = new Timer();
	TextView hTextView;
	TableRow hTableRow;
	Button hButton, hButtonStop;
	private int nCounter = 0;
	public static int fileCount = 0;
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
			Toast.makeText(getApplicationContext(), "picture taken", Toast.LENGTH_SHORT).show();
	        galleryAddPic();
	    }
	};
   
	public void doTimerTask(){
    	Log.d("TIMER","TimerTask Started");
    	
    	mTimerTask = new TimerTask() {
    		public void run() {
    			handler.post(new Runnable() {
    				public void run() {
    					nCounter++;
    					fileCount++;
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
    		
    	t.schedule(mTimerTask,500,10000);
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
   
    

    // Create a File for saving an image or video
    private static File getOutputMediaFile(int type){
  
    	File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PorkyCam");

    	// Create the storage directory if it does not exist
    	if (! mediaStorageDir.exists()){
    		if (! mediaStorageDir.mkdirs()){
                Log.d("PorkyCam", "failed to create directory");
   		            return null;
  	        }
  	    }
    		    
       // Create file name
       String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(type));
       File mediaFile;
    		   
       if (type == MEDIA_TYPE_IMAGE){
   	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + fileCount + ".jpg");
   	        Log.d("FILE", "newfile");
       } else {
   	        return null;
       }
  	   return mediaFile;
   	}
    
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Toast.makeText(getApplicationContext(), "galleryAddPic", Toast.LENGTH_SHORT).show();
    }
}

    

