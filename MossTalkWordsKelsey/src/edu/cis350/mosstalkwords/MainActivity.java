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
	StimulusSet livingHard;
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
		firstImage=(ImageView) findViewById(R.id.imageView1);
		nextButton = (Button) findViewById(R.id.btnChangeImage);
		nextButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				firstImage.setImageResource(R.drawable.bird);
			}
		});
	}
	public void loadData()
	{
		Stimulus livingHardStimuli[] = new Stimulus [10];
		livingHardStimuli[0] = new Stimulus(null, 0, null, firstImage);
		livingHard=new StimulusSet("LivingHard", livingHardStimuli);
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
