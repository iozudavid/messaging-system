import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AdminsList {
	private ConcurrentMap<String, ArrayList<String>> admins = new ConcurrentHashMap<String, ArrayList<String>>();

	public void changeName(String oldName, String newName){
		ArrayList<String> s=getList(oldName);
		remove(oldName);
		admins.put(newName, s);
	}
	
	public boolean verify(String s) {
		if (admins.containsKey(s) == true)
			return true;
		return false;
	}
	
	public boolean isAdmin(String userName, String groupName){
		ArrayList<String> l=new ArrayList<String>();
		l=getList(groupName);
		for(String s3 : l){
			if(s3.equals(userName)==true)
				return true;
		}
		return false;
	}

	public void add(String groupName, String userName) {
		if(verify(groupName)==true){
			getList(groupName).add(userName);
		}
		else{
			ArrayList<String> l = new ArrayList<String>();
			l.add(userName);
		admins.put(groupName, l);
		}
		
		
	}

	public ArrayList<String> getList(String s) {
		for (Entry<String, ArrayList<String>> e : admins.entrySet()) {
			if (e.getKey().equals(s) == true) {
				return e.getValue();
			}
		}
		return null;
	}

	public void get() {
		for (Entry<String, ArrayList<String>> e : admins.entrySet())
			System.out.println(e.getKey() + " -> " + e.getValue());
	}

	public void writeIn(ArrayList<String> array) {
		for (Entry<String, ArrayList<String>> e : admins.entrySet()) {
			for (String s : e.getValue()) {
				array.add(e.getKey());
				array.add(s);
			}
		}
	}
	
	public void removeAdmin(String userName, String groupName){
		ArrayList<String> list=new ArrayList<String>();
		list=getList(groupName);
		list.remove(userName);
		
	}

	public void remove(String s) {
		admins.remove(s);
	}
}
