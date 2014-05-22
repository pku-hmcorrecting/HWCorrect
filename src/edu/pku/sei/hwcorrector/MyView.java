package edu.pku.sei.hwcorrector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

public class MyView extends ImageView {  
   int curPageNo;
   boolean DoDragAndZoom = false;
   boolean DoInit = false;
   DrawActivity mainactivity;
   
   /** ��Ϸ���� **/  
   Paint mPaint = new Paint();
   String comment = "";
   long curtime = 0, prevtime = 0;
   
   
   ArrayList<Bitmap> undobitmap = new ArrayList<Bitmap>();
   ArrayList<Bitmap> redobitmap = new ArrayList<Bitmap>();
   
   
   boolean hasCorrect = false;
   
   int color = 2;
   double size = 1.4;
   int bgcolor = Color.WHITE;
 
   
   /** ��Ϸ���� **/  
   public Canvas mCanvas = null;
   
   public Bitmap background = null;
   public Bitmap originalbg = null;

 
     
   /**��ͨ�ֱ�**/
   usualPen myUsualPen = new usualPen();
   
   private int historySize;
   
   /**��־˫�㴥���¼��Ƿ����**/
   private boolean doublepoint = false;
   
   /**���ڹ��챴��������**/
   private double prex, prey, prev,curv, preprex, preprey, preprev;  
   
   /**��¼ԭʼ���ڴ�С**/
   private int screen_W, screen_H;
   
   /**��¼ͼƬ�������**/
   private double max_W, min_W, max_H, min_H, bitmap_H, bitmap_W;
   
   /**��View��ǰ�������ұ߽�**/
   private double left = 0, right = -1, top = 0, bottom = -1;
   
   
   /**��ʱ����**/
   private double x, y, deltaX, deltaY;
   private double scale;
   
   /**��һ�������ڵ����ĵ�λ��**/
   private double curX, curY, preX, preY, curDistance, preDistance;
   
   /**���ñ���**/
   public void setBackground(Bitmap bm)
   {
	 
	  //background = Bitmap.createScaledBitmap(bm, bm.getWidth()+1, bm.getHeight()+1, true);
	  //background = Bitmap.createScaledBitmap(background, bm.getWidth(), bm.getHeight(), true);
	  background = bm.copy(Bitmap.Config.ARGB_8888, true);
	  //����bm����ɨ�Ժ�background������ʧ��ɴ��� ������С֮��������� 
	  bitmap_W = background.getWidth();
	  bitmap_H = background.getHeight();
	  
		  right = getWidth();
		  bottom = getHeight();
		  
		  max_W = right * 3;
		  max_H = bottom * 3;

		  min_W = right / 3;
		  min_H = bottom / 3;
		  right+=left;
		  bottom += top;
		  setFrame((int)left, (int)top, (int)right, (int)bottom);
	  mCanvas.setBitmap(background);
	  setImageBitmap(background);
   }
   
   private void resetBackground(Bitmap bm)
   {
	 
	  //background = Bitmap.createScaledBitmap(bm, bm.getWidth()+1, bm.getHeight()+1, true);
	  //background = Bitmap.createScaledBitmap(background, bm.getWidth(), bm.getHeight(), true);
	  background = bm.copy(Bitmap.Config.ARGB_8888, true);
	  //����bm����ɨ�Ժ�background������ʧ��ɴ��� ������С֮��������� 
	  
		  setFrame((int)left, (int)top, (int)right, (int)bottom);

	 

	  mCanvas.setBitmap(background);
	  //mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
	  setImageBitmap(background);
   }
   
@Override
	protected void onLayout(boolean changed, int left_, int top_, int right_,
			int bottom_) {
		super.onLayout(changed, left_, top_, right_, bottom_);
		if (right == 0) {
			top = top_;
			left = left_;
			bottom = bottom_;
			right = right_;
		}

	}
   
   
   /**���ÿɼ���Ļ����**/
   public void setscreen_W(int w)
   {
	   screen_W = w;
   }
   
   /**���ÿɼ���Ļ�߶�**/
   public void setscreen_H(int h)
   {
	   screen_H = h;
   }
   
   /**���캯��**/
   public MyView(Context context, AttributeSet attrs) {
       super(context, attrs);  
       /** ���õ�ǰViewӵ�п��ƽ��� **/
       this.setKeepScreenOn(true);
       this.setFocusable(true);  
       /** ���õ�ǰViewӵ�д����¼� **/  
       this.setFocusableInTouchMode(true);  
       /** �������� **/
       mCanvas = new Canvas();
       /** �������߻��� **/
       mPaint = new Paint();
       mCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
       /**���û��ʿ����**/  
       mPaint.setAntiAlias(true);  
       /**���ʵ�����**/  
       //mPaint.setStyle(Paint.Style.FILL);  
       mPaint.setStyle(Paint.Style.STROKE);  
       /**���û��ʱ�ΪԲ��״**/  
       mPaint.setFilterBitmap(true);
       mPaint.setStrokeCap(Paint.Cap.ROUND); 
       mPaint.setStrokeJoin(Paint.Join.ROUND); 
       mPaint.setStrokeWidth(1);  
       mPaint.setColor(Color.RED);
   } 


   @Override  
   /**���������¼�**/
   public boolean onTouchEvent(MotionEvent event) {
	   hasCorrect = true;
       /** �洢�¼�״̬ **/  
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
    	   //���һ��ʱ�����߲�ȫ
    	   }
    	   break;  
       }  
       prevtime = curtime;
       return true;  
   }  
   
     
   /**��ʼ��д**/
   public void startWriting(MotionEvent event)
   {
	   /**�������߹켣��� X Y����**/
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
   	   myUsualPen.setSize(size);
   }
   /**����δ��ɵ���д**/
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
   			//if (prev == 0) {prev = 0.7 * curv; break;}
   			curv = prev * 0.3 + curv * 0.7;
   			if (prev == 0&&preprev == 0) {prev = preprev = curv; break;}
   			//���ٶȾ��������仯
   			mCanvas.save();
   			myUsualPen.paintOncanvas( preprex, preprey,prex, prey, x, y, preprev, prev, mPaint, mCanvas);
   			//���챴��������
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
   
   /**���һ���Ѿ���������д**/
   public void finishWriting(MotionEvent event)
   {
	   mCanvas.save();
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
   			//���ٶȾ��������仯
   			mCanvas.save();
   			myUsualPen.paintOncanvas( preprex, preprey,prex, prey, x, y, preprev, prev, mPaint, mCanvas);
   			//���챴��������
   			preprex = prex;
   			preprey = prey;
   			preprev = prev;
   			prex = x;
   			prey = y;
   			prev = curv;
   			mCanvas.restore();
   			setImageBitmap(background);
   		}   
	   myUsualPen.paintOncanvas(preprex, preprey, prex, prey, prex, prey, prev, prev*1.4, mPaint, mCanvas);
	   mCanvas.restore();
	   //setImageBitmap(background);
	   resetBackground(background);
	   //postInvalidate();
   }
   
   
  /**��ʼ����һ����㴥���¼�**/
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
   
  /**�Զ�㴥���¼����д���**/
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
   /**��ⴰ���Ƿ�Ϸ�**/
   private void checkView()
   {
	   doublepoint = false;
   }
   public void undo() {
	   if (undobitmap.isEmpty()) return;
	   redobitmap.add(background);
	   Bitmap tmp = undobitmap.get(undobitmap.size()-1);
	   undobitmap.remove(undobitmap.size()-1);
	   setBackground(tmp);
   }
   
   public void redo() {
	   if (redobitmap.isEmpty()) return;
	   undobitmap.add(background);
	   Bitmap tmp = redobitmap.get(redobitmap.size()-1);
	   redobitmap.remove(redobitmap.size()-1);
	   setBackground(tmp);
   }
   
   public void clear() {
	   saveundolist();
	   hasCorrect = false;
   	   setBackground(originalbg);
   }
   
   public void saveundolist() {
	   Bitmap bm = background.copy(Config.ARGB_8888, true);
	   undobitmap.add(bm);
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
   
   public void changesize(double newsize) {
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
		Bitmap mbitmap = background;
		
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
			Toast toast=Toast.makeText(mainactivity.getApplicationContext(), "����Ϊ"+picname, Toast.LENGTH_SHORT);
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

       return background;
	
   }

 }  