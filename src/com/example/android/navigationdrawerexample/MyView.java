package com.example.android.navigationdrawerexample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.ImageView;
import android.widget.Toast;

public class MyView extends ImageView {  
   int curPageNo;
   boolean DoDragAndZoom = false;
   DrawActivity mainactivity;
   
   /** 游戏画笔 **/  
   Paint mPaint = new Paint();
   String comment = "";
   long curtime = 0, prevtime = 0;
   
   
   ArrayList<Bitmap> undobitmap = new ArrayList<Bitmap>();
   ArrayList<Bitmap> redobitmap = new ArrayList<Bitmap>();
   
   
   boolean hasCorrect = false;
   
   int color = 2;
   int size = 5;
   int bgcolor = Color.WHITE;
 
   
   /** 游戏画布 **/  
   public Canvas mCanvas = null;
   
   public Bitmap background = null;

 
     
   /**普通钢笔**/
   usualPen myUsualPen = new usualPen();
   
   private int historySize;
   
   /**标志双点触摸事件是否结束**/
   private boolean doublepoint = false;
   
   /**用于构造贝塞尔曲线**/
   private double prex, prey, prev,curv, preprex, preprey, preprev;  
   
   /**记录原始窗口大小**/
   private int screen_W, screen_H;
   
   /**记录图片各项参数**/
   private double max_W, min_W, max_H, min_H, bitmap_H, bitmap_W;
   
   /**该View当前上下左右边界**/
   private double left = -1, right = -1, top = -1, bottom = -1;
   
   
   /**临时变量**/
   private double x, y, deltaX, deltaY;
   private double scale;
   
   /**上一个和现在的中心点位置**/
   private double curX, curY, preX, preY, curDistance, preDistance;
   
   /**设置背景**/
   public void setBackground(Bitmap bm)
   {
	 
	  background = Bitmap.createScaledBitmap(bm, bm.getWidth()-1, bm.getHeight()-1, false);
	  bitmap_W = background.getWidth();
	  bitmap_H = background.getHeight();

	  max_W = bitmap_W * 3;
	  max_H = bitmap_H * 3;

	  min_W = bitmap_W / 3;
	  min_H = bitmap_H / 3;
	  setFrame(0, 0, (int)bitmap_W, (int)bitmap_H);
	  mCanvas.setBitmap(background);
	  setImageBitmap(background);
   }
   
	@Override
	protected void onLayout(boolean changed, int left_, int top_, int right_,
			int bottom_) {
		super.onLayout(changed, left_, top_, right_, bottom_);
		if (top == -1) {
			top = top_;
			left = left_;
			bottom = bottom_;
			right = right_;
		}

	}
   
   
   /**设置可见屏幕宽度**/
   public void setscreen_W(int w)
   {
	   screen_W = w;
   }
   
   /**设置可见屏幕高度**/
   public void setscreen_H(int h)
   {
	   screen_H = h;
   }
   
   /**构造函数**/
   public MyView(Context context, AttributeSet attrs) {  
       super(context, attrs);  
       /** 设置当前View拥有控制焦点 **/
       this.setKeepScreenOn(true);
       this.setFocusable(true);  
       /** 设置当前View拥有触摸事件 **/  
       this.setFocusableInTouchMode(true);  
 
       /** 创建画布 **/
       mCanvas = new Canvas();
       /** 创建曲线画笔 **/
       mPaint = new Paint();
       mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
       /**设置画笔抗锯齿**/  
       mPaint.setAntiAlias(true);  
       /**画笔的类型**/  
       //mPaint.setStyle(Paint.Style.FILL);  
       mPaint.setStyle(Paint.Style.STROKE);  
       /**设置画笔变为圆滑状**/  
       mPaint.setStrokeCap(Paint.Cap.ROUND); 
       mPaint.setStrokeJoin(Paint.Join.ROUND); 
       mPaint.setStrokeWidth(1);  
       mPaint.setColor(Color.RED);
       
       
   } 


   @Override  
   /**处理触屏事件**/
   public boolean onTouchEvent(MotionEvent event) {
	   hasCorrect = true;
       /** 存储事件状态 **/  
       int action = event.getAction();  
       curtime = event.getEventTime();
       switch (action & MotionEvent.ACTION_MASK) {  
       
       case MotionEvent.ACTION_DOWN:  
    	   startWriting(event);
       	   break;  
       case MotionEvent.ACTION_POINTER_DOWN:
    	   startZoomAndDrag(event);
    	   break;
       case MotionEvent.ACTION_MOVE:   
    	   if (doublepoint)
    		   doZoomAndDrag(event);
    	   else
    		   continueWriting(event);
    	   break;
    	   
       case MotionEvent.ACTION_UP:  
    	   if (doublepoint)
    		   checkView();
    	   else
    	   {
    		   continueWriting(event);
    		   finishWriting(event);
    	   //最后一笔时将曲线补全
    	   }
    	   break;  
       }  
       prevtime = curtime;
       return true;  
   }  
   
     
   /**开始书写**/
   public void startWriting(MotionEvent event)
   {
	   /**设置曲线轨迹起点 X Y坐标**/ 
	   double x = translateX(event.getX());  
       double y = translateY(event.getY());
       saveundolist();
       prex = x;
       prey = y;
       preprex = x;
       preprey = y;
   	   prev = 0;
   	   preprev = 0;
   	   myUsualPen.setStart((int)x, (int)y);
   }
   /**继续未完成的书写**/
   public void continueWriting(MotionEvent event)
   {
	   double x = translateX(event.getX());  
       double y = translateY(event.getY());
       historySize = event.getHistorySize();
   	   for (int i = 0;i < historySize - 1;i ++)
   		{
   			x = translateX(event.getHistoricalX(i));
   			y = translateY(event.getHistoricalY(i));
   			curtime = event.getHistoricalEventTime(i);
   			curv = Math.sqrt((x-prex)*(x-prex) + (y-prey)*(y-prey));
   			curv = curv / ((double)(curtime - prevtime)/1000);
   			if (prev == 0) {prev = 0.7 * curv; break;}
   			curv = prev * 0.3 + curv * 0.7;
   			//让速度尽量连续变化
   			mCanvas.save();
   			myUsualPen.paintOncanvas( preprex, preprey,prex, prey, x, y, preprev, prev, mPaint, mCanvas);
   			//构造贝塞尔曲线
   			preprex = prex;
   			preprey = prey;
   			preprev = prev;
   			prex = x;
   			prey = y;
   			prev = curv;
   			mCanvas.restore();
   			setImageBitmap(background);
   		}   
   }
   
   /**完成一个已经结束的书写**/
   public void finishWriting(MotionEvent event)
   {
	   mCanvas.save();
	   myUsualPen.paintOncanvas(preprex, preprey, prex, prey, prex, prey, prev, prev*1.4, mPaint, mCanvas);
	   mCanvas.restore();
	   setImageBitmap(background);
   }
   
   
  /**开始进行一个多点触摸事件**/
   private void startZoomAndDrag(MotionEvent event)
   {
	   if(!DoDragAndZoom) return;
	   x = (event.getX(0) - event.getX(1));
	   y = (event.getY(0) - event.getY(1));
	   preX = (event.getX(0) + event.getX(1)) / 2 + left;
	   preY = (event.getY(0) + event.getY(1)) / 2 + top;
	   preDistance = Math.sqrt(x * x + y * y);
	   doublepoint = true;
   }
   
  /**对多点触控事件进行处理**/
   private void doZoomAndDrag(MotionEvent event)
   {
	   /*left = this.getLeft();
	   right = this.getRight();
	   top = this.getTop();
	   bottom = this.getBottom();*/
	   if(!DoDragAndZoom) return;
	   if (event.getPointerCount() != 2) return;
	   x = (event.getX(0) - event.getX(1));
	   y = (event.getY(0) - event.getY(1));
	   curX = (event.getX(0) + event.getX(1)) / 2 + left;
	   curY = (event.getY(0) + event.getY(1)) / 2 + top;
	   curDistance = Math.sqrt(x * x + y * y);
	   scale = curDistance/preDistance;
	   
	   if (Math.abs(curDistance - preDistance) > 3)
	   {
		   deltaX = (this.getWidth() * Math.abs(1 - scale)) / 4;
		   deltaY = (this.getHeight() * Math.abs(1 - scale)) / 4;
		   if (scale > 1)
		   {
			   left -= deltaX;
			   right += deltaX;
			   top -= deltaY;
			   bottom += deltaY;
		   }
		   else
		   {
			   left += deltaX;
			   right -= deltaX;
			   top += deltaY;
			   bottom -= deltaY;
		   }
		   preX = curX;
		   preY = curY;
		   preDistance = curDistance;
		   this.setFrame((int)left, (int)top, (int)right ,(int)bottom);
		   return;
	   }
	   deltaX = curX - preX;
	   deltaY =	curY - preY;
	   left += deltaX;
	   right += deltaX;
	   top += deltaY;
	   bottom += deltaY;
	   preX = curX;
	   preY = curY;
	   
	   
	   this.setFrame((int)left, (int)top, (int)right ,(int)bottom);
	   
	  
   }
   
   private double translateX(double x)
   {
	   return x * (bitmap_W) / (right - left);
   }
   
   private double translateY(double y)
   {
	   return y * (bitmap_H) / (bottom - top);
   }
   /**检测窗口是否合法**/
   private void checkView()
   {
	   doublepoint = false;
   }
   public void undo() {
	   if (undobitmap.isEmpty()) return;
	   redobitmap.add(background);
	   Bitmap tmp = undobitmap.get(undobitmap.size()-1);
	   undobitmap.remove(undobitmap.size()-1);
	   background = tmp;
	   setImageBitmap(background);
   }
   
   public void redo() {
	   if (redobitmap.isEmpty()) return;
	   undobitmap.add(background);
	   Bitmap tmp = redobitmap.get(redobitmap.size()-1);
	   redobitmap.remove(redobitmap.size()-1);
	   background = tmp;
	   setImageBitmap(background);
   }
   
   public void clear() {
	   saveundolist();
	   hasCorrect = false;
   	   background = null;
   }
   
   public void saveundolist() {
	   undobitmap.add(background);
	   if (undobitmap.size() > 12) undobitmap.remove(0);
       redobitmap.clear();
   }
   
   public void changecolor(int newcolor) {
   	   mPaint.setColor(newcolor);
   	//PathList.add(new Path());
   	//PaintList.add(mPaint[color]);
   	//mPath = PathList.get(PathList.size()-1);
   	//mTextPaint.setColor(mPaint[color].getColor());
   }
   
   public void changesize(int newsize) {
	   size = newsize;
   }
   
   public void changebg(int newcolor) {
	   saveundolist();
   	if(newcolor == 0)bgcolor = Color.BLACK;
   	if(newcolor == 1)bgcolor = Color.WHITE;
   	if(newcolor == 2)bgcolor = Color.RED;
   	if(newcolor == 3)bgcolor = Color.BLUE;
   	if(newcolor == 4)bgcolor = Color.GREEN;
   	if(newcolor == 5)bgcolor = Color.argb(255, 255, 0, 255);
   	if(newcolor == 6)bgcolor = Color.argb(255, 255, 255, 0);
   	if(newcolor == 7)bgcolor = Color.GRAY;
   	background = null;
   }
   


   
   public void save() {           
		Bitmap mbitmap = Bitmap.createBitmap(mainactivity.getWindowManager().getDefaultDisplay().getWidth(), mainactivity.getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        if (background != null) mbitmap = background;
        else {
        	Canvas tmpcv = new Canvas(mbitmap);
        	tmpcv.drawColor(bgcolor);
        }
		
       File dir = new File("/mnt/sdcard/Drawer");
       if (!dir.exists()) {
       	dir.mkdir();
       }       
       String picname = ((Long)System.currentTimeMillis()).toString()+".png";
       try {
			FileOutputStream fos = new FileOutputStream(new File(dir.getPath() + "/" + picname));
			mbitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			Toast toast=Toast.makeText(mainactivity.getApplicationContext(), "保存为"+picname, Toast.LENGTH_SHORT);
			toast.show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
   }
   
   public Bitmap getCurPageBitmap() {
	   Bitmap mbitmap = Bitmap.createBitmap(mainactivity.getWindowManager().getDefaultDisplay().getWidth(), mainactivity.getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
       if (background != null) mbitmap = background;
       else {
       	Canvas tmpcv = new Canvas(mbitmap);
       	tmpcv.drawColor(bgcolor);
       }
       return mbitmap;
	
   }

 }  
