package edu.pku.sei.hwcorrector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.R.string;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

public class HomeworkFileManager {
	DrawActivity drawActivity;

    private String teacherId;
	private String courseId;
	private String year;
	private String month;
	private String day;
	private String studentId;
	
	private int prevPageNo;
	private int nextPageNo;
	private Bitmap pageBitmap;
	private Bitmap tmpBitmap;
	private int tmpPage;
	
	private int pageSum;
	private MyView myView;
	public void setMyView(MyView myView_)
	{
		myView = myView_;
	};
	
	public HomeworkFileManager(DrawActivity drawActivity, String teacherId, 
			String courseId, String year, String month, String day, String studentId, String pageNum) {
		this.drawActivity = drawActivity;
		this.teacherId = teacherId;
		this.courseId = courseId;
		this.year = year;
		this.month = month;
		this.day = day;
		this.studentId = studentId;
		pageSum = Integer.parseInt(pageNum);
		new GetCurHWFileTask().execute(null, null);
	}

	public void getPrevPage() throws IOException {
    	prevPageNo = myView.curPageNo - 1;
    	if (prevPageNo < 1) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("已经是第一页")
			.setPositiveButton("确定", null);
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
    	new GetCurHWFileTask().execute(null, null);
    	drawActivity.setTitle(studentId + "的作业 - " + prevPageNo + "/" + pageSum);
    }
    
    public void getNextPage() throws Exception {
    	nextPageNo = myView.curPageNo + 1;
    	if (nextPageNo > pageSum) {
			AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
			.setTitle("Message").setMessage("已经是最后一页")
			.setPositiveButton("确定", null);
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
    	new GetCurHWFileTask().execute(null, null);
    	drawActivity.setTitle(studentId + "的作业 - " + nextPageNo + "/" + pageSum);
    }
    
    public void saveCurPage() {
    	tmpBitmap = myView.getCurPageBitmap();
    	tmpPage = myView.curPageNo;
    	new PostCurHWFileTask().execute(null, null);
    }
//    public void saveCurPage() {
//    	Bitmap bitmap = myView.getCurPageBitmap();
//    	try {
// 			FileOutputStream fos = new FileOutputStream(new File(basePath  + "//" + myView.curPageNo + ".png"));
// 			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
// 			fos.flush();
// 			fos.close();
// 		} catch (FileNotFoundException e) {
// 			// TODO Auto-generated catch block
// 			e.printStackTrace();
// 		} catch (IOException e) {
// 			// TODO Auto-generated catch block
// 			e.printStackTrace();
// 		}
//    }
    
    private class GetCurHWFileTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				String urlString = MainActivity.BASEURL_STRING + "GetHWFile?tid=" + teacherId + "&cid=" +
						courseId + "&year=" + year + "&month=" + month + "&day=" + day + 
						"&sid=" + studentId + "&page=" + myView.curPageNo;
				System.out.println(urlString);
				URL url = new URL(urlString);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			    conn.setDoInput(true);
			    conn.connect();
			    InputStream is = conn.getInputStream();   
			    pageBitmap = BitmapFactory.decodeStream(is);   
			    is.close();
			    return "OK";
			} catch (Exception e) {
				return "error";
			}
		}
		
		@Override
		protected void onPostExecute(String state) {
			if (state == "OK") {
				drawActivity.setBackgroundImg(pageBitmap);
				drawActivity.setOriginalBackground();
			}
			else {
				AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
				.setTitle("Message").setMessage("作业获取失败！")
				.setPositiveButton("确定", null);
				b2.setCancelable(false);
				b2.create();
				b2.show();
				return;
			}
		}
	}
    
    private class PostCurHWFileTask extends AsyncTask<Void, Void, String> {

		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... arg0) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(MainActivity.BASEURL_STRING + "CorrectedHWUpload");
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  

	        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	  
	        
	        tmpBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
	        byte[] data = bos.toByteArray(); 
	        // sending a String param;  
	        //entity.addPart("myParam", new StringBody("my value"));  
	  
	        // sending a Image;  
	        // note here, that you can send more than one image, just add another param, same rule to the String;  
	  
	        entity.addPart("file", new ByteArrayBody(data, tmpPage + ".png"));  
	        
	        httppost.setEntity(entity);  
	        HttpResponse response;
			try {
				response = httpclient.execute(httppost);
				if (response.getStatusLine().getStatusCode() == 200) {
		        	return "OK";
				}
				else {
					return "error";
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error";
			}
		}
		
		@Override
		protected void onPostExecute(String state) {
			if (state == "OK") {
				Toast toast=Toast.makeText(drawActivity.getApplicationContext(), "第"
	        			+ tmpPage + "页已经保存", Toast.LENGTH_SHORT);
	    		toast.show();
			}
			else {
				AlertDialog.Builder b2 = new AlertDialog.Builder(drawActivity)
				.setTitle("Message").setMessage("作业保存失败！")
				.setPositiveButton("确定", null);
				b2.setCancelable(false);
				b2.create();
				b2.show();
				return;
			}
		}
	}
}
