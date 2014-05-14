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
	/** SD���Ƿ����**/ 
	private boolean hasSD = false; 
	/** ��ҵ����·��**/ 
	private String basePath;
	private int studentId;

	private int pageSum;
	private MyView myView;
	public void setMyView(MyView myView_)
	{
		myView = myView_;
	};
	public HomeworkFileManager(DrawActivity drawActivity, int teacherId, 
			int courseId, int year, int month, int day, int studentId) throws IOException {
		this.drawActivity = drawActivity;
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (!hasSD) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("�����SD����")
			.setPositiveButton("ȷ��", null);
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
			Bitmap bitmap = BitmapFactory.decodeFile(basePath  + "//1.png");
			drawActivity.setBackgroundImg(bitmap);
			drawActivity.setTitle(studentId + "����ҵ - 1/" + pageSum);
		}
		else {
			f.mkdirs();
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("��ҵ�����ڣ�")
			.setPositiveButton("ȷ��", null);
			b2.setCancelable(false);
			b2.create();
			b2.show();
			pageSum = 0;
			return;
		}
	}

	public void getPrevPage() throws IOException {
    	int prevPageNo = myView.curPageNo - 1;
    	if (prevPageNo < 1) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("�Ѿ��ǵ�һҳ")
			.setPositiveButton("ȷ��", null);
			b2.setCancelable(false);
			b2.create();
			b2.show();
			return;
    	}
    	boolean needToSave = myView.hasCorrect;
    	if (needToSave)
    		saveCurPage();
    	myView.clear();
    	myView.curPageNo = prevPageNo;
    	Bitmap bitmap = BitmapFactory.decodeFile(basePath  + "//" + prevPageNo + ".png");
    	drawActivity.setBackgroundImg(bitmap);
    	drawActivity.setTitle(studentId + "����ҵ - " + prevPageNo + "/" + pageSum);
    	if (needToSave) {
    		Toast toast=Toast.makeText(drawActivity.getApplicationContext(), "��"
        			+ (myView.curPageNo + 1) + "ҳ�Ѿ�����", Toast.LENGTH_SHORT);
    		toast.show();
    	}
    }
    
    public void getNextPage() throws Exception {
    	int nextPageNo = myView.curPageNo + 1;
    	if (nextPageNo > pageSum) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("�Ѿ������һҳ")
			.setPositiveButton("ȷ��", null);
			b2.setCancelable(false);
			b2.create();
			b2.show();
			return;
    	}
    	boolean needToSave = myView.hasCorrect;
    	if (needToSave)
    		saveCurPage();
    	myView.clear();
    	myView.curPageNo = nextPageNo;
    	Bitmap bitmap = BitmapFactory.decodeFile(basePath  + "//" + nextPageNo + ".png");
    	drawActivity.setBackgroundImg(bitmap);
    	drawActivity.setTitle(studentId + "����ҵ - " + nextPageNo + "/" + pageSum);
    	if (needToSave) {
    		Toast toast=Toast.makeText(drawActivity.getApplicationContext(), "��"
        			+ (myView.curPageNo - 1) + "ҳ�Ѿ�����", Toast.LENGTH_SHORT);
    		toast.show();
    	}
    }
    
    public void saveCurPage() {
    	Bitmap bitmap = myView.getCurPageBitmap();
    	try {
 			FileOutputStream fos = new FileOutputStream(new File(basePath  + "//" + myView.curPageNo + ".png"));
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
