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
import android.widget.RatingBar;

public class NameAndEmailActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Context mContext = this; //this.getApplicationContext();
		   
	    
		 Intent i=getIntent();
		 User currentUser=(User) i.getSerializableExtra("User");
		    
	    LayoutInflater inflater = this.getLayoutInflater();//(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View layout = inflater.inflate(R.layout.dialog_enter_name_and_email, null); 
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    	builder.setView(layout);
	    	builder.setCancelable(false);
	    	builder.setTitle("Welcome New User!");
	    
	    
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					//return to main activity
					Intent i=getIntent();
					i.putExtra("Cancel", true);
					setResult(RESULT_OK, i);
					finish();
				}
			})
			.setPositiveButton(R.string.send, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					//return username and email to main activity
					EditText username=(EditText) layout.findViewById(R.id.username);
				    EditText email=(EditText) layout.findViewById(R.id.email);
					
				    Intent i=getIntent();
					i.putExtra("Cancel",false);
					i.putExtra("Username", username.getText().toString());
					i.putExtra("Email", email.getText().toString());
					setResult(RESULT_OK, i);
					finish();
				}
			});
				
		
		AlertDialog alert= builder.create();//create the AlertDialog object and return it
		//alert.setContentView(R.layout.dialog_endset);
		alert.show();
	}
}
