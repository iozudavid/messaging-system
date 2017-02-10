
// Usage:
//        java Server
//
// There is no provision for ending the server gracefully.  It will
// end if (and only if) something exceptional happens.

import java.net.*;
import java.util.ArrayList;

import com.sun.javafx.collections.MappingChange.Map;

import java.io.*;

public class Server {
	
	protected static Object lock=0;
	protected static Object lock2=0;
	public static void main(String[] args) {

		// This table will be shared by the server threads:
		ClientTable clientTable = new ClientTable();
		UsersList userslist = new UsersList();
		ServerSocket serverSocket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedReader in_message = null;
		PrintWriter out_message = null;
		MessagesList messagesList=new MessagesList();
		
		try {
			serverSocket = new ServerSocket(Port.number);
		} catch (IOException e) {
			Report.errorAndGiveUp("Couldn't listen on port " + Port.number);
		}
		try {
			in = new BufferedReader(new FileReader("input.txt"));
			in_message=new BufferedReader(new FileReader("messages.txt"));
			String line;
			while ((line = in.readLine()) != null) {
				String line2;
				line2 = in.readLine();
				userslist.add(line, line2);
			}
			while((line=in_message.readLine())!=null){
				String line2;
				if(messagesList.verify(line)==true){
					line2=in_message.readLine();
					messagesList.getList(line).add(line2);
				}
				else{
					ArrayList<String> list=new ArrayList<String>();
					line2=in_message.readLine();
					list.add(line2);
					messagesList.add(line, list);
				}
				
			}
			in.close();
			in_message.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
		

		try {
			// We loop for ever, as servers usually do.
			while (true) {
				
				Socket socket = serverSocket.accept();
				try {
					out = new PrintWriter(new FileWriter("input.txt", true));
					out_message = new PrintWriter(new FileWriter("messages.txt"));

				} catch (FileNotFoundException e) {
					System.out.println("File not found");
				} catch (IOException e) {
					System.out.println(e.getStackTrace());
				}
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream toClient = new PrintStream(socket.getOutputStream());
				(new ServerReceiver(fromClient, clientTable, userslist, out, toClient, messagesList, out_message)).start();

			}

		} catch (IOException e) {
			// Lazy approach:T
			Report.error("IO error " + e.getMessage());
			// A more sophisticated approach could try to establish a new
			// connection. But this is beyond this simple exercise.
		} 
	}
}
