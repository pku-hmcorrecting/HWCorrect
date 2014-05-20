package edu.pku.sei.hwcorrector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HWListActivity extends Activity {
	private String stuList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hwlist);
		Intent intent = getIntent();
		int listNumber = intent.getIntExtra(MainActivity.LIST_NUMBER_MESSAGE, 1);
		//setUpList(listNumber);
		new GetHWNumberTask().execute(null, null);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	public void setUpList(int listNumber){
    	//绑定Layout里面的ListView  
        ListView list = (ListView) findViewById(R.id.CertainHWList);  
          
        //生成动态数组，加入数据  
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        for(int i=0;i<listNumber;i++)  
        {  
            HashMap<String, Object> map = new HashMap<String, Object>(); 
            map.put("CertainHWsItemTitle", "Student "+i);
            listItem.add(map);  
        }  
        //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
            R.layout.certain_hwlist_item,//ListItem的XML实现  
            //动态数组与ImageItem对应的子项          
            new String[] {"CertainHWsItemTitle"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.CertainHWsItemTitle}  
        );  
         
        //添加并且显示  
        list.setAdapter(listItemAdapter);  
          
        //添加点击  
        list.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
            	////
            	invokeDrawActivity();
            }  
        });   
    }
	
	public void setUpList(){
		
		int listNum = 0;
		String []temp = null;
		if ("null".equals(stuList)) {
			listNum = 0;
		}
		else {
			temp = stuList.split(",");
			for (String s : temp) {
				listNum++;
			}
		}
		
    	//绑定Layout里面的ListView  
        ListView list = (ListView) findViewById(R.id.CertainHWList);  
          
        //生成动态数组，加入数据  
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        for(int i=0;i<listNum;i++)  
        {  
            HashMap<String, Object> map = new HashMap<String, Object>(); 
            map.put("CertainHWsItemTitle", temp[i]);
            listItem.add(map);  
        }  
        //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
            R.layout.certain_hwlist_item,//ListItem的XML实现  
            //动态数组与ImageItem对应的子项          
            new String[] {"CertainHWsItemTitle"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.CertainHWsItemTitle}  
        );  
         
        //添加并且显示  
        list.setAdapter(listItemAdapter);  
          
        //添加点击  
        list.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
            	////
            	invokeDrawActivity();
            }  
        });   
    }
	
	void invokeDrawActivity(){
		Intent intent = new Intent(this, DrawActivity.class);
		startActivityForResult(intent, RESULT_OK);
		//startActivity(intent);
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
        //Log.i(TAG, result);
		return;
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hwlist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			View rootView = inflater.inflate(R.layout.fragment_display_homeworks,
					container, false);
			return rootView;
		}
	}
	
	private class GetHWNumberTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			try {
				String urlString = "http://115.27.19.253:8888/HWServer/GetHWNumber?tid=1&cid=1&year=2014&month=5&day=14";
				System.out.println(urlString);
				URL url = new URL(urlString);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			    conn.setDoInput(true);
			    conn.connect();
			    InputStream is = conn.getInputStream();
			    stuList = inputStream2String(is);
			    is.close();
			    return "OK";
			} catch (Exception e) {
				return "error";
			}
		}
		
		@Override
		protected void onPostExecute(String state) {
			if (state == "OK") {
				setUpList();
			}
			else {
				AlertDialog.Builder b2 = new AlertDialog.Builder(HWListActivity.this)
				.setTitle("Message").setMessage("已交学生列表失败！")
				.setPositiveButton("确定", null);
				b2.setCancelable(false);
				b2.create();
				b2.show();
				return;
			}
		}
		
		String inputStream2String(InputStream is) throws IOException{
		   BufferedReader in = new BufferedReader(new InputStreamReader(is));
		   StringBuffer buffer = new StringBuffer();
		   String line = "";
		   while ((line = in.readLine()) != null){
		     buffer.append(line);
		   }
		   return buffer.toString();
		}
	}

}
