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

https://git.cs.bham.ac.uk/dxi695/SWWAssignment1.git