import java.io.*;
import java.net.*;

// Gets messages from other clients via the server (by the
// ServerSender thread).

public class ClientReceiver extends Thread {

  private BufferedReader server;
  protected static boolean quit=false;

  ClientReceiver(BufferedReader server) {
    this.server = server;
  }

public void run() {
    // Print to the user whatever we get from the server:
	
    try {
      while (ServerSender.currentThread().isInterrupted()==false) {
        String s = server.readLine(); // Matches FFFFF in ServerSender.java
       // ClientSender.nickname="";
        if(s.equals("From List: Already exist") || s.equals("Already exist")){
        	ClientSender.log=false;
        	System.out.println(s);
        }
        else if(s.equals("From List: Succesfully loggedout") || s.equals("Succesfully loggedout")){
        	ClientSender.log=false;
        	System.out.println(s);
        }
        else if(s.equals("From List: User doesn't exist") || s.equals("User doesn't exist")){
        	ClientSender.log=false;
        	System.out.println(s);
        }
        else if(s.equals("From List: Succesfully registered") || s.equals("Succesfully registered")){
        	ClientSender.log=true;
        	System.out.println(s);
        }
        else if(s.equals("From List: Succesfully loggedin") || s.equals("Succesfully loggedin")){
        	ClientSender.log=true;
        	System.out.println(s);
        }
        else if(s.equals("From List: Already login") || s.equals("Already login")){
        	ClientSender.log=false;
        	System.out.println(s);
        }
        else if(s.equals("From Server: You have been disconnected.") || s.equals("You have been disconnected.")){
            System.out.println(s);
            ClientReceiver.quit=true;
            ClientSender.log=false;
            return;
        }
        
      
        else if (s != null){
          System.out.println(s);
        }
        else
          Report.errorAndGiveUp("Server seems to have died"); 
      }
    }
    catch (IOException e) {      
    	Report.errorAndGiveUp("Server seems to have died " + e.getMessage());
    }
  }
}
