package edu.cis350.mosstalkwords;

import android.app.Activity; 
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Score extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.score_text);
	    super.onCreate(savedInstanceState);
	    Intent i = getIntent();
	    int score = i.getIntExtra("score", 0);
	    TextView view = (TextView)findViewById(R.id.scoretext);
	    view.setText("Score = " + score);
	    try {
			i.wait(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    finish();
	}

}
