package edu.cis350.mosstalkwords.test;

import org.junit.Test;
import android.widget.ImageView;

import edu.cis350.mosstalkwords.MainActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;
import edu.cis350.mosstalkwords.R;

public class StimulusClassTest extends ActivityInstrumentationTestCase2<MainActivity>{
	private MainActivity activity;
	private Button button;
	private TextView result;
	private ImageView imageResult;
@SuppressWarnings("deprecation")
public StimulusClassTest()
{
	super("edu.cis350.mosstalkwords", MainActivity.class);
}
protected void setUp() throws Exception {
	super.setUp();
	activity=getActivity();
	
}
@Test
public void testOneClickHint1Button()
{
	button=(Button) activity.findViewById(R.id.hint1button);
	activity.runOnUiThread(new Runnable()
	{
		public void run()
		{
			button.performClick();
		}
	});
	getInstrumentation().waitForIdleSync();
	result=(TextView) activity.findViewById(R.id.hintText);
	assertTrue("Should be first hint of apple, red fruit grows on trees", result.getText().toString().equals("This is a red fruit that grows on trees."));
}
@Test
public void testOneClickHint2Button()
{
	button=(Button) activity.findViewById(R.id.hint2button);
	activity.runOnUiThread(new Runnable()
	{
		public void run()
		{
			button.performClick();
		}
	});
	getInstrumentation().waitForIdleSync();
	result=(TextView) activity.findViewById(R.id.hintText);
	assertTrue("Should be second hint of apple, Chapel", result.getText().toString().equals("Chapel"));
}
@Test
public void testOneClickHint3Button()
{
	button=(Button) activity.findViewById(R.id.hint3button);
	activity.runOnUiThread(new Runnable()
	{
		public void run()
		{
			button.performClick();
		}
	});
	getInstrumentation().waitForIdleSync();
	result=(TextView) activity.findViewById(R.id.hintText);
	assertTrue("Should be third hint of apple, apple", result.getText().toString().equals("apple"));
}
@Test
public void testOneClickNextButton()
{
	button=(Button) activity.findViewById(R.id.btnChangeImage);
	activity.runOnUiThread(new Runnable()
	{
		public void run()
		{
			button.performClick();
		}
	});
	getInstrumentation().waitForIdleSync();
	imageResult=(ImageView) activity.findViewById(R.id.imageView1);
	assertTrue("Should be bird", imageResult.getDrawable().equals(R.drawable.bird));
}
@Test
public void testOneClickNextSetButton()
{
	button=(Button) activity.findViewById(R.id.nextSetButton);
	activity.runOnUiThread(new Runnable()
	{
		public void run()
		{
			button.performClick();
		}
	});
	getInstrumentation().waitForIdleSync();
	imageResult=(ImageView) activity.findViewById(R.id.imageView1);
	assertTrue("Should be giraffe", imageResult.getDrawable().equals(R.drawable.giraffe));
}
}
