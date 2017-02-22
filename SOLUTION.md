Firstly, I check in ClientSender if the recipient is quit. 
If it is, I print to the server "quit" and then I wait for ClientReceiver to end the Thread. After that Thread is ended, I end the ClientSender too.
In the ServerReceiver, if the ClientSender sends the message "quit", I make a new message for the client who wants to quit.
The message will be: "From Server: You have been disconnected."
I get the message queue for the client who wants to quit, I offer the message i just created and then, i call the remove function which I created in ClientTable.
This function simply removes from the table the nickname.
Also, i print a message on the server that says the user has disconnected.
After that, I interrupt the ServerReceiver Thread.
The message will arrive now in ServerSender, where if I receive it I close the curent thread.
Lastly, in the ClientReceiver, when i receive the message from the server, I print it, change the static value of quit to true so in this way the ClientSender will know
that the ClientReceiver thread ended and then just end the ClientReceiver.










PART2


-----------------------------------------------------------------Client Side--------------------------------------------------------------------------------------------

---Client---
Firstly, I changed the restriction of args.length to 1 and the usage information to java Client server-hostname, in order to let the client to log in or register 
firstly and after that I will store the Client's name.
Now it's just one argument, so I store in a variable just the server-hostname. I also changed tho constructor of ClientSender, 
so now this class has just one parameter which is the PrintStream.

---ClientSender---
In ClientSender I have a static boolean variable called log which tells if the Client is connected or not based on what messages
it receives in ClientReceiver, so in this way, for example, I know if the client is able to send messages or he must login or register first.
I have also a protected MessageDigest variable to encrypt the passwords. In this way, the external file which holds the users list will display a password encrypted,
so no one will have acces to the account except of the client. 
I have chosen the MD5 functionality of a message digest algorithm, so it passes into a string and returns the MD5 encryption value.
Method getBytes() converts a text to byte array, the digest is then updated from from the byte array and a hash computation is conducted upon them, using MessageDigest.
https://docs.oracle.com/javase/7/docs/api/java/security/MessageDigest.html
There are a lot of changes in run method. Firstly the program waits for an action in order to perform it. If the action is not login register logout message or quit
it will print an information to the user about how to use it. The program checks the log boolean at every action required. So, for example if a user is logged in
and he typed login it will see the message:"You must logout." and so on. For register the program waits for a username and a password. When the user will insert the password,
a new thread will start which will hide the password by putting '*' instead of the actual input. I implemented this, as well as encrypted passwords, because of security
reasons which I consider very important for a messaging application. After the password is inserted, the thread which hide the input is stopped. After that, the action,
username and password are sent to ServerReceiver because the Server has all the informations about users, passwords etc.
For login command is basically the same procedure.
For message command, the program waits for recipient's name and text and send them as well to the ServerReceiver.
The rest of if clauses are for situations when the command doesn't make sense as I mentiond before, using the boolean log.

---ClientReceiver---
The ClientReceiver is not so much changed. Firstly, it checked for messages from the server in order to change the value of log and simply
print for the user every message.



------------------------------------------------------------------New classes----------------------------------------------------------------------------------------

---MessagesList---
This class contains a map which has keys usernames and values ArrayLists of strings in which are storred messages.
This class stores all the messages which can't be received by users because they are disconnected. It contains a few methods: 
verify - returns true if s is in the list, otherwise returns false
add - simply add to the map a new user with a list of strings
getList - it has an iterator which stops and return the list when a key is equal with s
writeIn - write in a new list every usernames and messages that will be use to write in the external file
remove - simply removes a key

---HidePassword---
A class which implements Runnable. When it is started, the finish boolean is set to true and while it is not interrupted it prints a '*' instead of the actual input.
I have used "\b" for this thing. The stopHiding method set the boolean to false so it stops the thread.

---UsersList---
This class contains a map which has usernames as keys and passwords as values.
This class stores all the users and passwords which were registered. It contains a few methods: 
verify - returns true if s is in the list, otherwise returns false
add - simply add to the map a new user with a password
getPassword - which returns true if the user and the password are in the map, false otherwise
get - get the map




-------------------------------------------------------------------External file-------------------------------------------------------------------------------------

---input.txt---
This file will store all the usernames and encrypted passwords.
An example of how it works is:
username1
password1
username2
password2    etc.


---messages.txt---
This file will store all the messages, which were received by users when they were loggedout.
An example of how it works:
username1
message1
username2
message2
username1
message3    etc

Note that if a user has more than one message, his name will appear for many times.
e.g a user receives 3 messages so his name in a file will appear 3 times.



-------------------------------------------------------------------Server Side----------------------------------------------------------------------------------------

---Server---
In server I create a userslist where are stored all users registered, so I can verify for each attempt to register or login if there is already a user with that name
or not. I also have two readers and two writers. One reader and one writer are for the external file which holds all the usernames and passwords and the other ones
are for the external file which holds the recipient's name with messages which were sent to him while he was disconnected. So, if the server is closed and then started again,
I have stored all usernames and messages which were not received and in this way users can receive messages even if the server is closed between sessions.
When the server starts, I read from both files and add to the two maps I created, which are in UsersList and MessagesList. In this way, the program can always check
if there is a valid login or registration request, or when a user login if he was receiving messages while he was loggedout. For MessagesList, the program checks
if the user already has a list. If he has one, it will add to that a new message, otherwise it will add a key with the username and a list which caontains only one message
as a value. After that, I close the two readers.
The server is running forever as always and waits for a request. When the request is accepted, the program creates two writers for each client. So, in some situations,
it will write in the files.
I chose to only start the ServerReceiver thread from the Server because the ServerSender thread only works when a user has a queue and when is loggedin. So, for efficiency,
the ServerSender thread will be started in ServerReceiver when a user is registered or loggedin and stopped as well in ServerReceiver when the user quit or logout.

---ServerReceiver---
It stores all the paramteres which comes from the Server. It runs until the quit action in called. Firstly, it takes the action from the client.
If the action is register, then it takes a username and a password. Then the program will verify if it is a user in usersList with this name.
If it is it will print a message to the client to inform him this thing, otherwise it will print to the server that the client is connected.
It adds to the map usersList, clientTable and also write in the external file to have all the registered user saved after the server is closed.
I chose to synchronize this action with a static boolean lock which is in the server because of the next reason. If there are two threads which wants to write the username
and the password, the action is probably to happen in this way for example:
username1
username2
password1
password2
So, the file will have wrong dates and the whole program will be bugged. Also, the program prints a succesfull message to the client and starts the ServerSender thread
with client's dates. It also set the boolean stopThread to false, so the ServerSender should run.
If the action is login, the program will wait again for an username and a password. It checks if the user extists. If it not extists the program will print a message
to the client. Otherwise, it checks if it exists in clientTable. If it is, it prints message to the Client to inform him that someone is already loggedin. Else,
it checks if the password is ok. If it is, it adds to cilentTable and starts the ServerSender thread. It doesn't add in the usersList and doesn't write in the file
because the client was added when he registered. Boolean stopThread is set to false.
If the action is logout, the program will remove the client from the clientTable and offer a message to the Client. It changes the stopThread to true so we don't need
the ServerSender for the logout Client anymore. After this logout action, the Client will still run and it will be able to register or login.
If the action is quit, it will call the writeIn method from messagesList and write in an empty list. Then, the program writes in the external file for messages
all the messages that exist in messagesList. It closes the writers. If it was a client connected who typed quit, then the program will delete it from the clientTable.
If the action is message then the program waits for a name and a text. If the user is loggedin, it simply send the message. Else, it checks if it is registered.
If it is, it will add the message to its arraylist in messagesList, else it sends the error that the user doesn't exist.

---ServerSender---
When the ServerSender is started, it checks if there are any messages for the user in messagesList and send them if there are.
Then it will check the queue and sends to the client when a message comes. It will stop when quit or logout is typed














PART3


-----------------------------------------------------------------Client side------------------------------------------------------------------------------------------------

---ClientSender---
In ClientSender I added more commands for groups. If a user types "create group", he will need to insert the name and the visibility mode for creting the group.
If the visibility is not public or private, there is no information sent to the server. "remove group" is for removing th entire group, it asks for the group's name and
send it to the server. The "add member" is to add a member to a group. It asks for group's name and person's name. "remove me" is called to left a group and it also asks
for the group's name. The "remove" command is for removing another person in a group and you will need to insert group's name and person's name. In order to request to be added
to a group you will need to type "request add" and the group's name which you want to join. To view requests, you need to type "view requests" and then, the program will aks
for the group's name. The command "make admin" is called if you want to make a person of a group an admin of that group. You will also need to type group's name and person's name.
On the other part, "remove admin" is when you want to remove a person from admins, but the person will be still a member of that group, but not an admin. It will ask for the group's name
and person's name. "accept" and "decline" commands are for requests. So, if you want to accept a request, you will type "accept", otherwise you will type "decline". Both will ask
for group's name and person's name. If you want to change group's name, you need to type "change name". You will also type the actual name and the new name you want to change in.
The "set status" command is to change your status. All people's status is visible when you type view people. The default status is an empty string. If you want to see
the people which are registered, you need to type "view people". This will also show if they are online or offline. The "view people in group" command is to see the members of a group
and if they are online or not. The "view groups" command is to show you all the groups. All these commands will work if you are loggedin. Otherwise, you will receive a message to
login or register. If you type a command which is not in the list, you will receive a message with all the commands you can use.

---Client and ClientReceiver--- are unchanged.



----------------------------------------------------------------------------New classes--------------------------------------------------------------------------------
---GroupList---
This class contains three maps. Groups map has keys group names and values ArrayLists of strings in which are storred the members.
Requests map has keys group names and values a list of strings in which are storred the add requests.
Visibility map has keys group names and values a string for each group which is the type of visibility.
This class contains informations about members, requests and visibility mode of all groups. It contains a few methods:
changeName- takes as parameters the actual name and the new name and remove the group with the old name and create a new one with the new name, but the members, visibility and requests
remains the same as there were for the old group
addVisibility- adds the visibility mode for each group
changeVisibility- changes the visibility of a group
getVisibility- returns the visibility of the group
verify- verifies if a group exists or not, checking in groups map
verifyInRequests- check if there exists a map for a group in requests map
addInUsers- checks if the group exists with verify method. If it exists, it adds the user to the list of users in groups map, else it creates a new list with the user and put it in groups map
addRequest- it verifies if there is a map for the group. If it exists it adds the user to the list of requests in requests map, else it creates a new list with the user and put it in groups map
isMember- it takes as parameters a username and a group and it checks if the username is in the list of members for that group
isWaiting- it takes a userName and a groupName and checks if the username is in the list of requests for that group
getUsersList- takes the group name as parameter and return the arraylist of members for that group
getRequestsList- takes the group name as parameter and return the list of requests for that group
get- prints informations about the group with members, requests and visibility
getPublicGroups- takes the userName as parameter and returns a list with all the public groups and the private groups where the user is a member. The other private groups where the user
is not a member can't be displayed as they are secret.
writeIn- it writes in an arraylist members, requests and visibility as they can be written in an external file.
removeUser- it removes a user from the members list of a group
removeRequest- it removes a user from the requests list of a group
remove- remove the group entirely, from the groups map, requests map and visibility map
getMembers- returns a list with all the members of a group

---AdminsList---
This class contains a map which has keys group names and values ArrayLists of strings in which are storred the admins of the group.
This class stores all the admins of each group. It contains a few methods: 
changeName- takes as parameters the actual name and the new name and remove the group with the old name and create a new one with the new name, but the admins remains the same as there were for the 
old group
isAdmin- takes a username and a group name as parameters and return true if the user is an admin of that group, false otherwise
add- it adds a username to the list of admins of a group
getList- returns a list of the admins of the group
get- prints all the adimns for wach group
writeIn- return an arraylist of all admins of each group which will be used to write in an extrnal file
removeAdmin- removes an admin of a group
remove- remove a group and its list from the map


---------------------------------------------------------------------------External files------------------------------------------------------------------------------------
---groups.txt---
This file will store all groups with all the members, requests and type of visibility.
An example of how it works is:
group1
username1
group1
username2
group2
username1
requests
group1
username3
visibility
group1
public
group2
private

---admins.txt---
This file will store all the groups with all the admins.
An example of how it works is:
group1
username1
group1
username2
group2
username2


---------------------------------------------------------------------------Server side---------------------------------------------------------------------------------------
---Server---
I created two new BufferedReader and two new PrintWriter variables. Also, I created an object of type GroupList and another one of type AdminsList.
The program reads from files each line and adds to each map a key and a value. Then, all the reader are closed. It generates for each client four PrintWriters, the new PrintWriters
are out_groups and out_admins which will be used to write in the grops.txt file and admins.txt file. Also,  the ServerReceiver takes as parameters the new variables I mentioned before
and the Server starts the ServerReceiver thread.

---ServerReceiver---
I changed the structure of this class. Instead of writing in if clauses, I made methods for each of commands came from the client. In this way, I can re-used the message method
for each other method.
The new methods:
remove_group(group)- firstly checks if the group exists, then if the user is an admin. If they are true, it sends messages to the ehole group and remove it definetely.
remove_admin(group, person)- also checks if the group exists and if the person is a member of the group and if the client is an admin. If the person is an admin and is the only one
the operation can't be processed as the group will have no admins. Else, the person is removed from admins list.
view_people_in_group(group)- it checks if the group is valid and if the client has the permission to view informations about the group (the client has the permision if the group is public
or if the group is private but he is already a member of this group). Then it will prints to the user all the members of the group, if they are online or offline and if they are admins or
simply users.
e.g. ABCDE (online) admin
view_people- prints all the users registered, prints whether they are online or offline and their status
status(_status)- change the client's status in _status
change_name(oldName, newName)- verifies if the group exists, if the client is a member and an admin.If the name already exist, the name won't change, else the name will change
and all the members will receive a message with this information
view_groups- prints "No groups" if there aren't any groups or prints all the public groups and the private ones where the client is a member. All the public groups will be printed
whether the user is a member or not. This moethod is to print all the groups available to join.
decline(group, person)- checks if the group exists, if the client is a member and if the client is an admin too. It decline a request if the request exists, otherwise will print that
the person didn't require add.
accept(group, person)- like decline, but here the person will be added to the group
make_admin(group, member)- it verifies if the group exists, if the person is a member and if the client is an admin. If all are true, it checks if the person is already an admin.
If it isn't the person will become an admin, else it will prints to the client that the person is already an admin.
view_requests(group)- checks if the group exists and if the client is an admin. If they are true, it will prints the list of requests.
messageToGroup(recipient, text)- it will print a message to all the members of a group. It is used in a lot of methods to inform the members about the changes of the group.
request_add(group)- it verifies if the group exists, if the client is already a member, if the person already made a request and if the group is public. If all conditions are right,
the request will be made.
remove(group, member)- it checks if the group exists, if the person is a member of the group, if the client is an admin. If conditions are good, it will remove the user from group's users
and if it is an admin from admins list of the group.
remove_me(group)- it checks if the group exists, if the client is a member. If conditions are good, he will be removed from group's users and if he is an admin, if he is the only admin,
another person will be made admin automatically. If the client is the only one in the group, the group will be totally removed.
add_member(group, member)- it checks if the group exists, if the member exists and if the client is an admin. If these conditions are good, it checks if the person is already a member.
If it isn't, the person will be added to the group and removed from requests list if he made a request.
create_group(group, visibility)- it checks if the group exists, and if it isn't, the group will be created and the client will be added in users list and admins list.











COMPILATION
Create a classes folder near the messaging one and compile from  SWWAssignment1 with the command javac -d classes -cp classes messaging/*.java.
RUNNING
java -cp classes Server
java -cp classes Client localhost


https://git.cs.bham.ac.uk/dxi695/SWWAssignment1.git