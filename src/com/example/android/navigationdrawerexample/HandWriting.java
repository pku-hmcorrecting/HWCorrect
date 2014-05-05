package com.example.android.navigationdrawerexample;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;

abstract class Handwriting
{
	protected double speedrate;//速度倍乘参数
	protected int drawSteps = 130;
	////根据SAI画线法定义的“画笔直径/圆的间距”
	public abstract double getWidthFromSpeed(double v);
	abstract void paintOncanvas(double x1, double y1, double x2, double y2, double x3, double y3, double v1, double v2, Paint paint, Canvas canvas);
	abstract void setStart(int x, int y);
};

class usualPen extends Handwriting
{
	private double startWidth,endWidth, widthDelta;
	private double t,tt,ttt,u,uu,uuu,x,y;
	private Point control1 = new Point(), control2 = new Point();
	private double size;
	RectF oval = new RectF();
	public usualPen()
	{
		speedrate = 140;
	}
	public void setStart(int x, int y)
	{
		control1.x = x;
		control1.y = y;
	}
	public double getWidthFromSpeed(double v)
	{
		size = Math.exp(-1*v/600);
		size = size * speedrate + 0.5;
		if (size > 10) size = 10;
		size = 0.9;
		return size;
	}
	public void paintOncanvas(double x1, double y1, double x2, double y2, double x3, double y3, double v1, double v2, Paint paint, Canvas canvas)
	{
		 startWidth = getWidthFromSpeed(v1);
		 endWidth = getWidthFromSpeed(v2);
		 widthDelta = endWidth - startWidth;
		 control2.x = (int)(x2 + (x1 - x3)/4.0*0.6);
		 control2.y = (int)(y2 + (y1 - y3)/4.0*0.6);
		 for (int i = 0; i < drawSteps; i++) 
		 {  
			    // Calculate the Bezier (x, y) coordinate for this step.  
			    t = ((float) i) / drawSteps;  
			    tt = t * t;  
			    ttt = tt * t;  
			    u = 1 - t;  
			    uu = u * u;  
			    uuu = uu * u;  
			  
			    x = uuu * x1;  
			    x += 3 * uu * t * control1.x;  
			    x += 3 * u * tt * control2.x;  
			    x += ttt * x2;  
			  
			    y = uuu * y1;  
			    y += 3 * uu * t * control1.y;  
			    y += 3 * u * tt * control2.y;  
			    y += ttt * y2;  
			  
			    // Set the incremental stroke width and draw.  
			    paint.setStrokeWidth((float)(startWidth + ttt * widthDelta));  
			    canvas.drawPoint((float)x, (float)y, paint);  
		 } 
		 control1.x = (int)(x2 - (x1 - x3)/4.0*0.6);
		 control1.y = (int)(y2 - (y1 - y3)/4.0*0.6);
			     
	}
}