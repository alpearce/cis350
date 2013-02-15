package edu.cis350.ipaint;

import android.graphics.Color;

public class Path extends android.graphics.Path {
	public int color;
	public int thickness;
	
	public Path() {
		super();
		color = Color.BLACK; //default to 2px black lines
		thickness = 2;
	}
	
	/*public void setPaint(Paint p) {
		this.ppaint = p;
	}*/
	
	public void setColor(int c) {
		this.color = c;
	}
	
	public void setThickness(int t) {
		this.thickness = t;
	}

}
