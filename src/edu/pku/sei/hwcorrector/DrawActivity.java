package edu.pku.sei.hwcorrector;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
 
public class DrawActivity extends Activity implements OnClickListener {  
    
    Button option = null;
    HomeworkFileManager hwFileManager;
    private String teacherId;
	private String courseId;
	private String year;
	private String month;
	private String day;
	private String studentId;
	private String pageNum;
	private MyView myView;
 
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        //requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
            //WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.activity_draw);
        myView = (MyView)findViewById(R.id.view3d);
        myView.mainactivity = this;
        myView.curPageNo = 1;
        Intent intent = this.getIntent();
	    teacherId = intent.getStringExtra("teacherId");
	    courseId = intent.getStringExtra("courseId");
	    year = intent.getStringExtra("year");
	    month = intent.getStringExtra("month");
	    day = intent.getStringExtra("day");
	    studentId = intent.getStringExtra("studentId");
	    pageNum = intent.getStringExtra("pageNum");
	    /*
	    courseId = intent.getIntExtra("courseId", 1);
	    year = intent.getIntExtra("year", 2014);
	    month = intent.getIntExtra("month", 5);
	    day = intent.getIntExtra("day", 14);
	    studentId = intent.getIntExtra("studentId", 1100012844);*/
		hwFileManager = new HomeworkFileManager(this, teacherId, courseId, year, month, day, studentId, pageNum);
		hwFileManager.setMyView(myView);
        //option = (Button)findViewById(R.id.button);
        //option.setOnClickListener(this);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_draw,
                    container, false);
            return rootView;
        }
    }*/
     
    @Override  
    public void onClick(View v) {  
        if (v == option) {  
        	openOptionsMenu();
        } 
    }
    /*
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
    */
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
    	  // TODO Auto-generated method stub
    	menu.add(0, 0, 0, "撤销");
    	menu.add(0, 2, 2, "恢复");
        menu.add(0, 4, 4, "清空");  
        menu.add(0, 6, 6, "保存");
        SubMenu size = menu.addSubMenu(0, 7, 7, "更改图案粗细..");
        SubMenu graphcolor = menu.addSubMenu(0, 8, 8, "更改图案颜色..");
       // SubMenu bgcolor = menu.addSubMenu(0, 9, 9, "更改背景颜色..");
        menu.add(0, 10, 10, "选择背景图片..");
        menu.add(0, 12, 12, "拍照取背景..");
        graphcolor.add(1, 15, 15, "黑色");
        graphcolor.add(1, 16, 16, "白色");
        graphcolor.add(1, 17, 17, "红色");
        graphcolor.add(1, 18, 18, "蓝色");
        graphcolor.add(1, 19, 19, "绿色");
        graphcolor.add(1, 20, 20, "紫色");
        graphcolor.add(1, 21, 21, "黄色");
        graphcolor.add(1, 22, 22, "灰色");
        size.add(3, 24, 24, "很细");
        size.add(3, 25, 25, "比较细");
        size.add(3, 26, 26, "中等");
        size.add(3, 27, 27, "比较细");
        size.add(3, 28, 28, "很粗");
        /*bgcolor.add(2, 34, 34, "黑色");
        bgcolor.add(2, 35, 35, "白色");
        bgcolor.add(2, 36, 36, "红色");
        bgcolor.add(2, 37, 37, "蓝色");
        bgcolor.add(2, 38, 38, "绿色");
        bgcolor.add(2, 39, 39, "紫色");
        bgcolor.add(2, 40, 40, "黄色");
        bgcolor.add(2, 41, 41, "灰色");*/
        menu.add(0, 44, 44, "下一页");
        menu.add(0, 46, 46, "上一页");
        menu.add(0, 127, 127, "退出");
        return super.onCreateOptionsMenu(menu);   

    }
    
    @Override  
    public boolean onMenuItemSelected(int featureId, MenuItem item) {  
        // TODO Auto-generated method stub
    	if(item.getItemId()==0)
    	{
    		myView.undo();
    	}
    	if(item.getItemId()==2)
    	{
    		myView.redo();
    	}
        if(item.getItemId()==4)  
        {  
        	myView.clear();
        	myView.curPageNo--;
        	try {
				hwFileManager.getNextPage();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }            
        if(item.getItemId()==6)  
        {  
        	myView.save();
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
        	if(item.getItemId()==15)myView.changecolor(Color.BLACK);
        	else if(item.getItemId()==16)myView.changecolor(Color.WHITE);
        	else if(item.getItemId()==17)myView.changecolor(Color.RED);
        	else if(item.getItemId()==18)myView.changecolor(Color.BLUE);
        	else if(item.getItemId()==19)myView.changecolor(Color.GREEN);
        	else if(item.getItemId()==20)myView.changecolor(0xFFFF00FF);
        	else if(item.getItemId()==21)myView.changecolor(0xFFFFFF00);
        	else if(item.getItemId()==22)myView.changecolor(Color.GRAY);
        }
        if(item.getItemId()>=24&&item.getItemId()<=28)
        {
        	if(item.getItemId()==24)myView.changesize(1);
        	else if(item.getItemId()==25)myView.changesize(3);
        	else if(item.getItemId()==26)myView.changesize(5);
        	else if(item.getItemId()==27)myView.changesize(9);
        	else if(item.getItemId()==28)myView.changesize(15);
        }
        if(item.getItemId()>=34 && item.getItemId()<=41)  
        {  
        	myView.changebg(item.getItemId()-34);
        }
        if(item.getItemId()==127)
        {
        	finish();
        	System.exit(0);
        }
        if (item.getItemId() == 44) {
        	try {
				hwFileManager.getNextPage();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//next page;
		}
        if (item.getItemId() == 46) {
        	try {
				hwFileManager.getPrevPage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	//prev page;
		}
        /*if(item.getItemId()==30)
        {
        	final EditText inputServer = new EditText(this);
            inputServer.setFocusable(true);
            inputServer.setText(myView.comment);
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("ÉèÖÃÆÀÓï").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer).setPositiveButton("È·¶¨", new DialogInterface.OnClickListener() {
        		@Override
        		public void onClick(DialogInterface dialog, int which) {
        			myView.comment = inputServer.getText().toString();
        		}
        	}).setNegativeButton("È¡Ïû", null).show();       	
        }*/
        return super.onMenuItemSelected(featureId, item);  
    }  
 
    public void getbgpic() {
    	Intent intent=new Intent();
        //ÖÆ¶¨ÄÚÈÝµÄÀàÐÍÎªÍ¼Ïñ
        intent.setType("image/*");
        //ÖÆ¶¨µ÷ÓÃÏµÍ³ÄÚÈÝµÄaction
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //ÏÔÊ¾ÏµÍ³Ïà²á
        startActivityForResult(intent, 1);        
    }
    
    public void camera() {
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
    	  
        startActivityForResult(intent, 2);
    }
    
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK && requestCode == 1) {
        	myView.setscreen_H(getWindowManager().getDefaultDisplay().getHeight());
        	myView.setscreen_W(getWindowManager().getDefaultDisplay().getWidth());
        	myView.background = null;
        	Bundle extras = data.getExtras();
        	Bitmap bitmap = null;
        	if (null != extras){
        		bitmap = (Bitmap)extras.get("data");
        		if (bitmap.getWidth() > bitmap.getHeight()) {
        			Matrix m = new Matrix();
        			m.setRotate(90, (float)bitmap.getWidth()/2, (float)bitmap.getHeight()/2);
        			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getHeight(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getWidth());
        			myView.setBackground(bitmap);
        		}
        		else {
        			Matrix m = new Matrix();
           			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getWidth(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getHeight());
           			myView.setBackground(bitmap);
        		}
        	} else {
            	Uri uri = data.getData();
            	if (uri != null) {
            		bitmap = BitmapFactory.decodeFile(uri.getPath());
            		if (bitmap.getWidth() > bitmap.getHeight()) {
            			Matrix m = new Matrix();
            			m.setRotate(90, (float)bitmap.getWidth()/2, (float)bitmap.getHeight()/2);
               			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getHeight(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getWidth());
               			//myView.setBackground1(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true));
               			myView.setBackground(bitmap);
            		}
            		else {
            			Matrix m = new Matrix();
               			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getWidth(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getHeight());
               			myView.setBackground(bitmap);
            		}
            	}
        	}
        	
        	myView.saveundolist();
        }
        else if (resultCode == RESULT_OK && requestCode == 2) {
        	Bundle bundle = data.getExtras();  
            Bitmap bitmap = (Bitmap) bundle.get("data");
            if (bitmap.getWidth() > bitmap.getHeight()) {
    			Matrix m = new Matrix();
    			m.setRotate(90, (float)bitmap.getWidth()/2, (float)bitmap.getHeight()/2);
    			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getHeight(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getWidth());
    			myView.setBackground(bitmap);
    		}
    		else {
    			Matrix m = new Matrix();
       			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getWidth(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getHeight());
       			myView.setBackground(bitmap);
    		}
            myView.saveundolist();
        }
        super.onActivityResult(requestCode, resultCode, data);  
    }   
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() == KeyEvent.ACTION_DOWN)) {  
	        finish();
	        System.exit(0);
	        return true;
	    }          
	    return super.onKeyDown(keyCode, event);
	}
   public void setBackgroundImg(Bitmap bitmap) {
		if (bitmap.getWidth() > bitmap.getHeight()) {
			Matrix m = new Matrix();
			m.setRotate(90, (float)bitmap.getWidth()/2, (float)bitmap.getHeight()/2);
  			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getHeight(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getWidth());
  			myView.setBackground(bitmap);
		}
		else {
			Matrix m = new Matrix();
  			m.postScale((float)getWindowManager().getDefaultDisplay().getWidth()/bitmap.getWidth(), (float)getWindowManager().getDefaultDisplay().getHeight()/bitmap.getHeight());
			if (myView  != null){
				myView.setBackground(bitmap);
			}
			else
			{
				int a = 1;
				int b = 2;
			}
		}
   }
    
} 