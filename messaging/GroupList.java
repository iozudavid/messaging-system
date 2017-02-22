import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GroupList {
	private ConcurrentMap<String, ArrayList<String>> groups = new ConcurrentHashMap<String, ArrayList<String>>();
	private ConcurrentMap<String, ArrayList<String>> requests = new ConcurrentHashMap<String, ArrayList<String>>();
	private ConcurrentMap<String, String> visibility = new ConcurrentHashMap<String, String>();
	
	public void changeName(String oldName, String newName){
		ArrayList<String> s=getUsersList(oldName);
		if(verifyInRequests(oldName)==true){
			ArrayList<String> s2=getRequestsList(oldName);
			requests.put(newName, s2);
		}
		String vis=visibility.get(oldName);
		remove(oldName);
		groups.put(newName, s);
		
		visibility.put(newName, vis);
	}
	
	public void addVisibility(String groupName, String visibility2){
		visibility.put(groupName, visibility2);
	}
	
	public void changeVisibility(String groupName, String visible){
		visibility.remove(groupName);
		visibility.put(groupName, visible);
		
	}
	
	public String getVisibility(String groupName){
		return visibility.get(groupName);
	}

	public boolean verify(String groupName) {
		if (groups.containsKey(groupName) == true)
			return true;
		return false;
	}

	public boolean verifyInRequests(String groupName) {
		if (requests.containsKey(groupName) == true)
			return true;
		return false;
	}

	public void addInUsers(String groupName, String userName) {
		if(verify(groupName)==true){
			getUsersList(groupName).add(userName);
		}
		else{
			ArrayList<String> l = new ArrayList<String>();
			l.add(userName);
			groups.put(groupName, l);
		
		
		
		}
		
	}

	public void addRequest(String groupName, String userName) {
		if (verifyInRequests(groupName) == true) {
			getRequestsList(groupName).add(userName);

		} else {
			ArrayList<String> l = new ArrayList<String>();
			l.add(userName);
			requests.put(groupName, l);
		}
	}

	public boolean isMember(String userName, String group) {
		ArrayList<String> l = new ArrayList<String>();
		l = getUsersList(group);
		for (String s2 : l) {
			if (s2.equals(userName) == true)
				return true;
		}
		return false;
	}

	public boolean isWaiting(String userName, String groupName) {
		if(verifyInRequests(groupName)){
		ArrayList<String> l=new ArrayList<String>();
		l=getRequestsList(groupName);
		for (String s2 : l) {
			if (s2.equals(userName) == true)
				return true;
		}
		}
		return false;
	}

	public ArrayList<String> getUsersList(String s) {
		for (Entry<String, ArrayList<String>> e : groups.entrySet()) {
			if (e.getKey().equals(s) == true) {
				return e.getValue();
			}
		}
		return null;
	}

	public ArrayList<String> getRequestsList(String s) {
		for (Entry<String, ArrayList<String>> e : requests.entrySet()) {
			if (e.getKey().equals(s) == true) {
				return e.getValue();
			}
		}
		return null;
	}

	public void get() {
		for (Entry<String, ArrayList<String>> e : groups.entrySet())
			System.out.println(e.getKey() + " -> " + e.getValue());
		System.out.println("requests");
		for (Entry<String, ArrayList<String>> e : requests.entrySet())
			System.out.println(e.getKey() + " -> " + e.getValue());
		System.out.println("visibility");
		for (Entry<String, String> e : visibility.entrySet())
			System.out.println(e.getKey() + " -> " + e.getValue());
	}
	
	public ArrayList<String> getPublicGroups(String name) {
		ArrayList<String> l=new ArrayList<String>();
		if(groups.size()!=0){
		for (Entry<String, ArrayList<String>> e : groups.entrySet()){
			if(getVisibility(e.getKey()).equals("public") || isMember(name, e.getKey())){
				l.add(e.getKey());
			}
		}
		return l;
		}
		return null;
	}

	public void writeIn(ArrayList<String> array) {
		for (Entry<String, ArrayList<String>> e : groups.entrySet()) {
			for (String s : e.getValue()) {
				array.add(e.getKey());
				array.add(s);
			}

		}
		array.add("requests");
		for (Entry<String, ArrayList<String>> e : requests.entrySet()) {
			for (String s : e.getValue()) {
				array.add(e.getKey());
				array.add(s);
			}

		}
		array.add("visibility");
		for (Entry<String, String> e : visibility.entrySet()) {
				array.add(e.getKey());
				array.add(e.getValue());
		}

	}

	public void removeUser(String userName, String groupName) {
		ArrayList<String> list=new ArrayList<String>();
		list=getUsersList(groupName);
		list.remove(userName);

	}
	
	public void removeRequest(String userName, String groupName) {
		ArrayList<String> list=new ArrayList<String>();
		list=getRequestsList(groupName);
		list.remove(userName);

	}

	public void remove(String s) {
		groups.remove(s);
		requests.remove(s);
		visibility.remove(s);
	}
	
	public ArrayList<String> getMembers(String group){
		for (Entry<String, ArrayList<String>> e : groups.entrySet()){
		    if(e.getKey().equals(group)){
		    	return e.getValue();
		    }
		}
		return null;
	}
	
	
}
