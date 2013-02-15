package edu.cis350.ipaint;

import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new BackgroundTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void onClearButtonClick(View view) {
		edu.cis350.ipaint.PaintBrushView pbv = (edu.cis350.ipaint.PaintBrushView)findViewById(R.id.pbv);
		pbv.clear();
	}
	
	class BackgroundTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			TextView tv = (TextView)findViewById(R.id.timer);
			tv.setText("0:00");
			
		}
		
		protected Void doInBackground(Void... params) {
			int secs = 0;
			while(true) {
				try {
					Thread.sleep(1000);
					secs++;
					publishProgress(secs);
				} catch (InterruptedException e) {
					
				}	
			}
		}
		
		protected void onProgressUpdate(Integer...progress) {
			TextView tv = (TextView)findViewById(R.id.timer);
			int mins = progress[0]/60;
			int secs = progress[0]%60;
			String timer;
			if (secs < 10) {
				timer = mins + ":0" + secs;
			} else {
				timer = mins + ":" + secs;
			}
			tv.setText(timer);
		}
		
		protected void onPostExecute(String Result) {
			TextView tv = (TextView)findViewById(R.id.timer);
			tv.setText("-:--");
			
		}
		
		
	}
	
	
	
}
