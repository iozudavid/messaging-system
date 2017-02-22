import java.net.*;
import java.io.*;

// Continuously reads from message queue for a particular client,
// forwarding to the client.

public class ServerSender extends Thread {
  protected MessageQueue clientQueue;
  private PrintStream client;
  private MessagesList messagesList;
  private String name;

  public ServerSender(String n,MessageQueue m, PrintStream c, MessagesList ml) {
	clientQueue=m;
    client = c;
    messagesList=ml;
    name=n;
  }

  public void run() {
	  if(messagesList.verify(name)==true){
		  for(String s : messagesList.getList(name)){
			  client.println(s);
		  }
		  messagesList.remove(name);
	  }
    while (true) {
      Message msg = clientQueue.take(); // Matches EEEEE in ServerReceiver
      client.println(msg); // Matches FFFFF in ClientReceiver
      if(msg.getSender().equals("Server")){
    	  return;
      }
      
    }
  }
}
