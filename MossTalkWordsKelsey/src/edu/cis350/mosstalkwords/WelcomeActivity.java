package edu.cis350.mosstalkwords;


import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_page);
		
	}
	
	public void onClickLivingEasy(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 0);
		setResult(RESULT_OK, i);
		finish();
	}
	public void onClickLivingMedium(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 1);
		setResult(RESULT_OK, i);
		finish();
	}
	public void onClickLivingHard(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 2);
		setResult(RESULT_OK, i);
		finish();
	}
	public void onClickNonLivingEasy(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 3);
		setResult(RESULT_OK, i);
		finish();
	}
	public void onClickNonLivingHard(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 4);
		setResult(RESULT_OK, i);
		finish();
	}

}
