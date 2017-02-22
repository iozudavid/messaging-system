import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Repeatedly reads recipient's nickname and text from the user in two
// separate lines, sending them to the server (read by ServerReceiver
// thread).

public class ClientSender extends Thread {

	private PrintStream server;
	protected static boolean log=false;
	protected MessageDigest messageDigest;

	ClientSender(PrintStream server) {
		this.server = server;
		
	}
	
	public static String getMD5(String input) {
        try {
            MessageDigest message = MessageDigest.getInstance("MD5");
            byte[] messageDigest = message.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

	public void run() {
		// So that we can use the method readLine:
		BufferedReader user = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			// Then loop forever sending messages to recipients via the server:

			while (true) {

				String action = user.readLine();
				String s2 = null;
				String s3=null;
				if (action.equals("register") && log==false) {
					try {
						System.out.println("Insert username: ");
						s2 = user.readLine();
						System.out.println("Insert password: ");
						HidePassword hide= new HidePassword();
					    Thread unseen = new Thread(hide);
					    unseen.start();
					    s3=user.readLine();
					    hide.stopHiding();
					    
					    
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					server.println("register");
					server.println(s2);
					server.println(getMD5(s3));
				}

				else if (action.equals("login") && log==false) {
					try {
						System.out.println("Insert username: ");
						s2 = user.readLine();
						System.out.println("Insert password: ");
						HidePassword hide= new HidePassword();
					    Thread unseen = new Thread(hide);
					    unseen.start();
					    s3=user.readLine();
					    hide.stopHiding();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					server.println("login");
					server.println(s2);
					server.println(getMD5(s3));
				}

				else if(action.equals("quit")){
					server.println("quit");
					while (ClientReceiver.quit == false) {

					}
					return;
				}
				
				else if (action.equals("message") && log==true) {
					server.println("message");
					System.out.println("Insert recipient's username: ");
					String recipient = user.readLine();
					System.out.println("Insert text: ");
					String text = user.readLine();
					server.println(recipient);
					server.println(text);
				} 
				
				else if (action.equals("create group") && log==true) {
					
					System.out.println("Insert group's name: ");
					String recipient = user.readLine();
					System.out.println("Insert group's visibility: ");
					String v = user.readLine();
					if(v.equals("public")==true || v.equals("private")==true){
						server.println("create group");
						server.println(recipient);
						server.println(v);
					}
					else{
						System.out.println("Visibility must be public or private. Insert a new command:");
					}
				} 
				
				else if (action.equals("remove group") && log==true) {
					server.println("remove group");
					System.out.println("Insert group's name: ");
					String group = user.readLine();
					server.println(group);
				} 
				
				else if (action.equals("add member") && log==true) {
					server.println("add member");
					System.out.println("Insert group's name: ");
					String recipient = user.readLine();
					System.out.println("Insert member's name: ");
					String member = user.readLine();
					server.println(recipient);
					server.println(member);
				} 
				
				else if (action.equals("remove me") && log==true) {
					server.println("remove me");
					System.out.println("Insert group's name: ");
					String group = user.readLine();
					server.println(group);
				} 
				
				else if (action.equals("remove") && log==true) {
					server.println("remove");
					System.out.println("Insert group's name: ");
					String group = user.readLine();
					System.out.println("Insert person's name: ");
					String person = user.readLine();
					server.println(group);
					server.println(person);
				}
				
				else if(action.equals("request add") && log==true){
					server.println("request add");
					System.out.println("Insert group's name: ");
					String group = user.readLine();
					server.println(group);
				
				}
				
				else if(action.equals("view requests") && log==true){
					server.println("view requests");
					System.out.println("Insert group's name: ");
					String group = user.readLine();
					server.println(group);
				
				}
				
				else if(action.equals("make admin") && log==true){
					server.println("make admin");
					System.out.println("Insert group's name: ");
					String group = user.readLine();
					System.out.println("Insert person's name: ");
					String person = user.readLine();
					server.println(group);
					server.println(person);
				
				}
				
				else if(action.equals("remove admin") && log==true){
					server.println("remove admin");
					System.out.println("Insert group's name: ");
					String group = user.readLine();
					System.out.println("Insert person's name: ");
					String person = user.readLine();
					server.println(group);
					server.println(person);
				
				}
				
				else if(action.equals("accept") && log==true){
					server.println("accept");
					System.out.println("Insert group's name: ");
					String group = user.readLine();
					System.out.println("Insert person's name: ");
					String person = user.readLine();
					server.println(group);
					server.println(person);
				
				}
				
				else if(action.equals("decline") && log==true){
					server.println("decline");
					System.out.println("Insert group's name: ");
					String group = user.readLine();
					System.out.println("Insert person's name: ");
					String person = user.readLine();
					server.println(group);
					server.println(person);
				
				}
				
				else if(action.equals("change name") && log==true){
					server.println("change name");
					System.out.println("Insert old group's name: ");
					String oldName = user.readLine();
					System.out.println("Insert new name: ");
					String newName = user.readLine();
					server.println(oldName);
					server.println(newName);
				
				}
				
				else if(action.equals("set status") && log==true){
					server.println("set status");
					System.out.println("Insert a status: ");
					String status = user.readLine();
					server.println(status);
				
				}
				
				else if(action.equals("view people") && log==true){
					server.println("view people");
				
				}
				
				else if(action.equals("view people in group") && log==true){
					server.println("view people in group");
					System.out.println("Insert group's name:");
					String group=user.readLine();
					server.println(group);
				
				}
				
				else if(action.equals("view groups") && log==true){
					server.println("view groups");
				}
				
				else if (action.equals("logout") == true && log==true) {
					server.println("logout");
				}
				
				else if (action.equals("remove me") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("remove") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("create group") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("add member") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("request add") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("view requests") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("make admin") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("accept") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("decline") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("change name") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("set status") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("view people") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if (action.equals("view people in group") == true && log==false) {
					System.out.println("You must register or log in.");
				}
				
				else if(action.equals("message") && log==false){
					System.out.println("You must register or log in.");
				}
				
				else if(action.equals("logout") && log==false){
					System.out.println("You must register or log in.");
				}
				
				else if(action.equals("register") && log==true){
					System.out.println("You must log out.");
				}
				
				else if(action.equals("login") && log==true){
					System.out.println("You must log out.");
				}
				
				else if(action.equals("view groups") && log==true){
					System.out.println("You must log out.");
				}
				
				
				else
					System.out.println("Choose an action: login, register, message, logout, quit, create group, view groups, "
							+ "view people, view people in group, set status, change name, accept, decline, make admin, view requests, add member, request add"
							+ "remove, remove me");
			}

		} catch (IOException e) {
			Report.errorAndGiveUp("Communication broke in ClientSender" + e.getMessage());
		}
	}
}
