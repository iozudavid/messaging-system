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



https://git.cs.bham.ac.uk/dxi695/SWWAssignment1.git