package com.example.porkycam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	File mediaFile;
	
	
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
			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PorkyCam");
			try{
				mediaFile = new File(mediaStorageDir.getPath() + File.separator + "PORK_" + fileCount + ".jpg");
				Log.d("FILE", "newfile");			
				FileOutputStream outStream = null;
				Toast.makeText(getApplicationContext(), "picture taken", Toast.LENGTH_SHORT).show();
				outStream = new FileOutputStream(mediaFile);
				outStream.write(data);
				outStream.close();
				Log.d("onPictureTaken - wrote bytes: " + data.length, null, null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d("onPictureTaken - jpeg", null, null);
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
    
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(mediaFile);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Toast.makeText(getApplicationContext(), "galleryAddPic", Toast.LENGTH_SHORT).show();
    }
}

    

