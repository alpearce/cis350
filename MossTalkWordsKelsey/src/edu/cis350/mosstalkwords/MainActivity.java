package edu.cis350.mosstalkwords;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	StimulusSet allStimulusSets [];
	StimulusSet currentSet = livingEasySet;

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
		
		loadData();
		new BackgroundTask().execute();
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
		imSwitcher.setImageResource(currentSet.getStimuli()[imageCounter].getImage());
		currentImage = currentSet.getStimuli()[imageCounter].getName();
		
		
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
			imageCounter=imageCounter%(currentSet.getStimuli().length);
			
			currentImage = currentSet.getStimuli()[imageCounter].getName();
			Bitmap im = getBitmapFromCache(currentImage); 
			if (im == null) { Log.d("nextImage","null bitmap- that's bad/" + currentImage); } 
			else { Log.d("nextImage","should load" + currentImage); }
			Drawable drawableBitmap = new BitmapDrawable(getResources(),im);
			imSwitcher.setImageDrawable(drawableBitmap);
			Log.d("nextImage","set image to " + currentImage);
			
			TextView hintView= (TextView)findViewById(R.id.hintText);
			hintView.setText("");
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
		imageCounter++;
		imageCounter=imageCounter%(currentSet.getStimuli().length);
		new BackgroundTask().execute();
	
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
		hintView.setText(currentSet.getStimuli()[imageCounter].getHints()[0]);
		hintsUsed++;
	}

	//handler for similar word hint
	public void onHint2ButtonClick(View view) {
		TextView hintView= (TextView)findViewById(R.id.hintText);
		hintView.setText(currentSet.getStimuli()[imageCounter].getHints()[1]);
		hintsUsed++;
	}

	//handler for giving up and getting answer
	public void onHint3ButtonClick(View view) {
		TextView hintView= (TextView)findViewById(R.id.hintText);
		hintView.setText(currentSet.getStimuli()[imageCounter].getHints()[2]);
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
	
	//async task for interfacing with Amazon S3 without blocking main thread
	class BackgroundTask extends AsyncTask<String, Integer, Drawable[]> {
		public String[] remoteURLS = livingEasySet.getStimuliNames();
		
		@Override
		protected void onPreExecute() {
		//TODO could make a textview that says "loading images" or some such
		}
		
		protected Drawable[] doInBackground(String... params) {		
			try {
				for(int i = 0; i < remoteURLS.length; i++) {
					URL aURL = new URL("https://s3.amazonaws.com/mosstalkdata/" +
						currentSet.getName() +"2/" + remoteURLS[i].toLowerCase() + ".jpg");				
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

				//stimReady = true;
				Log.d("async task","done setting drawables");
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
				String name = currentSet.getStimuli()[1].getName();
				Drawable drawableBitmap = new BitmapDrawable(getResources(),getBitmapFromCache(name));
				imSwitcher.setImageDrawable(drawableBitmap);
			}
			Log.d("async task","progress update: loaded " + im);
			
		}	
		//after we are done downloading the data, set all the drawables
		protected void onPostExecute(String Result) {
			
		}
	}
	
	public void loadData()
	{
		Stimulus livingEasyStimuli[] = new Stimulus [10];
		Stimulus livingHardStimuli[] = new Stimulus [10];
		Stimulus nonlivingEasyStimuli[] = new Stimulus [10];
		Stimulus nonlivingHardStimuli[] = new Stimulus [10];


		String [] birdhints = {getResources().getString(R.string.birdhint1),
				getResources().getString(R.string.birdhint2), 
				getResources().getString(R.string.birdhint3)};

		
		String [] cathints = {getResources().getString(R.string.cathint1),
				getResources().getString(R.string.cathint2), 
				getResources().getString(R.string.cathint3)};

		String [] cornhints = {getResources().getString(R.string.cornhint1),
							   getResources().getString(R.string.cornhint2), 
							   getResources().getString(R.string.cornhint3)};

		String [] cowhints = {getResources().getString(R.string.cowhint1),
							  getResources().getString(R.string.cowhint2), 
							  getResources().getString(R.string.cowhint3)}; 

		String [] doghints = {getResources().getString(R.string.doghint1),
							  getResources().getString(R.string.doghint2), 
							  getResources().getString(R.string.doghint3)};
		
		String [] beanhints = {getResources().getString(R.string.beanhint1),
				  getResources().getString(R.string.beanhint2), 
				  getResources().getString(R.string.beanhint3)};
		
		String [] bearhints = {getResources().getString(R.string.bearhint1),
				  getResources().getString(R.string.bearhint2), 
				  getResources().getString(R.string.bearhint3)};
		
		String [] ricehints = {getResources().getString(R.string.ricehint1),
				  getResources().getString(R.string.ricehint2), 
				  getResources().getString(R.string.ricehint3)};
		
		String [] froghints = {getResources().getString(R.string.froghint1),
				  getResources().getString(R.string.froghint2), 
				  getResources().getString(R.string.froghint3)};

		String [] treehints = {getResources().getString(R.string.treehint1),
				  getResources().getString(R.string.treehint2), 
				  getResources().getString(R.string.treehint3)};
	
		
		livingEasyStimuli[0] = new Stimulus("Bean", 0, beanhints, 0);
		livingEasyStimuli[1] = new Stimulus("Bear", 0, bearhints, 0);
		livingEasyStimuli[2] = new Stimulus("Bird", 0, birdhints, 0);
		livingEasyStimuli[3] = new Stimulus("Cat", 0, cathints, 0);
		livingEasyStimuli[4] = new Stimulus("Corn", 0, cornhints, 0);
		livingEasyStimuli[5] = new Stimulus("Cow", 0, cowhints, 0);
		livingEasyStimuli[6] = new Stimulus("Dog", 0, doghints, 0);
		livingEasyStimuli[7] = new Stimulus("Frog", 0, froghints, 0);
		livingEasyStimuli[8] = new Stimulus("Tree", 0, treehints, 0);
		livingEasyStimuli[9] = new Stimulus("Rice", 0, ricehints, 0);
		
		
		/*livingEasyStimuli[0] = new Stimulus("Apple", 0, applehints, R.drawable.applesmall);
		livingEasyStimuli[1] = new Stimulus("Bird", 0, birdhints, R.drawable.bird);
		livingEasyStimuli[2] = new Stimulus("Carrot", 0, carrothints, R.drawable.carrot);
		livingEasyStimuli[3] = new Stimulus("Cat", 0, cathints, R.drawable.cat);
		livingEasyStimuli[4] = new Stimulus("Corn", 0, cornhints, R.drawable.corn);
		livingEasyStimuli[5] = new Stimulus("Cow", 0, cowhints, R.drawable.cow);
		livingEasyStimuli[6] = new Stimulus("Dog", 0, doghints, R.drawable.dog);
		livingEasyStimuli[7] = new Stimulus("Elephant", 0, elephanthints, R.drawable.elephant);
		livingEasyStimuli[8] = new Stimulus("Flower", 0, flowerhints, R.drawable.flower);
		livingEasyStimuli[9] = new Stimulus("Tomato", 0, tomatohints, R.drawable.tomato);*/

		livingEasySet=new StimulusSet("livingthingseasy", livingEasyStimuli);

		String [] applehints = {getResources().getString(R.string.applehint1),
				getResources().getString(R.string.applehint2), 
				getResources().getString(R.string.applehint3)};
		
		String [] elephanthints = {getResources().getString(R.string.elephanthint1),
							 	   getResources().getString(R.string.elephanthint2), 
							 	   getResources().getString(R.string.elephanthint3)};

		String [] flowerhints = {getResources().getString(R.string.flowerhint1),
							     getResources().getString(R.string.flowerhint2), 
							     getResources().getString(R.string.flowerhint3)};

		String [] tomatohints = {getResources().getString(R.string.tomatohint1),
								 getResources().getString(R.string.tomatohint2), 
								 getResources().getString(R.string.tomatohint3)};

		String [] carrothints = {getResources().getString(R.string.carrothint1),
				getResources().getString(R.string.carrothint2), 
				getResources().getString(R.string.carrothint3)};
		
		String [] giraffehints = {getResources().getString(R.string.giraffehint1),
								  getResources().getString(R.string.giraffehint2), 
								  getResources().getString(R.string.giraffehint3)}; 

		String [] octopushints = {getResources().getString(R.string.octopushint1),
					      		  getResources().getString(R.string.octopushint2), 
					      		  getResources().getString(R.string.octopushint3)};

		String [] pineapplehints = {getResources().getString(R.string.pineapplehint1),
									getResources().getString(R.string.pineapplehint2), 
									getResources().getString(R.string.pineapplehint3)};

		String [] pineconehints = {getResources().getString(R.string.pineconehint1),
								   getResources().getString(R.string.pineconehint2), 
								   getResources().getString(R.string.pineconehint3)};

		String [] pumpkinhints = {getResources().getString(R.string.pumpkinhint1),
								  getResources().getString(R.string.pumpkinhint2), 
								  getResources().getString(R.string.pumpkinhint3)};

		String [] roosterhints = {getResources().getString(R.string.roosterhint1),
								  getResources().getString(R.string.roosterhint2), 
								  getResources().getString(R.string.roosterhint3)}; 

		String [] cauliflowerhints = {getResources().getString(R.string.cauliflowerhint1),
									  getResources().getString(R.string.cauliflowerhint2), 
									  getResources().getString(R.string.cauliflowerhint3)};

		String [] asparagushints = {getResources().getString(R.string.asparagushint1),
						    		getResources().getString(R.string.asparagushint2), 
						    		getResources().getString(R.string.asparagushint3)};

		String [] avocadohints = {getResources().getString(R.string.avocadohint1),
								  getResources().getString(R.string.avocadohint2), 
								  getResources().getString(R.string.avocadohint3)};

		String [] broccolihints = {getResources().getString(R.string.broccolihint1),
								   getResources().getString(R.string.broccolihint2), 
								   getResources().getString(R.string.broccolihint3)};

		String [] strawberryhints =  {getResources().getString(R.string.strawberryhint1),
								   getResources().getString(R.string.strawberryhint2), 
								   getResources().getString(R.string.strawberryhint3)};


		livingHardStimuli[0] = new Stimulus("Giraffe", 1, giraffehints, R.drawable.giraffe);
		livingHardStimuli[1] = new Stimulus("Octopus", 1, octopushints, R.drawable.octopus);
		livingHardStimuli[2] = new Stimulus("Pineapple", 1, pineapplehints, R.drawable.pineapple);
		livingHardStimuli[3] = new Stimulus("Strawberry",1, strawberryhints, 0);
		//livingHardStimuli[3] = new Stimulus("Pine Cone", 1, pineconehints, R.drawable.pinecone);
		livingHardStimuli[4] = new Stimulus("Pumpkin", 1, pumpkinhints, R.drawable.pumpkin);
		//livingHardStimuli[5] = new Stimulus("Rooster", 1, roosterhints, R.drawable.rooster);
		livingHardStimuli[5] = new Stimulus("Elephant", 1, elephanthints, 0);
		livingHardStimuli[6] = new Stimulus("Cauliflower", 1, cauliflowerhints, R.drawable.cauliflower);
		livingHardStimuli[7] = new Stimulus("Asparagus", 1, asparagushints, R.drawable.asparagus);
		livingHardStimuli[8] = new Stimulus("Avocado", 1, avocadohints, R.drawable.avocado);
		livingHardStimuli[9] = new Stimulus("Broccoli", 1, broccolihints, R.drawable.broccoli);


		livingHardSet = new StimulusSet("livingthingshard", livingHardStimuli);

		String [] chairhints = {getResources().getString(R.string.chairhint1),
  								getResources().getString(R.string.chairhint2), 
  								getResources().getString(R.string.chairhint3)};

		String [] tablehints = {getResources().getString(R.string.tablehint1),
		  						getResources().getString(R.string.tablehint2), 
		  						getResources().getString(R.string.tablehint3)};

		String [] lamphints = {getResources().getString(R.string.lamphint1),
							   getResources().getString(R.string.lamphint2), 
							   getResources().getString(R.string.lamphint3)};

		String [] bedhints = {getResources().getString(R.string.bedhint1),
							  getResources().getString(R.string.bedhint2), 
							  getResources().getString(R.string.bedhint3)};
		String [] phonehints = {getResources().getString(R.string.phonehint1),
						        getResources().getString(R.string.phonehint2), 
						        getResources().getString(R.string.phonehint3)};
		String [] househints = {getResources().getString(R.string.househint1),
							    getResources().getString(R.string.househint2), 
							    getResources().getString(R.string.househint3)};
		String [] shirthints = {getResources().getString(R.string.shirthint1),
								getResources().getString(R.string.shirthint2), 
								getResources().getString(R.string.shirthint3)};
		String [] shoeshints = {getResources().getString(R.string.shoehint1),
								getResources().getString(R.string.shoehint2), 
								getResources().getString(R.string.shoehint3)};
		String [] hathints = {getResources().getString(R.string.hathint1),
							  getResources().getString(R.string.hathint2), 
							  getResources().getString(R.string.hathint3)};
		String [] moneyhints = {getResources().getString(R.string.moneyhint1),
						  		getResources().getString(R.string.moneyhint2), 
						  		getResources().getString(R.string.moneyhint3)};

		nonlivingEasyStimuli[0] = new Stimulus("Chair", 0, chairhints, R.drawable.chair);
		nonlivingEasyStimuli[1] = new Stimulus("Table", 0, tablehints, R.drawable.table);
		nonlivingEasyStimuli[2] = new Stimulus("Lamp", 0, lamphints, R.drawable.lamp);
		nonlivingEasyStimuli[3] = new Stimulus("Bed", 0, bedhints, R.drawable.bed);
		nonlivingEasyStimuli[4] = new Stimulus("Phone", 0, phonehints, R.drawable.phone);
		nonlivingEasyStimuli[5] = new Stimulus("House", 0, househints, R.drawable.house);
		nonlivingEasyStimuli[6] = new Stimulus("Shirt", 0, shirthints, R.drawable.shirt);
		nonlivingEasyStimuli[7] = new Stimulus("Shoes", 0, shoeshints, R.drawable.shoes);
		nonlivingEasyStimuli[8] = new Stimulus("Hat", 0, hathints, R.drawable.hat);
		nonlivingEasyStimuli[9] = new Stimulus("Money", 0, moneyhints, R.drawable.money);

		nonlivingEasySet = new StimulusSet("nonlivingthingseasy", nonlivingEasyStimuli);

		String [] computerhints = {getResources().getString(R.string.computerhint1),
								   getResources().getString(R.string.computerhint2), 
								   getResources().getString(R.string.computerhint3)};

		String [] textbookhints = {getResources().getString(R.string.textbookhint1),
								   getResources().getString(R.string.textbookhint2), 
								   getResources().getString(R.string.textbookhint3)};

		String [] televisionhints = {getResources().getString(R.string.televisionhint1),
				 					 getResources().getString(R.string.televisionhint2), 
				 					 getResources().getString(R.string.televisionhint3)};

		 String [] refrigeratorhints = {getResources().getString(R.string.refrigeratorhint1),
				 						getResources().getString(R.string.refrigeratorhint2), 
				 						getResources().getString(R.string.refrigeratorhint3)};

		 String [] basketballhints = {getResources().getString(R.string.basketballhint1),
				 					  getResources().getString(R.string.basketballhint2), 
				 					  getResources().getString(R.string.basketballhint3)};


		 String [] footballhints = {getResources().getString(R.string.footballhint1),
				 					getResources().getString(R.string.footballhint2), 
				 					getResources().getString(R.string.footballhint3)};

		 String [] soccerballhints = {getResources().getString(R.string.soccerballhint1),
				 					  getResources().getString(R.string.soccerballhint2), 
				 					  getResources().getString(R.string.soccerballhint3)};
		 String [] pockethints = {getResources().getString(R.string.pockethint1),
				 				  getResources().getString(R.string.pockethint2), 
				 				  getResources().getString(R.string.pockethint3)};
		 String [] zipperhints = {getResources().getString(R.string.zipperhint1),
				 				  getResources().getString(R.string.zipperhint2), 
				 				  getResources().getString(R.string.zipperhint3)};

		 String [] gloveshints = {getResources().getString(R.string.gloveshint1),
				 				  getResources().getString(R.string.gloveshint2), 
				 				  getResources().getString(R.string.gloveshint3)};


		nonlivingHardStimuli[0] = new Stimulus("Computer", 1, computerhints, R.drawable.computer);
		nonlivingHardStimuli[1] = new Stimulus("Textbook", 1, textbookhints, R.drawable.textbook);
		nonlivingHardStimuli[2] = new Stimulus("Television", 1, televisionhints, R.drawable.tv);
		nonlivingHardStimuli[3] = new Stimulus("Refrigerator", 1, refrigeratorhints, R.drawable.fridge);
		nonlivingHardStimuli[4] = new Stimulus("Basketball", 1, basketballhints, R.drawable.basketball);
		nonlivingHardStimuli[5] = new Stimulus("Football", 1, footballhints, R.drawable.football);
		nonlivingHardStimuli[6] = new Stimulus("Soccerball", 1, soccerballhints, R.drawable.soccerball);
		nonlivingHardStimuli[7] = new Stimulus("Pocket", 1, pockethints, R.drawable.pocket);
		nonlivingHardStimuli[8] = new Stimulus("Zipper", 1, zipperhints, R.drawable.zipper);
		nonlivingHardStimuli[9] = new Stimulus("Gloves", 1, gloveshints, R.drawable.gloves);

		nonlivingHardSet= new StimulusSet("nonlivingthingshard", nonlivingHardStimuli);

		allStimulusSets= new StimulusSet[4];
		allStimulusSets[0] = livingEasySet;
		allStimulusSets[1] = livingHardSet;
		allStimulusSets[2] = nonlivingEasySet;
		allStimulusSets[3] = nonlivingHardSet;

		currentSet=allStimulusSets[0];
	
	}

}
