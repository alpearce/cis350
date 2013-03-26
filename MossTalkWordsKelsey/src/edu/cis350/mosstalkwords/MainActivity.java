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
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
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
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ImageSwitcher;
import android.widget.ViewSwitcher;

public class MainActivity extends Activity implements ViewFactory, TextToSpeech.OnInitListener{

	private static final int REQUEST_CODE = 1234;
	final int REQUIRE_HEIGHT = 1280;
	final int REQUIRE_WIDTH = 800;
	private int index;

	private TextToSpeech tts;


	private ImageCache imCache;
	Button nextButton;
	ImageSwitcher imSwitcher;
	User currentUser;
	Button speakBtn;

	Chronometer timer;

	String currentImage;
	private int stimulusSetSize=10;

	//game metrics--MOST OF THESE WENT TO THE USER CLASS AFTER REFACTORING
	int hintsUsed=0;
	int numAttempts=0;//starts at one because does not increment when answered correctly
	///end metrics

	int imageCounter=0;
	int setCounter=0;
	ArrayList<StimulusSet> allStimulusSets;
	StimulusSet currentSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("on create");
		super.onCreate(savedInstanceState);

		new InitCategoriesBackgroundTask().execute();
		openWelcomePage();


		tts = new TextToSpeech(this, this);

		setContentView(R.layout.activity_main);

		imSwitcher = (ImageSwitcher) findViewById(R.id.ImageSwitcher1);
		imSwitcher.setFactory(this);
		imSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
		imSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

		imCache = new ImageCache();
		currentUser = new User();


		timer = (Chronometer) findViewById(R.id.chronometer1);
		timer.start();

		TextView txtView = (TextView)findViewById(R.id.chronometer1);
		TextView scoreView = (TextView)findViewById(R.id.scoretext);

		txtView.setTextColor(Color.RED);
		Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-BoldCondensed.ttf");
		txtView.setTypeface(typeface);
		scoreView.setTypeface(typeface);
		//loadData();


		/*new InitCategoriesBackgroundTask().execute();**********************REMEMBER TO UNCOMMENT THIS***/

		//new ImageBackgroundTask().execute();



		addListenerForButton();

		PackageManager pm = getPackageManager();
		List<ResolveInfo> RecognizerActivities = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (RecognizerActivities.size() == 0) {
			speakBtn.setEnabled(false);
			speakBtn.setText("Not compatible");
		}			
	}
	private void openWelcomePage() {
		Intent welcome = new Intent(this, WelcomeActivity.class);
		startActivityForResult(welcome, 3);

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

	public void addListenerForButton()
	{
		speakBtn = (Button) findViewById(R.id.speakButton);

		imSwitcher=(ImageSwitcher) findViewById(R.id.ImageSwitcher1);

		nextButton = (Button) findViewById(R.id.btnChangeImage);

		nextButton.setBackgroundColor(Color.WHITE);
		nextButton.setOnClickListener(new OnClickListener()
		{
			//next button: user didn't get the word so score = 0 and streak ends; switch images
			public void onClick(View arg0) {
				currentUser.updateImageScore(currentSet.getName(), imageCounter, 0);
				currentUser.streakEnded(currentSet);
				hintsUsed=3;
				numAttempts=3;
				nextImage();
			}
		});

		speakBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				startVoiceRecognitionActivity();
			}
		});
	}
	public void finishedSet()
	{
		imSwitcher.setImageDrawable(null);
		Intent endSet=new Intent(this, EndSetActivity.class);
		endSet.putExtra("User", currentUser);
		endSet.putExtra("currentSet", currentSet.getName());
		startActivityForResult(endSet,4);
	}
	public void nextImage() {
		currentUser.updateImageEfficiency(currentSet.getName(), imageCounter, hintsUsed, numAttempts);
		resetMetricsImage();
		System.out.println(imageCounter);
		//if at the end of the set then go into finishedSet setup
		if(imageCounter==currentSet.getStimuli().size()-1) {
			currentUser.finishedSet(currentSet.getName());
			imageCounter = 0;
			Log.d("imagecounter","image counter is: " + imageCounter);
			//call next set?
			//GO INTO SCORE ACTIVITY HERE
			finishedSet();
		}
		else {
			imageCounter++;
			imageCounter=imageCounter%(currentSet.getStimuli().size());
			Log.d("imagecounter","imagecounter is: " + imageCounter);
			//imageCounter=imageCounter%(currentSet.getStimuli().size());

			//load next image
			currentImage = currentSet.getStimuli().get(imageCounter).getName();
			Bitmap im = imCache.getBitmapFromCache(currentImage); 
			if (im == null) { Log.d("nextImage","null bitmap- that's bad/" + currentImage); } 
			Drawable drawableBitmap = new BitmapDrawable(getResources(),im);
			imSwitcher.setImageDrawable(drawableBitmap);

			TextView hintView= (TextView)findViewById(R.id.hintText);
			hintView.setText("");	
		}
		timer.setBase(SystemClock.elapsedRealtime());
	}

	public void resetMetricsImage() {
		hintsUsed=0;
		numAttempts=0;//starts at one because does not increment when answered correctly
	}

	public void nextSet() {
		resetMetricsImage();
		setCounter=(setCounter + 1)%allStimulusSets.size();
		currentSet = allStimulusSets.get(setCounter);
		Log.d("nextset","new set is " + currentSet.getName());
		imageCounter=0;
		Log.d("imagecounter","image counter is: " + imageCounter);
		imCache.clearCache();
		new LoadHintsBackgroundTask().execute();
		TextView hintView= (TextView)findViewById(R.id.hintText);
		hintView.setText("");
	}	
	public void replaySet()
	{
		imageCounter=0;
		resetMetricsImage();
		currentImage = currentSet.getStimuli().get(imageCounter).getName();
		Bitmap im = imCache.getBitmapFromCache(currentImage); 
		if (im == null) { Log.d("nextImage","null bitmap- that's bad/" + currentImage); } 
		Drawable drawableBitmap = new BitmapDrawable(getResources(),im);
		imSwitcher.setImageDrawable(drawableBitmap);
		timer.setBase(SystemClock.elapsedRealtime());
		//currentImage = currentSet.getStimuli().get(imageCounter).getName();
	}
	public void onNextSetButtonClick(View view) {
		nextSet();			
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
		if (requestCode == 3) {
			//this is the index of the allStimulusSetsArray
			index = data.getExtras().getInt("indexOfSetsArray");
			System.out.println(index);
			System.out.println(allStimulusSets.get(index).getName());
			//new InitCategoriesBackgroundTask().execute();
			currentSet = allStimulusSets.get(index);
			new LoadHintsBackgroundTask().execute();
			timer.setBase(SystemClock.elapsedRealtime());

			//System.out.println(currentSet.getName());
		}
		if(requestCode==4)
		{
			if(data.getBooleanExtra("Replay Set", false))
			{
				replaySet();
			}
			else if(data.getBooleanExtra("Next Set", false))
			{
				nextSet();
			}
			else if(data.getBooleanExtra("Main Menu", true))
			{
				openWelcomePage();
			}

		}
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
		{
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			//goes through all the possible strings from voice and determines if there is a match
			//right now score is just incremented by 100
			for (String s: matches) {
				if (s.toLowerCase().contains(currentImage.toLowerCase())) {
					//subtract 100 for each hint used, but if 3 are used make the score 100 anyway
					int thisImageScore = (300-100*hintsUsed == 0 ? 100:300-100*hintsUsed);
					currentUser.updateImageScore(currentSet.getName(), imageCounter, thisImageScore);
					TextView scoreTextView = (TextView)findViewById(R.id.scoretext);
					scoreTextView.setText("Score: " + String.valueOf(currentUser.getTotalScore()));

					if(hintsUsed==0) {
						currentUser.increaseStreak();
					}
					else {
						currentUser.streakEnded(currentSet);
					}
					MediaPlayer mp=MediaPlayer.create(MainActivity.this,R.raw.ding);
					mp.start(); //this could be its own class too
					nextImage();
				}
				else {
					numAttempts++;//if got it wrong the number of attempts increments
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//Handler for sentence hint
	public void onHint1ButtonClick(View view) {
		String stimulusName = currentSet.getStimuliNames()[imageCounter];
		for (Stimulus curr : currentSet.getStimuli()) {
			if (curr.getName().equalsIgnoreCase(stimulusName)) {
				speak(curr.getHints()[0]);
			}
		}
		hintsUsed++;
	}

	//handler for similar word hint
	public void onHint2ButtonClick(View view) {
		String stimulusName = currentSet.getStimuliNames()[imageCounter];
		for (Stimulus curr : currentSet.getStimuli()) {
			if (curr.getName().equalsIgnoreCase(stimulusName)) {
				speak(curr.getHints()[1]);
			}
		}
		hintsUsed++;
	}

	//handler for giving up and getting answer
	public void onHint3ButtonClick(View view) {
		String stimulusName = currentSet.getStimuliNames()[imageCounter];
		for (Stimulus curr : currentSet.getStimuli()) {
			if (curr.getName().equalsIgnoreCase(stimulusName)) {
				speak(curr.getHints()[2]);
			}
		}
		hintsUsed++;
	}


	/*-----------------------------------Background Tasks for Cache--------------------------------*/
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



	/*-----------------------------------------------------------------------------------------------*/
	/*-----------------------------------Background Tasks for S3-------------------------------------*/
	/*-----------------------------------------------------------------------------------------------*/

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
				//currentSet = allStimulusSets.get(0);//set current set to first set
				//new LoadHintsBackgroundTask().execute();
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
				currentUser.initSet(setName, tmpSet.size());
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
					options.inSampleSize = ImageCache.calculateInSampleSize(options, REQUIRE_WIDTH, REQUIRE_HEIGHT);

					// Decode bitmap with inSampleSize set
					options.inJustDecodeBounds = false;
					conn = aURL.openConnection(); //reopen connection
					conn.connect();
					Log.d("asynctask","got connected second time");
					bis = new BufferedInputStream(conn.getInputStream());
					Bitmap bitmap = BitmapFactory.decodeStream(bis, r, options); 					   

					imCache.addBitmapToCache( remoteURLS[i] , bitmap);
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
				Drawable drawableBitmap = new BitmapDrawable(getResources(),imCache.getBitmapFromCache(name));
				imSwitcher.setImageDrawable(drawableBitmap);
				imageCounter = 0;
			}
			Log.d("async task","progress update: loaded " + im);			
		}	

		protected void onPostExecute(String Result) {			
		}



	}
	
	public void speak(String words2say) {
		tts.speak(words2say, TextToSpeech.QUEUE_FLUSH, null);
	}

	public void onInit(int status) {
		speak("Welcome to MossTalk Words!");
	}

}
