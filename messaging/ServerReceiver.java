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
	protected GroupList groupslist;
	protected AdminsList adminslist;
	private PrintWriter out_groups = null;
	private PrintWriter out_admins = null;

	public ServerReceiver(BufferedReader c, ClientTable t, UsersList ul, PrintWriter pw, PrintStream toClient,
			MessagesList ml, PrintWriter out_message, GroupList groupslist, AdminsList adminslist,
			PrintWriter out_groups, PrintWriter out_admins) {
		myClient = c;
		clientTable = t;
		userslist = ul;
		out = pw;
		this.toClient = toClient;
		this.out_message = out_message;
		this.messagesList = ml;
		this.groupslist = groupslist;
		this.adminslist = adminslist;
		this.out_groups = out_groups;
		this.out_admins = out_admins;

	}

	public void run() {
		try {
			while (true) {
				String action = myClient.readLine();
				if (action.equals("register") == true) {
					String username = myClient.readLine();
					String password = myClient.readLine();
					register(username, password);

				}

				else if (action.equals("login") == true) {
					String username = myClient.readLine();
					String password = myClient.readLine();
					login(username, password);

				}

				else if (action.equals("logout") == true) {
					logout();
				}

				else if (action.equals("quit")) {
					quit();
					return;

				}

				else if (action.equals("create group")) {
					String s = myClient.readLine();
					String s2 = myClient.readLine();
					create_group(s, s2);
				}

				else if (action.equals("remove group")) {
					String group = myClient.readLine();
					remove_group(group);
				}

				else if (action.equals("add member")) {
					String group = myClient.readLine();
					String member = myClient.readLine();
					add_member(group, member);
				}

				else if (action.equals("remove me") == true) {
					String s = myClient.readLine();
					remove_me(s);

				}

				else if (action.equals("view requests") == true) {
					String s = myClient.readLine();
					view_requests(s);

				}

				else if (action.equals("remove") == true) {
					String group = myClient.readLine();
					String member = myClient.readLine();
					remove(group, member);

				}

				else if (action.equals("request add") == true) {
					String group = myClient.readLine();
					request_add(group);
				}

				else if (action.equals("make admin") == true) {
					String group = myClient.readLine();
					String person = myClient.readLine();
					make_admin(group, person);
				}

				else if (action.equals("remove admin") == true) {
					String group = myClient.readLine();
					String person = myClient.readLine();
					remove_admin(group, person);
				}

				else if (action.equals("accept") == true) {
					String group = myClient.readLine();
					String person = myClient.readLine();
					accept(group, person);
				}

				else if (action.equals("decline") == true) {
					String group = myClient.readLine();
					String person = myClient.readLine();
					decline(group, person);
				}

				else if (action.equals("view groups") == true) {
					view_groups();
				}

				else if (action.equals("set status") == true) {
					String _status = myClient.readLine();
					status(_status);
				}

				else if (action.equals("change name") == true) {
					String oldName = myClient.readLine();
					String newName = myClient.readLine();
					change_name(oldName, newName);
				}

				else if (action.equals("view people") == true) {
					view_people();
				}

				else if (action.equals("view people in group") == true) {
					String group = myClient.readLine();
					view_people_in_group(group);
				}

				else if (action.equals("message") == true) {
					String recipient = myClient.readLine();
					String text = myClient.readLine();// Matches DDDDD y //
														// ClientSender.java
					message(recipient, text);
				}
			}

		} catch (

		IOException e) {
			Report.error("Something went wrong with the client " + myClientsName + " " + e.getMessage());
			// No point in trying to close sockets. Just give up.
			// We end this thread (we don't do System.exit(1)).
		}
	}

	private void remove_group(String group) {
		if (groupslist.verify(group) == false)
			toClient.println("This group doesn't exist.");
		else {
			if (adminslist.isAdmin(myClientsName, group) == false) {
				toClient.println("You must be an admin");
			} else {
				messageToGroup(group, myClientsName + "has removed the group " + group);
				adminslist.remove(group);
				groupslist.remove(group);
				toClient.println(group + " succesfully removed");
			}
		}
	}

	private void remove_admin(String group, String person) {
		if (groupslist.verify(group) == false)
			toClient.println("This group doesn't exist.");
		else if (groupslist.isMember(person, group) == false) {
			toClient.println("This person is not a member of this group.");
		} else {
			if (adminslist.isAdmin(myClientsName, group) == false) {
				toClient.println("You must be an admin");
			} else {
				if (adminslist.isAdmin(person, group) == true) {
					if (person.equals(myClientsName) == false) {
						adminslist.removeAdmin(person, group);
						messageToGroup(group, person + "is not an admin of the group " + group + " anymore");
						toClient.println(person + " was sucesfully removed from admins");
					} else {
						if (adminslist.getList(group).size() == 1) {
							toClient.println(
									"You are the last admin. You need to make another person admin and then you will be able to be a simple user.");
						} else {
							adminslist.removeAdmin(person, group);
							messageToGroup(group, "is not an admin of the group " + group + " anymore");
							toClient.println("You was sucesfully removed from admins");
						}
					}

				} else {
					toClient.println("This person is not an admin");
				}
			}
		}
	}

	private void view_people_in_group(String group) {
		if (groupslist.verify(group) == false) {
			toClient.println("This is not a group");
		} else {
			if (groupslist.getVisibility(group).equals("private") == true
					&& groupslist.isMember(myClientsName, group) == false) {
				toClient.println(
						"You are not allow to see the members as this group is private and you are not a member");
			} else {

				for (int i = 0; i < groupslist.getMembers(group).size(); i++) {
					toClient.print(groupslist.getMembers(group).get(i) + " (");
					if (clientTable.verify(groupslist.getMembers(group).get(i)) == true) {
						toClient.print("online)");
					} else {
						toClient.print("offline)");
					}
					if (adminslist.isAdmin(groupslist.getMembers(group).get(i), group) == true) {
						toClient.println(" admin");
					} else {
						toClient.println(" user");
					}

				}

			}
		}

	}

	private void view_people() {
		for (int i = 0; i < userslist.getPeople().size(); i++) {
			toClient.print(userslist.getPeople().get(i) + " (");
			if (clientTable.verify(userslist.getPeople().get(i)) == true) {
				toClient.print("online)");
			} else {
				toClient.print("offline)");
			}
			toClient.println(" '" + userslist.getStatus().get(i) + " '");

		}
	}

	private void status(String _status) {
		userslist.changeStatus(myClientsName, _status);
		toClient.println("Status succesfully changed in" + " '" + _status + "'");

	}

	private void change_name(String oldName, String newName) {
		if (groupslist.verify(oldName) == false) {
			toClient.println("This group doesn't exist.");
		} else if (groupslist.isMember(myClientsName, oldName) == false) {
			toClient.println("You are not a member of this group.");

		} else {
			if (adminslist.isAdmin(myClientsName, oldName) == true) {
				if (groupslist.verify(newName) == true) {
					toClient.println(newName + " already exist");
				} else {
					groupslist.changeName(oldName, newName);
					adminslist.changeName(oldName, newName);
					messageToGroup(newName, myClientsName + " has changed the name from " + oldName + " to " + newName);
					toClient.println("You have changed the name to " + newName);
				}
			} else {
				toClient.println("You must be an admin.");
			}
		}
	}

	private void view_groups() {
		if (groupslist.getPublicGroups(myClientsName) != null) {
			for (String s : groupslist.getPublicGroups(myClientsName))
				toClient.println(s);
		} else
			toClient.println("No groups");
	}

	private void decline(String group, String person) {
		if (groupslist.verify(group) == false) {
			toClient.println("This group doesn't exist.");
		} else if (userslist.verify(person) == false) {
			toClient.println("This member doesn't exist.");
		} else {
			if (adminslist.isAdmin(myClientsName, group) == true) {
				if (groupslist.verifyInRequests(group) == true) {
					if (groupslist.isWaiting(person, group) == false) {
						toClient.println("This person didn't require an add");
					} else {
						groupslist.removeRequest(person, group);
						message(person, "Your request to be added to group " + group + " was declined");
						toClient.println(person + " was sucesfully removed from requests");
					}
				} else {
					toClient.println("There are no requests.");
				}
			} else {
				toClient.println("You must be an admin.");
			}
		}

	}

	private void accept(String group, String person) {
		if (groupslist.verify(group) == false) {
			toClient.println("This group doesn't exist.");
		} else if (userslist.verify(person) == false) {
			toClient.println("This member doesn't exist.");
		} else {
			if (adminslist.isAdmin(myClientsName, group) == true) {
				if (groupslist.verifyInRequests(group) == true) {
					if (groupslist.isWaiting(person, group) == false) {
						toClient.println("This person didn't require an add");
					} else {
						messageToGroup(group, person + " have been added to group " + group);
						groupslist.addInUsers(group, person);
						groupslist.removeRequest(person, group);
						message(person, "Your request to be added to group " + group + " was accepted");
						toClient.println(person + " was sucesfully added to this group");
					}
				} else {
					toClient.println("There are no requests.");
				}
			} else {
				toClient.println("You must be an admin.");
			}
		}

	}

	private void make_admin(String group, String member) {

		if (groupslist.verify(group) == false)
			toClient.println("This group doesn't exist.");
		else if (groupslist.isMember(member, group) == false) {
			toClient.println("This person is not a member of this group.");
		} else {
			if (adminslist.isAdmin(myClientsName, group) == false) {
				toClient.println("You must be an admin");
			} else {
				if(adminslist.isAdmin(member, group)==false){
				adminslist.add(group, member);
				messageToGroup(group, member + "is now an admin of the group " + group);
				toClient.println("Admin succesfully added");
				}
				else{
					toClient.println("This person is already an admin.");
				}
			}
		}

	}

	private void view_requests(String s) {
		if (groupslist.verify(s) == false) {
			toClient.println("This group doesn't exist");
		} else {
			if (adminslist.isAdmin(myClientsName, s) == true) {
				toClient.println(groupslist.getRequestsList(s));
			} else {
				toClient.println("You must be an admin");
			}
		}
	}

	private void message(String recipient, String text) {
		if (recipient != null && text != null) {
			Message msg = new Message(myClientsName, text);
			MessageQueue recipientsQueue = clientTable.getQueue(recipient);
			if (recipientsQueue != null) {
				recipientsQueue.offer(msg);
			} else {
				if (userslist.verify(recipient) == true) {
					messagesList.add(recipient, msg.toString());

				} else if (groupslist.verify(recipient) == true) {
					if (groupslist.isMember(myClientsName, recipient) == false) {
						toClient.println("You are not a member of this group");
					} else {
						ArrayList<String> members = groupslist.getUsersList(recipient);
						Message msg2 = new Message(myClientsName + " @" + recipient, text);
						for (String s : members) {
							if (s.equals(myClientsName) != true) {
								MessageQueue queuemember = clientTable.getQueue(s);
								if (queuemember != null) {
									synchronized (queuemember) {
										queuemember.offer(msg2);

									}

								} else {
									if (userslist.verify(s) == true) {
										messagesList.add(recipient, msg.toString());

									}
								}
							}
						}
					}
				} else {
					Report.error("Message for unexistent client " + recipient + ": " + text);
				}
			}
		} else
			// No point in closing socket. Just give up.
			return;
	}

	public void messageToGroup(String recipient, String text) {
		Message msg = new Message(myClientsName, text);
		ArrayList<String> members = groupslist.getUsersList(recipient);
		Message msg2 = new Message("@" + recipient, text);
		for (String s : members) {
			if (s.equals(myClientsName) != true) {
				MessageQueue queuemember = clientTable.getQueue(s);
				if (queuemember != null) {
					synchronized (queuemember) {
						queuemember.offer(msg2);

					}

				} else {
					if (userslist.verify(s) == true) {
						messagesList.add(s, msg.toString());

					}
				}
			}
		}
	}

	private void request_add(String group) {
		if (groupslist.verify(group) == false) {
			toClient.println("This group doesn't exist.");
		} else if (groupslist.isMember(myClientsName, group) == true)
			toClient.println("You are already a member");
		else if (groupslist.isWaiting(myClientsName, group))
			toClient.println("You have already requested");
		else {
			if (groupslist.getVisibility(group).equals("private")) {
				toClient.println("This group is private so you can't request add");
			} else {
				groupslist.addRequest(group, myClientsName);
				toClient.println("Request succesfully");
			}
			
		}
	}

	private void remove(String group, String member) {

		if (groupslist.verify(group) == false)
			toClient.println("This group doesn't exist.");
		else if (groupslist.isMember(member, group) == false) {
			toClient.println("This person is not a member of this group.");

		} else {
			if (adminslist.isAdmin(myClientsName, group) == false) {
				toClient.println("You must be an admin");
			} else {
				groupslist.removeUser(member, group);
				if (adminslist.isAdmin(member, group) == true) {
					adminslist.removeAdmin(member, group);
				}
				message(member, "You have been removed from group " + group);
				messageToGroup(group, member + " has been removed from group " + group);
				toClient.println(member + " succesfully removed");

			}
		}
	}

	private void remove_me(String s) {
		ArrayList<String> list = new ArrayList<String>();
		if (groupslist.verify(s) == false) {
			toClient.println("This group doesn't exist.");
		} else if (groupslist.isMember(myClientsName, s) == false) {
			toClient.println("You are not a member of this group.");
		} else {
			list = groupslist.getUsersList(s);
			groupslist.removeUser(myClientsName, s);
			if (adminslist.isAdmin(myClientsName, s) == true) {
				adminslist.removeAdmin(myClientsName, s);
				if (adminslist.getList(s).size() == 0) {
					if (list.size() > 0) {
						String member = list.get(0);
						adminslist.getList(s).add(member);
					} else {
						groupslist.remove(s);
						adminslist.remove(s);
					}
				}
			}
			toClient.println("You have been removed");
			if (groupslist.verify(s) == true)
				messageToGroup(s, myClientsName + " has left the group " + s);
		}
	}

	private void add_member(String group, String member) {
		if (userslist.verify(member) == false) {
			toClient.println("This member doesn't exist.");
		} else {
			if (groupslist.verify(group) == false) {
				toClient.println("This group doesn't exist.");
			} else {
				if (adminslist.isAdmin(myClientsName, group) == true) {
					if (groupslist.isMember(member, group) == true) {
						toClient.println("Already a member");
					} else {
						messageToGroup(group, member + " has been added to group " + group);
						groupslist.addInUsers(group, member);
						message(member, "You have been added to group " + group);
						toClient.println(member + " was added succesfully");
						if (groupslist.verifyInRequests(group) == true) {
							if (groupslist.isWaiting(member, group))
								groupslist.getRequestsList(group).remove(member);
						}
					}
				} else {
					toClient.println("You must be an admin.");
				}
			}
		}
	}

	private void create_group(String s, String s2) {
		if (groupslist.verify(s) == true) {
			toClient.println("Group already exist");
		} else {
			groupslist.addInUsers(s, myClientsName);
			adminslist.add(s, myClientsName);
			groupslist.addVisibility(s, s2);
			toClient.println("Group succesfully created.");

		}
	}

	private void quit() {
		synchronized (Server.lock2) {
			array = new ArrayList<String>();
			messagesList.writeIn(array);
			for (String s : array) {
				out_message.println(s);
			}
			array = new ArrayList<String>();
			groupslist.writeIn(array);
			for (String s : array) {
				out_groups.println(s);
			}
			array = new ArrayList<String>();
			adminslist.writeIn(array);
			for (String s : array) {
				out_admins.println(s);
			}

		}
		out.close();
		out_message.close();
		out_groups.close();
		out_admins.close();
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

	private void logout() {
		Report.behaviour(myClientsName + " disconnected");
		Message msg = new Message("List", "Succesfully loggedout");
		MessageQueue recipientsQueue = clientTable.getQueue(myClientsName);
		clientTable.remove(myClientsName);
		recipientsQueue.offer(msg);
	}

	private void login(String username, String password) {
		if (userslist.verify(username) == true) {
			if (clientTable.verify(username) == true) {
				toClient.println("Already login");
			} else if (userslist.getPassword(username, password) == true) {
				Report.behaviour(username + " connected");
				myClientsName = username;
				clientTable.add(myClientsName);
				toClient.println("Succesfully loggedin");
				(new ServerSender(myClientsName, clientTable.getQueue(myClientsName), toClient, messagesList)).start();

			} else {
				toClient.println("Invalid password");
			}
		} else {
			toClient.println("User doesn't exist");
		}

	}

	private void register(String username, String password) {
		if (userslist.verify(username) == true) {
			toClient.println("Already exist");
		} else {
			Report.behaviour(username + " connected");
			userslist.add(username, password);
			userslist.addStatus(username);
			clientTable.add(username);
			myClientsName = username;
			synchronized (Server.lock) {
				out.println(username);
				out.println(password);
			}
			toClient.println("Succesfully registered");
			(new ServerSender(myClientsName, clientTable.getQueue(myClientsName), toClient, messagesList)).start();

		}
	}
}
