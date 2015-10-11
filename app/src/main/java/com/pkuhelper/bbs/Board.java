package com.pkuhelper.bbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pkuhelper.lib.Constants;
import com.pkuhelper.lib.Editor;
import com.pkuhelper.lib.MyFile;
import com.pkuhelper.lib.webconnection.Parameters;

public class Board {
	String board, name, category;
	boolean anonymous;
	int total;
	public static HashMap<String, Board> boards=new HashMap<String, Board>();	
	public static ArrayList<String> favorite=new ArrayList<String>();
	public static void load() {
		boards.clear();
		try {
			String string=MyFile.getString(BBSActivity.bbsActivity, null, "bbs_allboards", null);
			JSONArray jsonArray=new JSONArray(string);
			int len=jsonArray.length();
			for (int i=0;i<len;i++) {
				JSONObject jsonObject=jsonArray.getJSONObject(i);
				String board=jsonObject.getString("board");
				boards.put(board, new Board(board, 
						jsonObject.getString("name"), jsonObject.optString("category"), 
						jsonObject.optInt("anonymous"), jsonObject.optInt("total")));
			}
		}
		catch (Exception e) {
			boards.clear();
		}
		if (boards.size()==0)
			reload();
		favorite.clear();
		try {
			String string=Editor.getString(BBSActivity.bbsActivity, "bbs_favorite");
			String[] strings=string.split(",");
			for (int i=0;i<strings.length;i++) {
				if (!"".equals(strings[i]))
					favorite.add(strings[i]);
			}
		}
		catch (Exception e) {}
		AllBoardsFragment.resetList();
	}
	public static void toggleFavorite(String board) {
		if (favorite.contains(board)) favorite.remove(board);
		else favorite.add(board);
		favorite.remove("(null)");
		comfirmFavorite();
		SearchFragment.updateSpinner();
	}
	private static void comfirmFavorite() {
		String string="";
		Iterator<String> iterator=favorite.iterator();
		while (iterator.hasNext()) {
			String string2=iterator.next();
			if (!"".equals(string2)) string+=string2+",";
		}
		if (string.length()!=0) string=string.substring(0, string.length()-1);
		Editor.putString(BBSActivity.bbsActivity, "bbs_favorite", string);
	}
	@SuppressWarnings("unchecked")
	public static void reload() {
		new RequestingTask(BBSActivity.bbsActivity, "正在获取所有版面...", "http://www.bdwm.net/client/bbsclient.php?type=getallboards", 
				Constants.REQUEST_BBS_GET_ALL_BOARDS).execute(new ArrayList<Parameters>());
	}
 	public static void save(String string) {
		boards.clear();
		try {
			JSONArray jsonArray=new JSONArray(string);
			int len=jsonArray.length();
			for (int i=0;i<len;i++) {
				JSONObject jsonObject=jsonArray.getJSONObject(i);
				String board=jsonObject.getString("board");
				boards.put(board, new Board(board, 
						jsonObject.getString("name"), jsonObject.optString("category"), 
						jsonObject.optInt("anonymous"), jsonObject.optInt("total")));
			}
			MyFile.putString(BBSActivity.bbsActivity, null, "bbs_allboards", string);
		}
		catch (Exception e) {
			boards.clear();
		}
		AllBoardsFragment.show();
	}
	
	public Board(String _board, String _name, String _category, int _anonymous,
			int total) {
		board=new String(_board);
		name=new String(_name);
		category=new String(_category);
		anonymous=_anonymous==1?true:false;
		this.total=total;
	}
}
