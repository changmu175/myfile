package com.csipsimple.pjsip;

import java.util.ArrayList;
import java.util.List;

public class MissedCallsList {
	private int count = 0;
	private List<String> callers = null;
	private List<String> times = null;
	public static final String THIS_FILE = "MissedCallsList";
	
	public MissedCallsList() {
		callers = new ArrayList<>();
		times = new ArrayList<>();
	}
	
	public int size(){
		return count;
	}
	
	public void insert(String caller, String time){
		if(callers.contains(caller) && times.contains(time))
			return;
		callers.add(caller);
		times.add(time);
		count ++;
	}
	
	public String getCaller(int idx) {
		return callers.get(idx); 
	}
	
	public String getTime(int idx) {
		return times.get(idx);
	}

}
