package edu.cis350.mosstalkwords;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class EndSetReturnActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
Context mContext = this; //this.getApplicationContext();
		   
	    
	    
	   // LayoutInflater inflater = this.getLayoutInflater();//(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    //final View layout = inflater.inflate(R.layout.dialog_enter_name_and_email, null); 
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    builder.setCancelable(false);
		builder.setTitle("Play Again?")//hackish extra spaces to center the title since not an option
			//.setView(layout)
		
			.setNeutralButton(R.string.restart, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//restart set
						Intent i=getIntent();
						i.putExtra("Main Menu", false);
						i.putExtra("Replay Set", true);
						i.putExtra("Next Set", false);
						setResult(RESULT_OK, i);
						finish();
					}
				})
				.setPositiveButton(R.string.nextSet, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//restart set
						Intent i=getIntent();
						i.putExtra("Main Menu", false);
						i.putExtra("Replay Set", false);
						i.putExtra("Next Set", true);
						setResult(RESULT_OK, i);
						finish();
					}
				})
				.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//go to main menu
						Intent i=getIntent();
						i.putExtra("Main Menu", true);
						i.putExtra("Replay Set", false);
						i.putExtra("Next Set", false);
						setResult(RESULT_OK, i);
						finish();
					}
				});
				
		
		AlertDialog alert= builder.create();//create the AlertDialog object and return it
		//alert.setContentView(R.layout.dialog_endset);
		alert.show();
	}

}
