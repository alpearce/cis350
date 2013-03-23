package edu.cis350.mosstalkwords;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList; 
import java.util.List;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;
import android.speech.RecognizerIntent;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ImageSwitcher;
import android.widget.ViewSwitcher;

public class MainActivity extends Activity implements ViewFactory {

	private static final int REQUEST_CODE = 1234;
	final int REQUIRE_HEIGHT = 1500;
	final int REQUIRE_WIDTH = 1000;
	
	private LruCache<String, Bitmap> imCache; //need cache for S3 images
	Button nextButton;
	ImageSwitcher imSwitcher;
	User currentUser;
	Button speakBtn;
	StimulusSet livingEasySet;
	StimulusSet livingHardSet;
	StimulusSet nonlivingEasySet;
	StimulusSet nonlivingHardSet;

	String currentImage;
	private int stimulusSetSize=10;
	
	//game metrics
	int hintsUsed=0;
	int [] scoreArray= new int[stimulusSetSize];
	int score = 0;
	int streak = 0;
	int numberOfAttempts=1;//starts at one because does not increment when answered correctly
	int [][] efficiencyArray= new int[2][stimulusSetSize];//0 is hintsUsed, 1 is numberOfAttempts 
	//end metrics
	
	int imageCounter=0;
	int setCounter=0;
	ArrayList<StimulusSet> allStimulusSets;
	StimulusSet currentSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		imSwitcher = (ImageSwitcher) findViewById(R.id.ImageSwitcher1);
		imSwitcher.setFactory(this);
		imSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
		imSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
		
		currentUser = new User();
			
		//set up cache for images
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);	
		
		//use 1/2 size of available memory for cache (probably a bad idea, but YOLO)
		final int cacheSize = maxMemory/2;
		imCache = new LruCache<String, Bitmap>(cacheSize) {
			//had to add this line to get it to compile; it won't like anything
			//less than api v12
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
		
		//loadData();
		new InitCategoriesBackgroundTask().execute();
		//new ImageBackgroundTask().execute();
		addListenerForButton();

		PackageManager pm = getPackageManager();
		List<ResolveInfo> RecognizerActivities = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (RecognizerActivities.size() == 0)
		{
			speakBtn.setEnabled(false);
			speakBtn.setText("Not compatible");
		}
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	//required method for imageswitcher class
	public View makeView() {
		ImageView iv = new ImageView(this);
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iv.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		iv.setBackgroundColor(0xFFFFFFFF); //opaque white background
		return iv;
	}
	
	public void addBitmapToCache(String key, Bitmap bitmap) {
		if (getBitmapFromCache(key)==null) {
			imCache.put(key, bitmap);
		}
	}
	
	public Bitmap getBitmapFromCache(String key) {
		return imCache.get(key);
	}
	
	public void clearCache() {
		imCache.evictAll();
	}
	
	public void addListenerForButton()
	{
		speakBtn = (Button) findViewById(R.id.speakButton);

		imSwitcher=(ImageSwitcher) findViewById(R.id.ImageSwitcher1);
		//imSwitcher.setImageResource(currentSet.getStimuli()[imageCounter].getImage());
		//currentImage = currentSet.getStimuli()[imageCounter].getName();
		
		nextButton = (Button) findViewById(R.id.btnChangeImage);
		nextButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				scoreArray[imageCounter]=0;
				streakEnded();
				nextImage();
			}
		});

		speakBtn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				startVoiceRecognitionActivity();
			}
		});
	}

	//moved contents of nextimage here so it can be accessed below
	public void nextImage() {
		efficiencyArray[0][imageCounter]=hintsUsed;
		efficiencyArray[1][imageCounter]=numberOfAttempts;
		resetMetricsImage();
		
		if(imageCounter==stimulusSetSize-1)//if at the end of the set then go into finishedSet setup
		{
			finishedSet();		
		}
		else
		{
			imageCounter++;
			imageCounter=imageCounter%(currentSet.getStimuli().size());
			
			currentImage = currentSet.getStimuli().get(imageCounter).getName();
			Bitmap im = getBitmapFromCache(currentImage); 
			if (im == null) { Log.d("nextImage","null bitmap- that's bad/" + currentImage); } 
			else { Log.d("nextImage","should load" + currentImage); }
			Drawable drawableBitmap = new BitmapDrawable(getResources(),im);
			imSwitcher.setImageDrawable(drawableBitmap);
			Log.d("nextImage","set image to " + currentImage);
			
			TextView hintView= (TextView)findViewById(R.id.hintText);
			hintView.setText("");
			
			//just to test
			score += 100;
			TextView scoreTextView = (TextView)findViewById(R.id.scoretext);
			scoreTextView.setText("Score: " + String.valueOf(score));
		}
	}
	public void finishedSet()
	{
		if(currentUser.stimulusSetScores.containsKey(currentSet.setName))//if the stimulus set has already been played, remove the previous score array
		{
			currentUser.stimulusSetScores.remove(currentSet.setName);
		}
		currentUser.stimulusSetScores.put(currentSet.setName, scoreArray);//add the score array for the stimulus set to the user hashmap
		currentUser.calculateStarScore(currentSet.setName);
		currentUser.stimulusSetEfficiencies.put(currentSet.setName, efficiencyArray);//add the efficiencies for this set to the hashmap
		currentUser.calculateAverageEfficiencyPercent(currentSet.setName);//calculate the percentage for this set
		nextSet();
	}
	public void nextSet()
	{
		resetMetricsSet();
		setCounter++;
		setCounter=setCounter%allStimulusSets.size();
		currentSet = allStimulusSets.get(setCounter);
		imageCounter=0;
		clearCache();
		new ImageBackgroundTask().execute();	
		TextView hintView= (TextView)findViewById(R.id.hintText);
		hintView.setText("");
	}	

	public void onNextSetButtonClick(View view)
	{
		nextSet();			
	}
	
	public void resetMetricsImage()
	{
		hintsUsed=0;
		numberOfAttempts=1;//starts at one because does not increment when answered correctly
	}
	public void resetMetricsSet()
	{
		//streak=0; streak should keep going between sets
		resetMetricsImage();
	}
	public void streakEnded()
	{
		if (currentUser == null) {Log.d("null pointer","current user is null");}
		if (currentUser.longestStreakForSets==null) {Log.d("null pointer", "CULSFS is null");}
		if (currentSet == null) {Log.d("null pointer","currentSet is null");}
		if (currentSet.setName == null) {Log.d("null pointer", "current set name null");}
		
		if(currentUser.longestStreakForSets.containsKey(currentSet.setName)&&streak>currentUser.longestStreakForSets.get(currentSet.setName))//if a streak already exists, and this streak is larger, update the streak
		{	
			currentUser.longestStreakForSets.remove(currentSet.setName);
			currentUser.longestStreakForSets.put(currentSet.setName, streak);
		}
		else if(!currentUser.longestStreakForSets.containsKey(currentSet.setName))//if there is no streak, make this the longest streak
			currentUser.longestStreakForSets.put(currentSet.setName, streak);
		//otherwise do nothing and reset streak counter
		streak=0;//used next button so reset streak to zero
	}
	
	private void startVoiceRecognitionActivity()
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say what you see in the picture...");
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
		{
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			//goes through all the possible strings from voice and determines if there is a match
			//right now score is just incremented by 100
			for (String s: matches) {
				if (s.toLowerCase().contains(currentImage.toLowerCase())) {
					scoreArray[imageCounter]=(300-100*hintsUsed==0?100:300-100*hintsUsed);//subtract 100 for each hint used, but if 3 are used make the score 100 anyway
					score += scoreArray[imageCounter];
					TextView scoreTextView = (TextView)findViewById(R.id.scoretext);
					scoreTextView.setText("Score: " + String.valueOf(score));
					
					if(hintsUsed==0)
						streak++;
					else
						streakEnded();
					MediaPlayer mp=MediaPlayer.create(MainActivity.this,R.raw.ding);
					mp.start();
					nextImage();
				}
				else
					numberOfAttempts++;//if got it wrong the number of attempts increments
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	//Handler for sentence hint
	public void onHint1ButtonClick(View view) {
		TextView hintView= (TextView)findViewById(R.id.hintText);
		hintView.setText(currentSet.getStimuli().get(imageCounter).getHints()[0]);
		hintsUsed++;
	}

	//handler for similar word hint
	public void onHint2ButtonClick(View view) {
		TextView hintView= (TextView)findViewById(R.id.hintText);
		hintView.setText(currentSet.getStimuli().get(imageCounter).getHints()[1]);
		hintsUsed++;
	}

	//handler for giving up and getting answer
	public void onHint3ButtonClick(View view) {
		TextView hintView= (TextView)findViewById(R.id.hintText);
		hintView.setText(currentSet.getStimuli().get(imageCounter).getHints()[2]);
		hintsUsed++;
	}
	
	//scale down images based on display size; helps with OOM errors
	public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 0;
	    int newHeight = height;
	    int newWidth = width;
	    
	    while (newHeight > reqHeight || newWidth > reqWidth) {
	    	newHeight = newHeight/2; //should be power of two 
	    	newWidth = newWidth/2;
	    	inSampleSize += 2;
	    	
	    	}
	    if (inSampleSize == 0) { inSampleSize = 2; }
	    Log.d("async task","in sample size is:" + (inSampleSize));
	
	    return inSampleSize;
	}
	
	
	class InitCategoriesBackgroundTask extends AsyncTask<String, Integer, Void> {
		String s3append = "2"; //until we merge our datasets, all of ours end in 2
		protected Void doInBackground(String... params) {
			allStimulusSets = new ArrayList<StimulusSet>();
			try {
				//load the categories
				URL aURL = new URL("https://s3.amazonaws.com/mosstalkdata/categories" + s3append + ".txt");				
				Log.d("url", aURL.toString());
	
				BufferedReader bread = new BufferedReader(new InputStreamReader(aURL.openStream()));
				int setIdx = 0;
				String line;
				while((line = bread.readLine()) != null) {
					allStimulusSets.add(new StimulusSet(line));
					Log.d("initcat","stimulusSets[" +setIdx+ "] = " + line);
					setIdx++;
				}
				currentSet = allStimulusSets.get(0);//set current set to first set
				new LoadHintsBackgroundTask().execute();
			} catch (MalformedURLException e) {
				// TODO we should make a crash/error screen
				e.printStackTrace();
				Log.e("exception","malformedURL");
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("exception","IOexception");
			} finally {  }
			return null;
		}		
	}
	
	class LoadHintsBackgroundTask extends AsyncTask<String, Integer, Void> {
		String s3append = "2"; //until we merge our datasets, all of ours end in 2
		protected Void doInBackground(String... params) {
			String setName = currentSet.getName();
			//String[] imageNames = currentSet.getStimuliNames();		
			ArrayList<Stimulus> tmpSet = new ArrayList<Stimulus>();	
			try {		
				URL aURL = new URL("https://s3.amazonaws.com/mosstalkdata/" +
						setName + "/" + "hints.txt");				
				Log.d("url", aURL.toString());
				BufferedReader bread = new BufferedReader(new InputStreamReader(aURL.openStream()));
				//lines invariant: name = hint3. hint1, hint2, empty line
				String line;
				while((line = bread.readLine()) != null) {	//load the 
					String[] hints = new String[Stimulus.NUMHINTS];
					String name = line;
					Log.d("initset","read name = " + name);
					hints[0] = bread.readLine();//should be sentence
					Log.d("initset","read sentence = " + hints[0]);
					hints[1] = bread.readLine();//should be rhymes
					Log.d("initset","read rhymes = " + hints[1]);
					bread.readLine();//skip blank line
					hints[2] = name;
					Stimulus tmp = new Stimulus(name, 1, hints);//everything is difficulty 1 right now. deal.
					tmpSet.add(tmp);
				}
				currentSet.setStimuli(tmpSet);	
				new ImageBackgroundTask().execute();
			} catch (MalformedURLException e) {
				// TODO we should make a crash/error screen
				e.printStackTrace();
				Log.e("exception","malformedURL");
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("exception","IOexception");
			} finally {  }
			return null;
		}		
	}

	
	//async task for interfacing with Amazon S3 without blocking main thread
	class ImageBackgroundTask extends AsyncTask<String, Integer, Drawable[]> {
		public String[] remoteURLS = currentSet.getStimuliNames();
		
		protected Drawable[] doInBackground(String... params) {		
			try {
				for(int i = 0; i < remoteURLS.length; i++) {
					URL aURL = new URL("https://s3.amazonaws.com/mosstalkdata/" +
						currentSet.getName() +"/" + remoteURLS[i].toLowerCase() + ".jpg");				
					Log.d("url", aURL.toString());
					URLConnection conn = aURL.openConnection();
					conn.connect();
					Log.d("asynctask","got connected");
					
					BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
					
					//try to decode bitmap without running out of memory
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					Rect r = new Rect(-1,-1,-1,-1);//me
					BitmapFactory.decodeStream(bis, r, options); //me
				    Log.d("asynctask","decoded bounds");
					bis.close();
				    
				    // Calculate inSampleSize - need to figure out required dims
				    options.inSampleSize = calculateInSampleSize(options, REQUIRE_WIDTH, REQUIRE_HEIGHT);
				    			    					
					// Decode bitmap with inSampleSize set
				    options.inJustDecodeBounds = false;
				    conn = aURL.openConnection(); //reopen connection
					conn.connect();
					Log.d("asynctask","got connected second time");
					bis = new BufferedInputStream(conn.getInputStream());
				    Bitmap bitmap = BitmapFactory.decodeStream(bis, r, options); 					   
				    			    
					addBitmapToCache( remoteURLS[i] , bitmap);
					Log.d("async task","added bitmap to cache: " + remoteURLS[i] );
					if (i==0) { 
						Log.d("async task","first image loaded");
					}
					publishProgress(i);
				}
			} catch (MalformedURLException e) {
				// TODO we should make a crash/error screen
				e.printStackTrace();
				Log.e("exception","malformedURL");
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("exception","IOexception");
			} finally {  }
			return null;
		}
				
		protected void onProgressUpdate(Integer...progress) {
			int im = progress[0];			
			if (progress[0] == 0) {
				String name = currentSet.getStimuli().get(0).getName();
				Drawable drawableBitmap = new BitmapDrawable(getResources(),getBitmapFromCache(name));
				imSwitcher.setImageDrawable(drawableBitmap);
			}
			Log.d("async task","progress update: loaded " + im);			
		}	
		protected void onPostExecute(String Result) {			
		}
	}
	
}
