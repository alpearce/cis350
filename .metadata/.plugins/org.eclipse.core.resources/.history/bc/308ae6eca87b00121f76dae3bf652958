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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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

}
