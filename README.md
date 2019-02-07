# NewChatServerAndClient
Updated version of my old chat server and client

This project contains both the server and client application built in java. The client and server 
compile into two different jar files. This application is meant to be run in terminal/command line. 

Clients connect to the server application through sockets, each client runs two threads. One thread
to constanly check for messages from the server and the other to gather input from the user and send
it to the server.

The server handles all client connections and distributes messages to all users. The server runs a
seperate thread for each user along with a thread for handling new connections and another for
distributing messages.
