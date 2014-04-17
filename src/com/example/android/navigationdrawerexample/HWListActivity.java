package com.example.android.navigationdrawerexample;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class HWListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hwlist);
		Intent intent = getIntent();
		int listNumber = intent.getIntExtra(MainActivity.LIST_NUMBER_MESSAGE, 1);
		setUpList(listNumber);
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

}
