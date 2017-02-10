import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sun.javafx.collections.MappingChange.Map;

public class UsersList {
	private ConcurrentMap<String,String> users=new ConcurrentHashMap<String,String>();
	
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
	
}
