package edu.cis350.mosstalkwords;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.view.View;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	Button nextButton;
	ImageView firstImage;
	StimulusSet livingEasySet;
	StimulusSet livingHardSet;
	StimulusSet nonlivingEasySet;
	StimulusSet nonlivingHardSet;
	int imageCounter=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadData();
		addListenerForButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	public void addListenerForButton()
	{
		
		//firstImage=(ImageView) findViewById(livingHard.getStimuli()[0].getImage());//this gives a picture id not the imageview id
		firstImage=((ImageView) findViewById(R.id.imageView1));
		firstImage.setImageResource(livingEasySet.getStimuli()[imageCounter].getImage());
		nextButton = (Button) findViewById(R.id.btnChangeImage);
		nextButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				imageCounter++;
				imageCounter=imageCounter%10;
				//firstImage.setImageResource(R.drawable.bird);
				firstImage.setImageResource((livingEasySet.getStimuli()[imageCounter].getImage()));
			}
		});
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
