package edu.pku.sei.hwcorrector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Environment;

public class HomeworkListManager  extends Activity {
	/** SD卡是否存在**/ 
	private static boolean hasSD = false; 
	/** 作业列表基本路径**/ 
	
	public static List<String> getHomeworkLists(int tId, int cId) {
		List<String> ans = new ArrayList<String>();
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (!hasSD) {
			ans.add("NOSD");
			return ans;
		}
		String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "//Drawer//hws//" + tId + "//" + cId;
		File f = new File(basePath);
		if (f.exists() && f.listFiles().length > 0) {
			String[] years = f.list();
			int yearsSum = years.length;
			for (int i = 0; i < yearsSum; ++i) {
				File curYear = new File(basePath + "//" + years[i]);
				String[] months = curYear.list();
				int monthsSum = months.length;
				for (int j = 0; j < monthsSum; ++j) {
					File curMonth = new File(basePath + "//" + years[i] + "//" + months[j]);
					String[] days = curMonth.list();
					int daysSum = days.length;
					for (int k = 0; k < daysSum; ++k)
						ans.add(years[i] + "." + months[j] + "." + days[k]);
				}
			}
			return ans;
		}
		else {
			ans.add("NODIR");
			return ans;
		}
	}
	
	public static List<String> selectOneList(int tId, int cId, int y, int m, int d) {
		List<String> ans = new ArrayList<String>();
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (!hasSD) {
			ans.add("NOSD");
			return ans;
		}
		String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"//Drawer//hws//" + tId + "//" + cId + "//" + y + "//" + m + "//" + d;
		File f = new File(basePath);
		if (f.exists() && f.listFiles().length > 0) {
			String[] studentIDs = f.list();
			int sum = studentIDs.length;
			for (int i = 0; i < sum; ++i)
				ans.add(studentIDs[i]);
			return ans;
		}
		else {
			ans.add("NODIR");
			return ans;
		}
	}
}
