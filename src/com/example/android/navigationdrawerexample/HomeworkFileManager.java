package com.example.android.navigationdrawerexample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

public class HomeworkFileManager {
	DrawActivity drawActivity;
	/** SD卡是否存在**/ 
	private boolean hasSD = false; 
	/** 作业基本路径**/ 
	private String basePath;
	private int studentId;

	private int pageSum;
	
	public HomeworkFileManager(DrawActivity drawActivity, int teacherId, 
			int courseId, int year, int month, int day, int studentId) throws IOException {
		this.drawActivity = drawActivity;
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (!hasSD) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("请插入SD卡！")
			.setPositiveButton("确定", null);
			b2.setCancelable(false);
			b2.create();
			b2.show();
			return;
		}
		this.studentId = studentId;
		basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "//Drawer//hws//" + teacherId + "//" + courseId + "//" +
				year + "//" + month + "//" + day + "//" + studentId;
		File f = new File(basePath);
		if (f.exists()) {
			pageSum = f.listFiles().length;
			Bitmap bitmap = BitmapFactory.decodeFile(basePath  + "//1.jpg");
			drawActivity.setBackgroundImg(bitmap);
			drawActivity.setTitle(studentId + "的作业 - 1/" + pageSum);
		}
		else {
			f.mkdirs();
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("作业不存在！")
			.setPositiveButton("确定", null);
			b2.setCancelable(false);
			b2.create();
			b2.show();
			pageSum = 0;
			return;
		}
	}

	public void getPrevPage() throws IOException {
    	int prevPageNo = MyView.curPageNo - 1;
    	if (prevPageNo < 1) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("已经是第一页")
			.setPositiveButton("确定", null);
			b2.setCancelable(false);
			b2.create();
			b2.show();
			return;
    	}
    	boolean needToSave = MyView.hasCorrect;
    	if (needToSave)
    		saveCurPage();
    	MyView.clear();
    	MyView.curPageNo = prevPageNo;
    	Bitmap bitmap = BitmapFactory.decodeFile(basePath  + "//" + prevPageNo + ".jpg");
    	drawActivity.setBackgroundImg(bitmap);
    	drawActivity.setTitle(studentId + "的作业 - " + prevPageNo + "/" + pageSum);
    	if (needToSave) {
    		Toast toast=Toast.makeText(drawActivity.getApplicationContext(), "第"
        			+ (MyView.curPageNo + 1) + "页已经保存", Toast.LENGTH_SHORT);
    		toast.show();
    	}
    }
    
    public void getNextPage() throws Exception {
    	int nextPageNo = MyView.curPageNo + 1;
    	if (nextPageNo > pageSum) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("已经是最后一页")
			.setPositiveButton("确定", null);
			b2.setCancelable(false);
			b2.create();
			b2.show();
			return;
    	}
    	boolean needToSave = MyView.hasCorrect;
    	if (needToSave)
    		saveCurPage();
    	MyView.clear();
    	MyView.curPageNo = nextPageNo;
    	Bitmap bitmap = BitmapFactory.decodeFile(basePath  + "//" + nextPageNo + ".jpg");
    	drawActivity.setBackgroundImg(bitmap);
    	drawActivity.setTitle(studentId + "的作业 - " + nextPageNo + "/" + pageSum);
    	if (needToSave) {
    		Toast toast=Toast.makeText(drawActivity.getApplicationContext(), "第"
        			+ (MyView.curPageNo - 1) + "页已经保存", Toast.LENGTH_SHORT);
    		toast.show();
    	}
    }
    
    public void saveCurPage() {
    	Bitmap bitmap = MyView.getCurPageBitmap();
    	try {
 			FileOutputStream fos = new FileOutputStream(new File(basePath  + "//" + MyView.curPageNo + ".jpg"));
 			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
 			fos.flush();
 			fos.close();
 		} catch (FileNotFoundException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
    }
}
