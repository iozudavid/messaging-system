import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sun.javafx.collections.MappingChange.Map;

public class UsersList {
	private ConcurrentMap<String,String> users=new ConcurrentHashMap<String,String>();
	private ConcurrentMap<String,String> status=new ConcurrentHashMap<String,String>();
	
	public void addStatus(String name){
		status.put(name, " ");
	}
	
	public void changeStatus(String name, String _status){
		status.remove(name);
		status.put(name, _status);
	}
	
	public boolean verify(String s){
		if(users.containsKey(s)==true)
			return true;
		return false;
	}
	
	public void add(String s, String s2){
			users.put(s, s2);
	}
	
	public boolean getPassword(String s, String s2){
		for (Entry<String, String> e : users.entrySet()){
		    if(e.getKey().equals(s)==true){
		    	if(e.getValue().equals(s2)==true)
		    		return true;
		    }
		}
		return false;
	}
	
	public void get(){
		for (Entry<String, String> e : users.entrySet())
		    System.out.println(e.getKey() + " -> " + e.getValue());
	}
	
	public ArrayList<String> getPeople(){
		ArrayList<String> l=new ArrayList<String>();
		for (Entry<String, String> e : status.entrySet())
		    l.add(e.getKey());
		return l;
	}
	
	public ArrayList<String> getStatus(){
		ArrayList<String> l=new ArrayList<String>();
		for (Entry<String, String> e : status.entrySet())
		    l.add(e.getValue());
		return l;
	}
	
}
