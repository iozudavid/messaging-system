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
				
				else if (action.equals("logout") == true && log==true) {
					server.println("logout");
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
				
				
				
				else
					System.out.println("Choose an action: login, register, message, logout, quit.");
			}

		} catch (IOException e) {
			Report.errorAndGiveUp("Communication broke in ClientSender" + e.getMessage());
		}
	}
}
