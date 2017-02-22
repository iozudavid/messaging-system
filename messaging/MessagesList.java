import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.sun.javafx.collections.MappingChange.Map;

public class MessagesList {
		private ConcurrentMap<String,ArrayList<String>> messages=new ConcurrentHashMap<String,ArrayList<String>>();
		
		public boolean verify(String s){
			if(messages.containsKey(s)==true)
				return true;
			return false;
		}
		
		public void add(String userName, String message){
			if (verify(userName) == true) {
				getList(userName).add(message);
			} else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(message);
				messages.put(userName, list);
			}
				
		}
		
		public ArrayList<String> getList(String s){
			for (Entry<String, ArrayList<String>> e : messages.entrySet()){
			    if(e.getKey().equals(s)==true){
			    	return e.getValue();
			    }
			}
			return null;
		}
		
		public void get(){
			for (Entry<String, ArrayList<String>> e : messages.entrySet())
			    System.out.println(e.getKey() + " -> " + e.getValue());
		}
		
		public void writeIn(ArrayList<String> array){
			for (Entry<String, ArrayList<String>> e : messages.entrySet()){
			    for(String s : e.getValue()){
			    	array.add(e.getKey());
			    	array.add(s);
			    }
			}
		}
		
		public void remove(String s){
			messages.remove(s);
		}
	

}
