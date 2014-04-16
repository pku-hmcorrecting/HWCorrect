package com.example.android.navigationdrawerexample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.example.android.navigationdrawerexample.DisplayHomeworksActivity.PlaceholderFragment;

import android.app.Activity;  
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;  
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Matrix;
import android.graphics.Paint;  
import android.graphics.Path;  
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;  
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;  
import android.view.SubMenu;
import android.view.SurfaceHolder;  
import android.view.SurfaceView;  
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;  
import android.view.WindowManager;  
import android.view.SurfaceHolder.Callback;  
import android.widget.EditText;
import android.widget.Toast;
 
 
public class DrawActivity extends Activity {  
 
    static MyView mAnimView = null;  
 
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
	    super.onCreate(savedInstanceState);  
	    // 全屏显示窗口  
	    //requestWindowFeature(Window.FEATURE_NO_TITLE);  
	    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
	        //WindowManager.LayoutParams.FLAG_FULLSCREEN);  
	    // 显示自定义的游戏View  
	    //fuck u
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    mAnimView = new MyView(this);  
	    setContentView(mAnimView);
    }
    /**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_draw,
					container, false);
			return rootView;
		}
	}

    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_MENU)  
        {             
            super.openOptionsMenu();  
              
            return true;  
        }  
        else  
        {   
            return super.onKeyDown(keyCode, event);  
        }         
    } 
    
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
     // TODO Auto-generated method stub
    	menu.add(0, 0, 0, "撤销");
    	menu.add(0, 2, 2, "恢复");
        menu.add(0, 4, 4, "清空");  
        menu.add(0, 6, 6, "保存");
        SubMenu graphcolor = menu.addSubMenu(0, 8, 8, "更改图案颜色..");
        SubMenu bgcolor = menu.addSubMenu(0, 9, 9, "更改背景颜色..");
        menu.add(0, 10, 10, "选择背景图片..");
        menu.add(0, 12, 12, "拍照取背景..");
        ///////////////////////////////
        menu.add(0, 44, 44, "下一页");
        menu.add(0, 46, 46, "上一页");
        ////////////////////////////////
        graphcolor.add(1, 15, 15, "黑色");
        graphcolor.add(1, 16, 16, "白色");
        graphcolor.add(1, 17, 17, "红色");
        graphcolor.add(1, 18, 18, "蓝色");
        graphcolor.add(1, 19, 19, "绿色");
        graphcolor.add(1, 20, 20, "紫色");
        graphcolor.add(1, 21, 21, "黄色");
        graphcolor.add(1, 22, 22, "灰色");
        bgcolor.add(2, 34, 34, "黑色");
        bgcolor.add(2, 35, 35, "白色");
        bgcolor.add(2, 36, 36, "红色");
        bgcolor.add(2, 37, 37, "蓝色");
        bgcolor.add(2, 38, 38, "绿色");
        bgcolor.add(2, 39, 39, "紫色");
        bgcolor.add(2, 40, 40, "黄色");
        bgcolor.add(2, 41, 41, "灰色");
        return super.onCreateOptionsMenu(menu);   
    }
    
    @Override  
    public boolean onMenuItemSelected(int featureId, MenuItem item) {  
        // TODO Auto-generated method stub
    	if(item.getItemId()==0)
    	{
    		mAnimView.undo();
    	}
    	if(item.getItemId()==2)
    	{
    		mAnimView.redo();
    	}
        if(item.getItemId()==4)  
        {  
        	mAnimView.clear();
        }            
        if(item.getItemId()==6)  
        {  
        	mAnimView.save();
        }
        if(item.getItemId()==10)
        {
        	getbgpic();
        }
        if(item.getItemId()==12)
        {
        	camera();
        }
        if(item.getItemId()>=15 && item.getItemId()<=22)  
        {  
        	mAnimView.changecolor(item.getItemId()-15);
        }
        if(item.getItemId()>=34 && item.getItemId()<=41)  
        {  
        	mAnimView.changebg(item.getItemId()-34);
        }
        ////////////////////
        if (item.getItemId()==44) {
			//nextpage();
		}
        if (item.getItemId()==46) {
			//prevpage();
		}
        ////////////////
        /*if(item.getItemId()==30)
        {
        	final EditText inputServer = new EditText(this);
            inputServer.setFocusable(true);
            inputServer.setText(mAnimView.comment);
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("设置评语").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer).setPositiveButton("确定", new DialogInterface.OnClickListener() {
        		@Override
        		public void onClick(DialogInterface dialog, int which) {
        			mAnimView.comment = inputServer.getText().toString();
        		}
        	}).setNegativeButton("取消", null).show();       	
        }*/
        return super.onMenuItemSelected(featureId, item);  
    }  
 
    public void getbgpic() {
    	Intent intent=new Intent();
        //制定内容的类型为图像
        intent.setType("image/*");
        //制定调用系统内容的action
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //显示系统相册
        startActivityForResult(intent, 1);        
    }
    
    public void camera() {
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
        startActivityForResult(intent, 2);
    }
    
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK && requestCode == 1) {
        	mAnimView.background = null;
        	Bundle extras = data.getExtras();
        	Bitmap bitmap = null;
        	if (null != extras){
        		bitmap = (Bitmap)extras.get("data");
        		if (bitmap.getWidth() > bitmap.getHeight()) {
        			Matrix m = new Matrix();
        			m.setRotate(90, (float)bitmap.getWidth()/2, (float)bitmap.getHeight()/2);
        			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getHeight(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getWidth());
        			mAnimView.background = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        		}
        		else {
        			Matrix m = new Matrix();
           			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getWidth(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getHeight());
        			mAnimView.background = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        		}
        	} else {
            	Uri uri = data.getData();
            	if (uri != null) {
            		bitmap = BitmapFactory.decodeFile(uri.getPath());
            		if (bitmap.getWidth() > bitmap.getHeight()) {
            			Matrix m = new Matrix();
            			m.setRotate(90, (float)bitmap.getWidth()/2, (float)bitmap.getHeight()/2);
               			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getHeight(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getWidth());
            			mAnimView.background = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            		}
            		else {
            			Matrix m = new Matrix();
               			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getWidth(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getHeight());
            			mAnimView.background = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            		}
            	}
        	}
        }
        else if (resultCode == RESULT_OK && requestCode == 2) {
        	Bundle bundle = data.getExtras();  
            Bitmap bitmap = (Bitmap) bundle.get("data");
            if (bitmap.getWidth() > bitmap.getHeight()) {
    			Matrix m = new Matrix();
    			m.setRotate(90, (float)bitmap.getWidth()/2, (float)bitmap.getHeight()/2);
    			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getHeight(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getWidth());
    			mAnimView.background = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    		}
    		else {
    			Matrix m = new Matrix();
       			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getWidth(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getHeight());
    			mAnimView.background = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    		}
        }
        super.onActivityResult(requestCode, resultCode, data);  
    }   
    
    
    
    public class MyView extends SurfaceView implements Callback,Runnable {  
 
     /**每50帧刷新一次屏幕**/    
    public static final int TIME_IN_FRAME = 5;   
    
    /** 游戏画笔 **/  
    Paint mPaint[] = new Paint[8];
    Paint mTextPaint = new Paint();
    String comment = "";
    
    SurfaceHolder mSurfaceHolder = null;
    
    private ArrayList<Path> PathList = new ArrayList();
    private ArrayList<Paint> PaintList = new ArrayList();
    
    private ArrayList<Path> Pathundo = new ArrayList();
    private ArrayList<Paint> Paintundo = new ArrayList();
    
    int color = 2;
    int bgcolor = Color.WHITE;
 
    /** 控制游戏更新循环 **/  
    boolean mRunning = false;  
    
    /** 游戏画布 **/  
    Canvas mCanvas = null;
    
    Bitmap background = null;

    /**控制游戏循环**/  
    boolean mIsRunning = false;  
      
    /**曲线方向**/  
    private Path mPath;  
      
    private float mposX, mposY;  
      
    public MyView(Context context) {  
        super(context);  
        /** 设置当前View拥有控制焦点 **/  
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
        for (int i = 0; i < mPaint.length; i++) {
            mPaint[i] = new Paint();    
            /**设置画笔抗锯齿**/  
            mPaint[i].setAntiAlias(true);  
            /**画笔的类型**/  
            mPaint[i].setStyle(Paint.Style.STROKE);  
            /**设置画笔变为圆滑状**/  
            mPaint[i].setStrokeCap(Paint.Cap.ROUND);  
            /**设置线的宽度**/  
            mPaint[i].setStrokeWidth(3);  
            /**创建路径对象**/  
        }
        mPaint[0].setColor(Color.BLACK);
        mPaint[1].setColor(Color.WHITE);
        mPaint[2].setColor(Color.RED);
        mPaint[3].setColor(Color.BLUE);
        mPaint[4].setColor(Color.GREEN);
        mPaint[5].setColor(Color.argb(255, 255, 0, 255));
        mPaint[6].setColor(Color.argb(255, 255, 255, 0));
        mPaint[7].setColor(Color.GRAY);
        
        /** 创建文字画笔 **/
        //mTextPaint.setTextSize(60);
        //mTextPaint.setColor(Color.RED);
    } 
 
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        /** 拿到触摸的状态 **/  
        int action = event.getAction();  
        float x = event.getX();  
        float y = event.getY();  
        switch (action) {  
        // 触摸按下的事件  
        case MotionEvent.ACTION_DOWN:  
        /**设置曲线轨迹起点 X Y坐标**/  
        	PathList.add(new Path());
        	PaintList.add(mPaint[color]);
        	mPath = PathList.get(PathList.size()-1);
        	mPath.moveTo(x, y);  
        	break;  
        // 触摸移动的事件  
        case MotionEvent.ACTION_MOVE:  
        /**设置曲线轨迹**/  
        //参数1 起始点X坐标  
        //参数2 起始点Y坐标  
        //参数3 结束点X坐标  
        //参数4 结束点Y坐标  
        	mPath.quadTo(mposX, mposY, x, y);  
        	break;  
        // 触摸抬起的事件  
        case MotionEvent.ACTION_UP:  
        /**按键抬起后清空路径轨迹**/ 
        //mPath.reset();
        	Pathundo.clear();
        	Paintundo.clear();
        break;  
        }  
       //记录当前触摸X Y坐标  
        mposX = x;  
        mposY = y;  
        return true;  
    }  
          
    private void Draw(Canvas canvas) {  
        /**清空画布**/  
    	canvas.drawColor(bgcolor);
    	if (background != null) canvas.drawBitmap(background, 0, 0, null);
        /**绘制曲线**/  
        int i;
        for (i = 0; i < PathList.size(); i++) {          
        	canvas.drawPath(PathList.get(i), PaintList.get(i));
        }
        
        //canvas.drawText(comment, 0, getWindowManager().getDefaultDisplay().getHeight(), mTextPaint);
        /**记录当前触点位置**/  
        //mCanvas.drawText("当前触笔 X：" + mposX, 0, 20,mTextPaint);  
        //mCanvas.drawText("当前触笔 Y：" + mposY, 0, 40,mTextPaint);  
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
    
    public void undo() {
    	if (!PathList.isEmpty()) {
    		Path tmppath = PathList.remove(PathList.size()-1);
    		Pathundo.add(tmppath);
    		Paint tmppaint = PaintList.remove(PaintList.size()-1);
    		Paintundo.add(tmppaint);
    	}
    }
    
    public void redo() {
    	if (!Pathundo.isEmpty()) {
    		Path tmppath = Pathundo.remove(Pathundo.size()-1);
    		PathList.add(tmppath);
    		Paint tmppaint = Paintundo.remove(Paintundo.size()-1);
    		PaintList.add(tmppaint);    		
    	}
    }
    
    public void clear() {
    	for (Path p:PathList) {
    		p.reset();
    	}
    	PathList.clear();
    	PaintList.clear();
    	Pathundo.clear();
    	Paintundo.clear();
    	background = null;
    }
    
    public void changecolor(int newcolor) {
    	color = newcolor;
    	//PathList.add(new Path());
    	//PaintList.add(mPaint[color]);
    	//mPath = PathList.get(PathList.size()-1);
    	//mTextPaint.setColor(mPaint[color].getColor());
    }
    
    public void changebg(int newcolor) {
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
        long startTime = System.currentTimeMillis();  
 
        /** 在这里加上线程安全锁 **/  
        synchronized (mSurfaceHolder) {  
            /** 拿到当前画布 然后锁定 **/  
            mCanvas = mSurfaceHolder.lockCanvas();  
            Draw(mCanvas);
            /** 绘制结束后解锁显示在屏幕上 **/  
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);  
        }  
 
        /** 取得更新游戏结束的时间 **/  
        long endTime = System.currentTimeMillis();  
 
        /** 计算出游戏一次更新的毫秒数 **/  
        int diffTime = (int) (endTime - startTime);  
 
        /** 确保每次更新时间为50帧 **/  
        while (diffTime <= TIME_IN_FRAME) {  
            diffTime = (int) (System.currentTimeMillis() - startTime);  
            /** 线程等待 **/  
            Thread.yield();  
        }  
 
        }  
 
    }
    
    public void save() {           
		Bitmap mbitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
		Canvas savecv = new Canvas(mbitmap);
		Draw(savecv);
		
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
			Toast toast=Toast.makeText(getApplicationContext(), "保存为"+picname, Toast.LENGTH_SHORT);
			toast.show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
    }
    
    }  
} 