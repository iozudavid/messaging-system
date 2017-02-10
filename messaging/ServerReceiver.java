import java.net.*;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.io.*;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client.

public class ServerReceiver extends Thread {
	private String myClientsName;
	private BufferedReader myClient;
	private ClientTable clientTable;
	private UsersList userslist;
	private PrintWriter out;
	private PrintStream toClient;
	private PrintWriter out_message = null;
	private MessagesList messagesList = null;
	private ArrayList<String> array = new ArrayList<String>();
	protected static boolean stopThread=true;

	public ServerReceiver(BufferedReader c, ClientTable t, UsersList ul, PrintWriter pw, PrintStream toClient,
			MessagesList ml, PrintWriter out_message) {
		myClient = c;
		clientTable = t;
		userslist = ul;
		out = pw;
		this.toClient = toClient;
		this.out_message = out_message;
		this.messagesList = ml;

	}

	public void run() {
		try {
			while (true) {
				String action = myClient.readLine();
				if (action.equals("register") == true) {
					String username = myClient.readLine();
					String password = myClient.readLine();
					if (userslist.verify(username) == true) {
						toClient.println("Already exist");
					} else {
						Report.behaviour(username + " connected");
						userslist.add(username, password);
						clientTable.add(username);
						myClientsName = username;
						synchronized (Server.lock) {
							out.println(username);
							out.println(password);
						}
						toClient.println("Succesfully registered");
						stopThread=false;
						(new ServerSender(myClientsName, clientTable.getQueue(myClientsName), toClient, messagesList))
								.start();
						
					}
				}

				else if (action.equals("login") == true) {
					String username = myClient.readLine();
					String password = myClient.readLine();
					if (userslist.verify(username) == true) {
						if (clientTable.verify(username) == true) {
							toClient.println("Already login");
						} else if (userslist.getPassword(username, password) == true) {
							Report.behaviour(username + " connected");
							myClientsName = username;
							clientTable.add(myClientsName);
							toClient.println("Succesfully loggedin");
							stopThread=false;
							(new ServerSender(myClientsName, clientTable.getQueue(myClientsName), toClient,
									messagesList)).start();
							
						} else {
							toClient.println("Invalid password");
						}
					} else {
						toClient.println("User doesn't exist");
					}
				}

				else if (action.equals("logout") == true) {
					Report.behaviour(myClientsName + " disconnected");
					Message msg = new Message("List", "Succesfully loggedout");
					MessageQueue recipientsQueue = clientTable.getQueue(myClientsName);
					clientTable.remove(myClientsName);
					recipientsQueue.offer(msg);
					stopThread=true;
				}

				else if (action.equals("quit")) {
					synchronized (Server.lock2) {
						array = new ArrayList<String>();
						messagesList.writeIn(array);
						for (String s : array) {
							out_message.println(s);
						}
						
					}
					out.close();
					out_message.close();
					stopThread=true;
					if (myClientsName != null) {
						Report.behaviour(myClientsName + " disconnected");
						toClient.println("You have been disconnected.");
						clientTable.remove(myClientsName);
						return;
					} else {
						toClient.println("You have been disconnected.");
						return;
					}
				
				}

				else if (action.equals("message") == true) {
					String recipient = myClient.readLine();
					String text = myClient.readLine();// Matches DDDDD in
														// ClientSender.java
					if (recipient != null && text != null) {
						Message msg = new Message(myClientsName, text);
						MessageQueue recipientsQueue = clientTable.getQueue(recipient); // Matches
																						// EEEER
																						// in
																						// ServerSenser.java
						if (recipientsQueue != null) {
							recipientsQueue.offer(msg);
						} else {
							if (userslist.verify(recipient) == true) {
								if (messagesList.verify(recipient) == true) {
									messagesList.getList(recipient).add(msg.toString());
								} else {
									ArrayList<String> list = new ArrayList<String>();
									list.add(msg.toString());
									messagesList.add(recipient, list);
								}

							} else {
								Report.error("Message for unexistent client " + recipient + ": " + text);
							}
						}
					} else
						// No point in closing socket. Just give up.
						return;
				}
			}

		} catch (

		IOException e) {
			Report.error("Something went wrong with the client " + myClientsName + " " + e.getMessage());
			// No point in trying to close sockets. Just give up.
			// We end this thread (we don't do System.exit(1)).
		}
	}
}
