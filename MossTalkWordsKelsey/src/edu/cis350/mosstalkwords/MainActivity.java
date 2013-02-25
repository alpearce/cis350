package edu.cis350.mosstalkwords;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.speech.RecognizerIntent;
import android.view.View.OnClickListener;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE = 1234;

	Button nextButton;
	ImageView firstImage;

	Button speakBtn;
	StimulusSet livingEasySet;
	StimulusSet livingHardSet;
	StimulusSet nonlivingEasySet;
	StimulusSet nonlivingHardSet;

	String currentImage;

	int score;
	
	int imageCounter=0;
	int setCounter=0;
	StimulusSet allStimulusSets [];
	StimulusSet currentSet=livingEasySet;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadData();
		//currentSet=allStimulusSets[setCounter];
		addListenerForButton();

		PackageManager pm = getPackageManager();
		List RecognizerActivities = pm.queryIntentActivities(
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
	public void addListenerForButton()
	{
		firstImage=(ImageView) findViewById(R.id.imageView1);

		speakBtn = (Button) findViewById(R.id.speakButton);

		//firstImage=(ImageView) findViewById(livingHard.getStimuli()[0].getImage());//this gives a picture id not the imageview id
		firstImage=((ImageView) findViewById(R.id.imageView1));
		firstImage.setImageResource(currentSet.getStimuli()[imageCounter].getImage());
		currentImage = currentSet.getStimuli()[imageCounter].getName();
		nextButton = (Button) findViewById(R.id.btnChangeImage);
		nextButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
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
		firstImage.setImageResource(R.drawable.bird);
		imageCounter++;
		imageCounter=imageCounter%(currentSet.getStimuli().length);
		//firstImage.setImageResource(R.drawable.bird);
		firstImage.setImageResource((currentSet.getStimuli()[imageCounter].getImage()));
		currentImage = currentSet.getStimuli()[imageCounter].getName();
	}

	public void loadData()
	{

		Stimulus livingEasyStimuli[] = new Stimulus [10];
		Stimulus livingHardStimuli[] = new Stimulus [10];
		Stimulus nonlivingEasyStimuli[] = new Stimulus [10];
		Stimulus nonlivingHardStimuli[] = new Stimulus [10];

		livingEasyStimuli[0] = new Stimulus("Apple", 0, null, R.drawable.applesmall);
		livingEasyStimuli[1] = new Stimulus("Bird", 0, null, R.drawable.bird);
		livingEasyStimuli[2] = new Stimulus("Carrot", 0, null, R.drawable.carrot);
		livingEasyStimuli[3] = new Stimulus("Cat", 0, null, R.drawable.cat);
		livingEasyStimuli[4] = new Stimulus("Corn", 0, null, R.drawable.corn);
		livingEasyStimuli[5] = new Stimulus("Cow", 0, null, R.drawable.cow);
		livingEasyStimuli[6] = new Stimulus("Dog", 0, null, R.drawable.dog);
		livingEasyStimuli[7] = new Stimulus("Elephant", 0, null, R.drawable.elephant);
		livingEasyStimuli[8] = new Stimulus("Flower", 0, null, R.drawable.flower);
		livingEasyStimuli[9] = new Stimulus("Tomato", 0, null, R.drawable.tomato);

		livingEasySet=new StimulusSet("Living Easy", livingEasyStimuli);

		livingHardStimuli[0] = new Stimulus("Giraffe", 1, null, R.drawable.giraffe);
		livingHardStimuli[1] = new Stimulus("Octopus", 1, null, R.drawable.octopus);
		livingHardStimuli[2] = new Stimulus("Pineapple", 1, null, R.drawable.pineapple);
		livingHardStimuli[3] = new Stimulus("Pine Cone", 1, null, R.drawable.pinecone);
		livingHardStimuli[4] = new Stimulus("Pumpkin", 1, null, R.drawable.pumpkin);
		livingHardStimuli[5] = new Stimulus("Rooster", 1, null, R.drawable.rooster);
		livingHardStimuli[6] = new Stimulus("Cauliflower", 1, null, R.drawable.cauliflower);
		livingHardStimuli[7] = new Stimulus("Asparagus", 1, null, R.drawable.asparagus);
		livingHardStimuli[8] = new Stimulus("Avocado", 1, null, R.drawable.avocado);
		livingHardStimuli[9] = new Stimulus("Broccoli", 1, null, R.drawable.broccoli);

		livingHardSet = new StimulusSet("Living Hard", livingHardStimuli);

		/*nonlivingEasyStimuli[0] = new Stimulus("Chair", 0, null, R.drawable.chair);
		nonlivingEasyStimuli[1] = new Stimulus("Table", 0, null, R.drawable.table);
		nonlivingEasyStimuli[2] = new Stimulus("Lamp", 0, null, R.drawable.lamp);
		nonlivingEasyStimuli[3] = new Stimulus("Bed", 0, null, R.drawable.bed);
		nonlivingEasyStimuli[4] = new Stimulus("Phone", 0, null, R.drawable.phone);
		nonlivingEasyStimuli[5] = new Stimulus("House", 0, null, R.drawable.house);
		nonlivingEasyStimuli[6] = new Stimulus("Shirt", 0, null, R.drawable.shirt);
		nonlivingEasyStimuli[7] = new Stimulus("Shoes", 0, null, R.drawable.shoes);
		nonlivingEasyStimuli[8] = new Stimulus("Hat", 0, null, R.drawable.hat);
		nonlivingEasyStimuli[9] = new Stimulus("Money", 0, null, R.drawable.money);*/

		nonlivingEasySet = new StimulusSet("Nonliving Easy", nonlivingEasyStimuli);

		String [] computerhints = {getResources().getString(R.string.computerhint1),
				getResources().getString(R.string.computerhint2), 
				getResources().getString(R.string.computerhint3)};

		String [] textbookhints = {getResources().getString(R.string.textbookhint1),
				getResources().getString(R.string.textbookhint2), 
				getResources().getString(R.string.textbookhint3)};

		/*String [] televisionhints = {getResources().getString(R.string.televisionhint1),
				 					  getResources().getString(R.string.televisionhint2), 
				 					  getResources().getString(R.string.televisionhint3)};*/

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

		/*nonlivingHardStimuli[0] = new Stimulus("Computer", 1, computerhints, R.drawable.computer);
		nonlivingHardStimuli[1] = new Stimulus("Textbook", 1, textbookhints, R.drawable.textbook);
		nonlivingHardStimuli[2] = new Stimulus("Television", 1, televisionhints, R.drawable.tv);
		nonlivingHardStimuli[3] = new Stimulus("Refrigerator", 1, refrigeratorhints, R.drawable.fridge);
		nonlivingHardStimuli[4] = new Stimulus("Basketball", 1, basketballhints, R.drawable.basketball);
		nonlivingHardStimuli[5] = new Stimulus("Football", 1, footballhints, R.drawable.football);
		nonlivingHardStimuli[6] = new Stimulus("Soccerball", 1, soccerballhints, R.drawable.soccerball);
		nonlivingHardStimuli[7] = new Stimulus("Pocket", 1, pockethints, R.drawable.pocket);
		nonlivingHardStimuli[8] = new Stimulus("Zipper", 1, zipperhints, R.drawable.zipper);
		nonlivingHardStimuli[9] = new Stimulus("Gloves", 1, gloveshints, R.drawable.gloves);*/

		nonlivingHardSet= new StimulusSet("Nonliving Hard", nonlivingHardStimuli);

		allStimulusSets= new StimulusSet[4];
		allStimulusSets[0] = livingEasySet;
		allStimulusSets[1] = livingHardSet;
		allStimulusSets[2] = nonlivingEasySet;
		allStimulusSets[3] = nonlivingHardSet;

		currentSet=allStimulusSets[0];


	}

	public void onNextSetButtonClick(View view)
	{
		setCounter++;
		imageCounter=0;
		setCounter=setCounter%allStimulusSets.length;
		currentSet=allStimulusSets[setCounter];
		firstImage.setImageResource((currentSet.getStimuli()[imageCounter].getImage()));
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
			//goes through all the possible strings from voice and detirmines if there is a match
			//right now score is just incremented by 100
			for (String s: matches) {
				if (s.equalsIgnoreCase(currentImage)) {
					score =+ 100;
					nextImage();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	//Handler for sentence hint
	public void onHint1ButtonClick() {
		//TODO
	}

	//handler for similar word hint
	public void onHint2ButtonClick() {
		//TODO
	}

	//handler for giving up and getting answer
	public void onHint3ButtonClick() {
		//TODO
	}

}
