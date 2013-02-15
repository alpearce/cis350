package edu.cis350.ipaint;

import java.util.ArrayList;
import edu.cis350.ipaint.PaletteView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class PaintBrushView extends View {
	static final int bgColor = Color.WHITE;
	protected Paint brushPaint;
	protected Canvas bgCanvas;
	//protected int brushColor;
	//protected int thickness;
	protected ArrayList<Point> pts;
	protected ArrayList<Path> paths;
	protected Path currPath;
	protected boolean newPath;
	
	public PaintBrushView(Context context) {
		super(context);	
		init();
		// TODO Auto-generated constructor stub
	}
	
	public PaintBrushView(Context c, AttributeSet a) {
		super(c,a);
		init();
	}
		
	protected void init(){
		setFocusable(true);
		brushPaint = new Paint();
		brushPaint.setAntiAlias(true);
		brushPaint.setStyle(Style.STROKE);
		bgCanvas = new Canvas();
		pts = new ArrayList<Point>();
		paths = new ArrayList<Path>();
		
		clear();
		
		brushPaint.setColor(PaletteView.drawingColor); //TODO: change		
		brushPaint.setStrokeWidth(PaletteView.drawingThickness);
	}
	
	void clear() {
		paths.removeAll(paths);
		pts.removeAll(pts);
		brushPaint.setColor(bgColor);
		bgCanvas.drawPaint(brushPaint);
		newPath = true;
		invalidate();
	}
	
	public void onDraw(Canvas c) {
		if (currPath != null) {
			if (newPath && !pts.isEmpty() && pts.get(0).x != 0) {
				currPath.moveTo(pts.get(0).x,pts.get(0).y);
				Log.d("drawing","points are:" + pts.get(0).x + " and " + pts.get(0).y);
				//currPath.setLastPoint(pts.get(0).x, pts.get(0).y);
				newPath = false;
				}
			else if (pts.size() > 1) {
				//for (int i = 1; i < pts.size(); i++) {				
					currPath.lineTo((float)pts.get(pts.size()-1).x, (float)pts.get(pts.size()-1).y);
					//currPath.setLastPoint(pts.get(pts.size()-1).x,pts.get(pts.size()-1).y);
				//}
					brushPaint.setColor(currPath.color);
					brushPaint.setStrokeWidth(currPath.thickness);
					c.drawPath(currPath, brushPaint); 
			}		 
		 }
		
		if (!paths.isEmpty()) {
			for (Path p : paths) {
				brushPaint.setColor(p.color);
				brushPaint.setStrokeWidth(p.thickness);
				c.drawPath(p, brushPaint);
			}
		}
	}
	
	public boolean onTouchEvent (MotionEvent e) {
		int x = (int)e.getX();
		int y = (int)e.getY();
		if (e.getAction() == MotionEvent.ACTION_DOWN ) {
			currPath = new Path();
			currPath.setColor(PaletteView.drawingColor);
			currPath.setThickness(PaletteView.drawingThickness);
			pts.add(new Point(x,y));
			invalidate();
			return true;
		}
		else if (e.getAction() == MotionEvent.ACTION_MOVE) {
			pts.add(new Point(x,y));
			invalidate();
			return true;
		}
		else if (e.getAction() == MotionEvent.ACTION_UP) {
			paths.add(currPath);
			pts.removeAll(pts);
			newPath = true;
			return true;
		}
		else {
			return false;
		}	
	}	
}

