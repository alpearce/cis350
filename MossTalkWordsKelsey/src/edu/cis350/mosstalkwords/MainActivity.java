package edu.cis350.mosstalkwords;

import java.io.BufferedInputStream; 
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.List;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher.ViewFactory;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
	private final String PREFERENCE_NAME="UserPreferences";
	private int index;

	private TextToSpeech tts;
	private ProgressBar pbar;

	private ImageCache imCache;
	Button nextButton;
	ImageSwitcher imSwitcher;
	User currentUser;
	Button speakBtn;

	Button popup;


	final Context context = this;


	Chronometer timer;

	String currentImage;
	private int stimulusSetSize=10;
	private int rhymePtr;

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
		currentUser = new User();/*must be initialized before welcome page for initial start up so that
		rating bars can be populated with scores the same way everytime, even if the sets haven't been played*/

		retrievePreferences();//get the current user and their data if already exists
		
		//openWelcomePage();


		tts = new TextToSpeech(this, this);

		setContentView(R.layout.activity_main);

		imSwitcher = (ImageSwitcher) findViewById(R.id.ImageSwitcher1);
		imSwitcher.setFactory(this);
		imSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
		imSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

		imCache = new ImageCache();

		rhymePtr = 0;


		new InitCategoriesBackgroundTask().execute();

		pbar = (ProgressBar) findViewById(R.id.progressBar1);

		timer = (Chronometer) findViewById(R.id.chronometer1);
		TextView txtView = (TextView)findViewById(R.id.chronometer1);
		TextView scoreView = (TextView)findViewById(R.id.scoretext);
		txtView.setTextColor(Color.RED);
		Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-BoldCondensed.ttf");
		txtView.setTypeface(typeface);
		scoreView.setTypeface(typeface);
		timer.start();


		addListenerForButton();

		PackageManager pm = getPackageManager();
		List<ResolveInfo> RecognizerActivities = pm.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (RecognizerActivities.size() == 0) {
			speakBtn.setEnabled(false);
			speakBtn.setText("Not compatible");
		}			

	}
	public void createSharedPreferences()
	{
		SharedPreferences userSettings=getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
		 SharedPreferences.Editor editor = userSettings.edit();
		 editor.putString("name", currentUser.name);
		 editor.putString("email", currentUser.email);
		 editor.putInt("totalScore", currentUser.getTotalScore());
		 for(String set:currentUser.starsForSets.keySet())
		 {
			 editor.putString(set, Integer.toString(currentUser.starsForSets.get(set)));
		 }
		 editor.commit();
		 
	}
	public void retrievePreferences()
	{
		//SharedPreferences emportPref = getSharedPreferences("dawaaData",MODE_PRIVATE);
		SharedPreferences prefs=getSharedPreferences(PREFERENCE_NAME,MODE_PRIVATE);
		if(prefs.contains("name"))//if prefs stored
		{
			currentUser.name=prefs.getString("name", null);
			currentUser.email=prefs.getString("email",null);
			currentUser.setTotalScore(prefs.getInt("totalScore", 0));
			HashMap<String, Integer> stars=new HashMap<String, Integer>();
			while(allStimulusSets==null||allStimulusSets.isEmpty())
			{
				
			}
			for(StimulusSet set:allStimulusSets)
			{
				stars.put(set.getName(),Integer.valueOf(prefs.getString(set.getName(), "0")));
			}
			currentUser.starsForSets.putAll(stars);
			for(String s: currentUser.starsForSets.keySet())
			{
				System.out.println(s+": "+currentUser.starsForSets.get(s));
			}
		}
		openWelcomePage();
	}
	public void enterNameAndEmail()
	{
		Intent userEntry = new Intent(this, NameAndEmailActivity.class);
		userEntry.putExtra("User", currentUser);
		startActivityForResult(userEntry,2);
	}
	private void openWelcomePage() {
		Intent welcome = new Intent(this, WelcomeActivity.class);
		welcome.putExtra("User", currentUser);
		startActivityForResult(welcome, 3);

	}
	private void returnFromSet()
	{
		Intent returnOptions = new Intent(this, EndSetReturnActivity.class);
		returnOptions.putExtra("User", currentUser);
		startActivityForResult(returnOptions,1);
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
				currentUser.streakEnded(currentSet.setName);
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
	public File createReport() throws IOException
	{
		//PrintWriter reportOut=new PrintWriter(currentSet.getName()+"Report.txt");
		//BufferedOutputStream reportOut = new BufferedOutputStream(new FileOutputStream(currentSet.getName()+"Report.txt"));
		//reportOut.write(("User: "+currentUser.name).getBytes());
		//General set stats
		File path =Environment.getExternalStorageDirectory();
		File dir=new File(path.getAbsolutePath()+"/textfiles");
		dir.mkdirs();
		File reportFile=new File(dir,(currentSet.getName()+"Report.txt"));
		//OutputStreamWriter reportOut = new OutputStreamWriter(openFileOutput(currentSet.getName()+"Report.txt", this.MODE_PRIVATE));
		String reportString=currentUser.generateSetReport(currentSet);
		FileWriter report=new FileWriter(reportFile);
		report.write(reportString);
		//reportOut.write(reportString);
		//reportOut.close();
		report.close();
		//reportOut.println("User: "+currentUser.name);
		//reportOut.close();
		return(reportFile);
	}
	public void sendReportViaEmail(File fileName)
	{
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
				new String[]{currentUser.email});
		String subject="Wordle "+currentSet.getName() +" Report";
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		String body= "Your report is attached below. Good Work!";
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);

		//String rawFolderPath = "android.resource://" + getPackageName() 
		//                       + "/" + R.raw.shortcuts;

		// Here my file name is shortcuts.pdf which i have stored in /res/raw folder
		//Uri emailUri = Uri.parse(rawFolderPath );
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileName));
		emailIntent.setType("vnd.android.cursor.dir/vnd.google.note");
		startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."),5);

	}
	public void createAndSendReport()
	{

		File fileMade=new File("");
		try {
			fileMade = createReport();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sendReportViaEmail(fileMade);

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
		Log.d("attempts image", "attempts used: "+String.valueOf(numAttempts));
	
		resetMetricsImage();
		pbar.setProgress(imageCounter + 1);
		
		//animate.scaleXBy(1/10);
		//if at the end of the set then go into finishedSet setup
		if(imageCounter==currentSet.getStimuli().size()-1) {
			currentUser.endedSet(currentSet.getName());
			imageCounter = 0;
			Log.d("imagecounter","image counter is: " + imageCounter);
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
			while (im == null) { //block main thread until it's loaded. could end badly.
				Log.d("nextImage","BUSY LOOP null bitmap- that's bad/" + currentImage); 
				im = imCache.getBitmapFromCache(currentImage);
			} 
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
		
		setCounter=(setCounter + 1)%allStimulusSets.size();
		currentSet = allStimulusSets.get(setCounter);
		playSet();
		Log.d("nextset","new set is " + currentSet.getName());
		imageCounter=0;
		Log.d("imagecounter","image counter is: " + imageCounter);
		imCache.clearCache();
		new LoadHintsBackgroundTask().execute();
		TextView hintView= (TextView)findViewById(R.id.hintText);
		hintView.setText("");
	}	
	public void playSet()
	{
		resetMetricsImage();
		pbar.setProgress(0);
		currentUser.resetLongestStreak(currentSet.setName);
	}
	public void replaySet()
	{
		imageCounter=0;
		playSet();
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
		currentImage = currentSet.getStimuli().get(imageCounter).getName();
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say what you see in the picture...");
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode==1)//if returning from Play Again? choice
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
		if(requestCode==2)//if returning from Send report enter email and Username?
		{
			if(data.getBooleanExtra("Cancel",true))
			{
				returnFromSet();
			}
			else
			{

				currentUser.name=data.getExtras().getString("Username");
				currentUser.email=data.getExtras().getString("Email");
				createAndSendReport();
				createSharedPreferences();

			}

		}
		if (requestCode == 3) {
			//this is the index of the allStimulusSetsArray
			index = data.getExtras().getInt("indexOfSetsArray");
			System.out.println(index);
			System.out.println(allStimulusSets.get(index).getName());
			//new InitCategoriesBackgroundTask().execute();
			currentSet = allStimulusSets.get(index);
			new LoadHintsBackgroundTask().execute();
			timer.setBase(SystemClock.elapsedRealtime());

		}
		if(requestCode==4)
		{
			if(data.getBooleanExtra("Send", false))
			{
				if(currentUser.name==null)
					enterNameAndEmail();
				else
				{
					createAndSendReport();

				}
			}
			else if(data.getBooleanExtra("No", false))
			{
				returnFromSet();
			}
			if(currentUser.name!=null)
				createSharedPreferences();

		}
		if(requestCode==5)//returned from email intent
		{
			returnFromSet();
		}
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
		{
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			//goes through all the possible strings from voice and determines if there is a match
			//right now score is just incremented by 100
			if (matches.isEmpty()) { Log.d("voice rec","NO MATCHES");}
			boolean matchFound = false;
			for (String s: matches) {
				if (s.toLowerCase().contains(currentImage.toLowerCase())) {
					//subtract 100 for each hint used, but if 3+ are used make the score 100 anyway
					int thisImageScore = (300-100*hintsUsed <= 0 ? 100:300-100*hintsUsed);
					currentUser.updateImageScore(currentSet.getName(), imageCounter, thisImageScore);
					ViewFlipper scoreTextView = (ViewFlipper)findViewById(R.id.ViewFlipper);
					TextView scoreView = (TextView)scoreTextView.findViewById(R.id.scoretext);

					scoreView.setText("Score: " + String.valueOf(currentUser.getTotalScore()));
					ViewPropertyAnimator animate = scoreTextView.animate();
					animate.rotationBy(360);

					speak("Great Job", 1);
					if(hintsUsed==0) {
						currentUser.increaseStreak();
					}
					else {
						currentUser.streakEnded(currentSet.setName);
					}
					MediaPlayer mp=MediaPlayer.create(MainActivity.this,R.raw.ding);
					mp.start(); //this could be its own class too
					nextImage();
					matchFound = true;
				}
			}

			if (!matchFound) {
				numAttempts++;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				int size = matches.size();
				String prompt = "Incorrect. You said:";
				String incorrectResponses = ""; 
				if (size > 3){size = 3;}
				for (int i = 0; i < size; i++) {
					incorrectResponses = incorrectResponses + "\n" + matches.get(i);
				}

				prompt = prompt + incorrectResponses;
				alertDialogBuilder.setMessage(prompt);
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						tts.stop();
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

				TextView messageText = (TextView)alertDialog.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);
				prompt = prompt.replaceFirst("\\n", "");
				prompt = prompt.replaceAll("\\n", ",oar, "); //oar = or for tts apparently...
				speak(prompt, (float) 0.8);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//Handler for sentence hint
	public void onSentenceHintButtonClick(View view) {
		TextView hintView= (TextView)findViewById(R.id.hintText);
		String hint = currentSet.getStimuli().get(imageCounter).getSentence();
		hintView.setText(hint);
		speak(hint, 1);
		hintsUsed++;
	}

	//handler for similar word hint
	public void onRhymeHintButtonClick(View view) {
		TextView hintView= (TextView)findViewById(R.id.hintText);
		String[] rhymes = currentSet.getStimuli().get(imageCounter).getRhymes();
		Log.d("rhyme hints","rhymes:" + rhymes.toString());
		Log.d("rhyme hints","rhyme ptr: " + rhymePtr);
		String hint = rhymes[rhymePtr];
		hintView.setText(hint);
		speak(hint, 1);
		hintsUsed++;
		rhymePtr = (++rhymePtr)%rhymes.length;
		Log.d("rhyme hints after","rhyme ptr: " + rhymePtr);
	}

	//handler for giving up and getting answer
	public void onAnswerHintButtonClick(View view) {
		TextView hintView= (TextView)findViewById(R.id.hintText);
		String hint = currentSet.getStimuli().get(imageCounter).getName();
		hintView.setText(hint);
		speak(hint, 1);
		hintsUsed++;
	}


	/*-----------------------------------Helpers for Cache-------------------------------------------*/
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


	public Bitmap fetchImageFromS3(String name) { //STOP TRYING TO MAKE FETCH HAPPEN, GRETCHEN.
		try {
			URL aURL = new URL("https://s3.amazonaws.com/mosstalkdata/" +
					currentSet.getName() +"/" + name.toLowerCase() + ".jpg");				
			Log.d("url", aURL.toString());
			URLConnection conn = aURL.openConnection();
			conn.connect();
			Log.d("fetch","got connected");

			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

			//decode bitmap bounds first to avoid running out of memory
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Rect r = new Rect(-1,-1,-1,-1);
			BitmapFactory.decodeStream(bis, r, options);
			Log.d("fetch","decoded bounds");
			bis.close();

			// Calculate inSampleSize - need to figure out required dims
			options.inSampleSize = ImageCache.calculateInSampleSize(options, REQUIRE_WIDTH, REQUIRE_HEIGHT);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			conn = aURL.openConnection(); //reopen connection
			conn.connect();
			Log.d("fetch","got connected second time");
			bis = new BufferedInputStream(conn.getInputStream());
			Bitmap bitmap = BitmapFactory.decodeStream(bis, r, options); 
			return bitmap;
		} catch (MalformedURLException e) {
			// TODO we should make a crash/error screen
			e.printStackTrace();
			Log.e("exception","malformedURL");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("exception","IOexception");
			return null;
		} 
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
					allStimulusSets.add(new StimulusSet(line.trim()));
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
					String name = line.trim();
					Log.d("initset","read name = " + name);
					String sentence = bread.readLine().trim();//should be sentence
					Log.d("initset","read sentence = " + sentence);

					String[] rhymes = bread.readLine().toLowerCase().trim().split(",",-1);//should be rhymes
					Log.d("initset","read rhymes = " + rhymes);
					bread.readLine();//skip blank line
					Stimulus tmp = new Stimulus(name, 1, rhymes, sentence);//everything is difficulty 1 right now. deal.
					tmpSet.add(tmp);
				}
				currentSet.setStimuli(tmpSet);
				currentUser.initSet(setName, tmpSet.size());
				pbar.setProgress(0);
				pbar.setMax(tmpSet.size());
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
					//Bitmap bitmap = fetchImageFromS3(remoteURLS[i].toLowerCase());

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

	public void speak(String words2say, float rate) {
		tts.setSpeechRate(rate);
		tts.speak(words2say, TextToSpeech.QUEUE_FLUSH, null);
	}

	public void onInit(int status) {
		speak("Welcome to Wordle!", 1);
	}

}
