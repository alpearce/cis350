package edu.cis350.mosstalkwords;

<<<<<<< HEAD
=======
import java.io.Serializable;
import java.util.ArrayList;

>>>>>>> 488055a70361f41d76224e9c39e906b32e8ca3fa
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
<<<<<<< HEAD
		Button b = (Button)findViewById(R.id.livingeasy);
		
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				
			}
		});
		
	}
=======
		
	}
	public void onClickLivingEasy(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 0);
		setResult(RESULT_OK, i);
		finish();
	}
	public void onClickLivingMedium(View view) {
		
	}
	public void onClickLivingHard(View view) {
		 
	}
>>>>>>> 488055a70361f41d76224e9c39e906b32e8ca3fa

}
