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
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

public class MyView extends SurfaceView implements Callback,Runnable {  
   static int curPageNo;
    /**每50帧刷新一次屏幕**/    
   static final int TIME_IN_FRAME = 50;
   
   static DrawActivity mainactivity;
   
   /** 游戏画笔 **/  
   static Paint mPaint = new Paint();
   static String comment = "";
   
   static SurfaceHolder mSurfaceHolder = null;
   
   private static float[] xpoint = new float[20000];
   private static float[] ypoint = new float[20000];
   private static float[] movespeed = new float[20000];
   
   static ArrayList<Bitmap> undobitmap = new ArrayList();
   static ArrayList<Bitmap> redobitmap = new ArrayList();
   
   static boolean stopdraw = true;
   
   static boolean hasCorrect = false;
   
   static int color = 2;
   static int size = 5;
   static int bgcolor = Color.WHITE;

   /** 控制游戏更新循环 **/  
   static boolean mRunning = false;  
   
   /** 游戏画布 **/  
   static Canvas mCanvas = null;
   
   static Bitmap background = null;

   /**控制游戏循环**/  
   static boolean mIsRunning = false;  
     
   /**曲线方向**/  
   static private Path mPath;      
   static private float mposX, mposY; 
   static private float con1, con2, con3, con4; 
   static private float prex, prey;  
   static private float preprex, preprey;
   
   private long pretime, prepretime, deltatime;
   
   int contr = 0, pointThreeFlag = 0;
   static int ini_v = 0, ini_width = 3, pointcnt = 0, prepointcnt = 0;
     
   public MyView(Context context, AttributeSet attrs) {  
       super(context, attrs);  
       /** 设置当前View拥有控制焦点 **/
       this.setKeepScreenOn(true);
       this.setFocusable(true);  
       /** 设置当前View拥有触摸事件 **/  
       this.setFocusableInTouchMode(true);  
       /** 拿到SurfaceHolder对象 **/  
       mSurfaceHolder = this.getHolder();  
       /** 将mSurfaceHolder添加到Callback回调函数中 **/  
       mSurfaceHolder.addCallback(this);  
       /** 创建画布 **/
       mCanvas = new Canvas();
       /** 创建曲线画笔 **/
       mPaint = new Paint();    
       /**设置画笔抗锯齿**/  
       mPaint.setAntiAlias(true);  
       /**画笔的类型**/  
       mPaint.setStyle(Paint.Style.STROKE);  
       /**设置画笔变为圆滑状**/  
       mPaint.setStrokeCap(Paint.Cap.ROUND);  
       /**设置线的宽度**/  
       mPaint.setStrokeWidth(size);
       mPaint.setColor(Color.RED);
       mPath = new Path();
       
       /** 创建文字画笔 **/
       //mTextPaint.setTextSize(60);
       //mTextPaint.setColor(Color.RED);
   } 

   @Override  
   public boolean onTouchEvent(MotionEvent event) {
	   hasCorrect = true;
       /** 拿到触摸的状态 **/  
       int action = event.getAction();  
       float x = event.getX();  
       float y = event.getY();
       long time = System.currentTimeMillis();
       switch (action) {  
       // 触摸按下的事件  
       case MotionEvent.ACTION_DOWN:  
       /**设置曲线轨迹起点 X Y坐标**/  
       	mPath.moveTo(x, y);
       	saveundolist();
       	stopdraw = false;
    	prex = x;
    	prey = y;
    	pointcnt = 0;
    	xpoint[pointcnt] = x;
    	ypoint[pointcnt] = y;
    	prepointcnt = pointcnt;
    	movespeed[pointcnt] = 0;
    	pretime = time;
    	pointcnt++;
       	break;  
       // 触摸移动的事件  
       case MotionEvent.ACTION_MOVE:  
       /**设置曲线轨迹**/  
       //参数1 起始点X坐标  
       //参数2 起始点Y坐标  
       //参数3 结束点X坐标  
       //参数4 结束点Y坐标  
    	   if(contr == 1){
           	con3 = (float)(prex + (preprex - x)/4.0*0.6);
           	 
           	con4 = (float)(prey + (preprey - y)/4.0*0.6);
           	
           	if (pointThreeFlag < 1) pointThreeFlag++;
           	else{
               	deltatime = pretime - prepretime;
               	
           		xpoint[pointcnt] = con3;
               	ypoint[pointcnt] = con4;
               	pointcnt++;
           		xpoint[pointcnt] = prex;
               	ypoint[pointcnt] = prey;
               	double dis = (prex - preprex)*(prex - preprex)
               			+ (prey - preprey)*(prey - preprey);
               	movespeed[pointcnt] = ((float)Math.sqrt(dis))/(deltatime);
               	movespeed[pointcnt - 2] = movespeed[pointcnt - 1]
               			= movespeed[pointcnt];
               	pointcnt++;
               	
           		mPath.cubicTo(con1, con2, con3, con4, prex, prey);
           	}

           	con1 = (float)(prex - (preprex - x)/4.0*0.6);
           	con2 = (float)(prey - (preprey - y)/4.0*0.6);
           	xpoint[pointcnt] = con1;
           	ypoint[pointcnt] = con2;
           	pointcnt++;
           	}
           	contr = (contr + 1)%2;
       	break;  
       // 触摸抬起的事件  
       case MotionEvent.ACTION_UP:  
       /**按键抬起后清空路径轨迹**/ 
       //mPath.reset();
    	   if(true){
       		con3 = (float)(prex + (preprex - x)/4.0*0.6);
          	 
           	con4 = (float)(prey + (preprey - y)/4.0*0.6);
           	
           	if (pointThreeFlag < 1) pointThreeFlag++;
           	else{
               	deltatime = pretime - prepretime;
               	
           		xpoint[pointcnt] = con3;
               	ypoint[pointcnt] = con4;
               	pointcnt++;
           		xpoint[pointcnt] = prex;
               	ypoint[pointcnt] = prey;
               	double dis = (prex - preprex)*(prex - preprex)
               			+ (prey - preprey)*(prey - preprey);
               	movespeed[pointcnt] = ((float)Math.sqrt(dis))/(deltatime);
               	movespeed[pointcnt - 2] = movespeed[pointcnt - 1]
               			= movespeed[pointcnt];
               	pointcnt++;
           		mPath.cubicTo(con1, con2, con3, con4, prex, prey);
           	}

           	con1 = (float)(prex - (preprex - x)/4.0*0.6);
           	con2 = (float)(prey - (preprey - y)/4.0*0.6);
           	xpoint[pointcnt] = con1;
           	ypoint[pointcnt] = con2;
           	pointcnt++;
           }
    	contr = 0;
    	pointThreeFlag = 0;
       	ini_v = 0;
    	ini_width = 3;
    	mPath.reset();
    	stopdraw = true;
       break;  
       }  
      //记录当前触摸X Y坐标  
       if(contr == 0){
           preprex = prex;
           preprey = prey;
           prex = x;
           prey = y;
           }
           if(contr == 0){
           prepretime = pretime;
           pretime = time;
       }
       return true;  
   }  
         
   private static void Draw(Canvas canvas) {
	   if (canvas == null) return;
	   canvas.drawColor(bgcolor);
	   if (background != null) canvas.drawBitmap(background, 0, 0, null); 
   	   if (stopdraw) return;
       drawcanvas(mCanvas);
       Bitmap mbitmap = Bitmap.createBitmap(mainactivity.getWindowManager().getDefaultDisplay().getWidth(), mainactivity.getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
       Canvas tmpcv = new Canvas(mbitmap);
       tmpcv.drawColor(bgcolor);
       if (background != null) tmpcv.drawBitmap(background, 0, 0, null);
       drawcanvas(tmpcv);
       background = mbitmap;
  
   }  
   
   public static void drawcanvas(Canvas canvas) {
	   int i = 0, j;
	   float t, tt, ttt, u, uu, uuu;
	   float drawx, drawy;
	   	
	   while(i < pointcnt - 5){
	   		//movespeed[i + 3] = (float)(movespeed[i]*0.5 + movespeed[i + 3]*0.5);
	   		for (j = 0; j < 100; j++){
	   			t = ((float) j) / 100;  
	   			tt = t * t;  
	   			ttt = tt * t;  
	   			u = 1 - t;  
	   			uu = u * u;  
	   			uuu = uu * u;  
	 
				    drawx = uuu * xpoint[i];  
				    drawx += 3 * uu * t * xpoint[i + 1];  
				    drawx += 3 * u * tt * xpoint[i + 2];  
				    drawx += ttt * xpoint[i + 3];  
				  
				    drawy = uuu * ypoint[i];  
				    drawy += 3 * uu * t * ypoint[i + 1];  
				    drawy += 3 * u * tt * ypoint[i + 2];  
				    drawy += ttt * ypoint[i + 3];
				      
				    mPaint.setStrokeWidth((float)(size + 120/(8 + movespeed[i]*movespeed[i]) + 
	 						t*(120/(movespeed[i + 3]*movespeed[i + 3] + 8) - 
	 								120/(movespeed[i]*movespeed[i] + 8))));  
	   			canvas.drawPoint(drawx, drawy, mPaint);
	   		}
	   		i += 3;
	   		if (i >= pointcnt - 5){
	   			for (j = 0; j < 100; j++){
	       			t = ((float) j) / 100;  
	       			tt = t * t;  
	       			ttt = tt * t;  
	       			u = 1 - t;  
	       			uu = u * u;  
	       			uuu = uu * u;  
	     
	   			    drawx = uuu * xpoint[i];  
	   			    drawx += 3 * uu * t * xpoint[i + 1];  
	   			    drawx += 3 * u * tt * xpoint[i + 2];  
	   			    drawx += ttt * xpoint[i + 3];  
	   			  
	   			    drawy = uuu * ypoint[i];  
	   			    drawy += 3 * uu * t * ypoint[i + 1];  
	   			    drawy += 3 * u * tt * ypoint[i + 2];  
	   			    drawy += ttt * ypoint[i + 3];
	   			      
	   			    mPaint.setStrokeWidth((float)(3 + 140/(8 + movespeed[i]*movespeed[i]) - 
	     						0.8*t*140/(movespeed[i]*movespeed[i] + 8)));  
	       			canvas.drawPoint(drawx, drawy, mPaint);
	   			}
	   		}
	   	}
   }
     
   @Override  
   public void surfaceChanged(SurfaceHolder holder, int format, int width,  
       int height) {  

   }  

   @Override  
   public void surfaceCreated(SurfaceHolder holder) {  
       /**开始游戏主循环线程**/  
       mIsRunning = true;  
       new Thread(this).start();  
   }  

   @Override  
   public void surfaceDestroyed(SurfaceHolder holder) {  
       mIsRunning = false;  
   }
   
   public static void undo() {
	   if (undobitmap.isEmpty()) return;
	   redobitmap.add(background);
	   Bitmap tmp = undobitmap.get(undobitmap.size()-1);
	   undobitmap.remove(undobitmap.size()-1);
	   background = tmp;
   }
   
   public static void redo() {
	   if (redobitmap.isEmpty()) return;
	   undobitmap.add(background);
	   Bitmap tmp = redobitmap.get(redobitmap.size()-1);
	   redobitmap.remove(redobitmap.size()-1);
	   background = tmp;
   }
   
   public static void clear() {
	   saveundolist();
	   hasCorrect = false;
   	   background = null;
   }
   
   public static void saveundolist() {
	   undobitmap.add(background);
	   if (undobitmap.size() > 12) undobitmap.remove(0);
       redobitmap.clear();
   }
   
   public static void changecolor(int newcolor) {
   	   mPaint.setColor(newcolor);
   	//PathList.add(new Path());
   	//PaintList.add(mPaint[color]);
   	//mPath = PathList.get(PathList.size()-1);
   	//mTextPaint.setColor(mPaint[color].getColor());
   }
   
   public static void changesize(int newsize) {
	   size = newsize;
   }
   
   public static void changebg(int newcolor) {
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
   

   @Override  
   public void run() {  
       while (mIsRunning) {  

       /** 取得更新游戏之前的时间 **/  
   //    long startTime = System.currentTimeMillis();  

       /** 在这里加上线程安全锁 **/  
       synchronized (mSurfaceHolder) {  
           /** 拿到当前画布 然后锁定 **/  
           mCanvas = mSurfaceHolder.lockCanvas();  
           if (mIsRunning) Draw(mCanvas);
           /** 绘制结束后解锁显示在屏幕上 **/
           if (mCanvas != null) mSurfaceHolder.unlockCanvasAndPost(mCanvas);  
       }  

       /** 取得更新游戏结束的时间 **/  
    //   long endTime = System.currentTimeMillis();  

       /** 计算出游戏一次更新的毫秒数 **/  
    //   int diffTime = (int) (endTime - startTime);  

       /** 确保每次更新时间为50帧 **/  
     //  while (diffTime <= 20) {  
     //      diffTime = (int) (System.currentTimeMillis() - startTime);  
           /** 线程等待 **/  
     //      Thread.yield();  
     //  }  

       }  

   }
   
   public static void save() {           
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
   
   public static Bitmap getCurPageBitmap() {
	   Bitmap mbitmap = Bitmap.createBitmap(mainactivity.getWindowManager().getDefaultDisplay().getWidth(), mainactivity.getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
       if (background != null) mbitmap = background;
       else {
       	Canvas tmpcv = new Canvas(mbitmap);
       	tmpcv.drawColor(bgcolor);
       }
       return mbitmap;
	
}

   }  
