package edu.cis350.ipaint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class PaletteView extends View {
	protected final int thin = 2;
	protected final int thick = 6;
	protected ShapeDrawable red;
	protected ShapeDrawable yellow;
	protected ShapeDrawable green;
	protected ShapeDrawable blue;
	protected ShapeDrawable black;
	protected int square_px = 60;
	protected int text_y = 100;
	protected int text1_x = 20;
	protected int text2_x = 100;
	protected Display disp;
	protected Paint paint;
	protected int colors_top = 10;
	protected static int drawingColor;
	protected static int drawingThickness;
	
	public PaletteView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	
	public PaletteView(Context c, AttributeSet a) {
		super(c,a);
		init();
	}
	
	protected void init() {
		int buf = 20;
		setFocusable(true);
		//initialize brush to black, thin lines
		drawingColor = Color.BLACK;
		drawingThickness = 2;
		
		red = new ShapeDrawable(new RectShape());
		red.getPaint().setColor(Color.RED);
		red.setBounds(buf, 10, buf + square_px, 10+square_px);
		
		yellow = new ShapeDrawable(new RectShape());
		yellow.getPaint().setColor(Color.YELLOW);
		yellow.setBounds(buf+square_px, 10, buf+square_px + square_px, 10+square_px);
		
		green = new ShapeDrawable(new RectShape());
		green.getPaint().setColor(Color.GREEN);
		green.setBounds(buf+2*square_px, 10, buf+2*square_px + square_px, 10+square_px);
		
		blue = new ShapeDrawable(new RectShape());
		blue.getPaint().setColor(Color.BLUE);
		blue.setBounds(buf+ 3*square_px, 10, buf+3*square_px + square_px, 10+square_px);
		
		black = new ShapeDrawable(new RectShape());
		black.getPaint().setColor(Color.BLACK);
		black.setBounds(buf+4*square_px, 10, buf+4*square_px + square_px, 10+square_px);

		paint = new Paint();
		paint.setColor(Color.BLACK); 
		paint.setTextSize(20); 
		
	}
	
	protected void onDraw(Canvas canvas) {
		red.draw(canvas);
		yellow.draw(canvas);
		green.draw(canvas);
		blue.draw(canvas);
		black.draw(canvas);
		
		canvas.drawText("THICK", text1_x, text_y, paint); 
		canvas.drawText("thin", text2_x, text_y, paint);
	}
	
	public boolean onTouchEvent (MotionEvent e) {
		Log.v("Palette View", "got a motion event" + e.getAction());
		Rect thickBounds = new Rect();
		paint.getTextBounds("THICK", 0, 4, thickBounds);
		Rect thinBounds = new Rect();
		paint.getTextBounds("thin", 0, 3, thinBounds);
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			float ex = e.getX(); float ey = e.getY();
			Log.v("coords","x, y, red bounds" + ex + ey + red.getBounds().left + red.getBounds().right);
			if (ey > colors_top && ey < colors_top + square_px) {
				if (ex > red.getBounds().left && ex < red.getBounds().right) {
					Log.v("coords","yellow" + yellow.getBounds().left + yellow.getBounds().right);
					drawingColor = Color.RED;
				}
				else if (ex > yellow.getBounds().left && ex < yellow.getBounds().right) {
					drawingColor = Color.YELLOW;
					Log.v("PV", "yellow");
				}
				else if (ex > green.getBounds().left && ex < green.getBounds().right) {
					drawingColor = Color.GREEN;
				}
				else if (ex > blue.getBounds().left && ex < blue.getBounds().right) {
					drawingColor = Color.BLUE;
				}
				else if (ex > black.getBounds().left && ex < black.getBounds().right) {
					drawingColor = Color.BLACK;
				}
			}
			else if (ex > text2_x + thinBounds.left && ex < text2_x + thinBounds.right &&
					ey > text_y + thinBounds.top && ey < text_y + thinBounds.bottom) {
				drawingThickness = thin;
			}
			else if (ex > text1_x + thickBounds.left && ex < text1_x + thickBounds.right 
					&& ey > text_y + thickBounds.top && ey < text_y + thickBounds.bottom) {
				drawingThickness = thick;
			}
		}
		return false;		
	}

	
}
