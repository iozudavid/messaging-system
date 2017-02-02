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
        if(s.equals("From Server: You have been disconnected.")==true){
            System.out.println(s);
            ClientReceiver.quit=true;
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
