suprabrowser
============

SupraBrowser is a web browser running on top of a "private social cloud" messaging environment.

It supports multiple communication types such as instant messaging, threaded discussions, email, file sharing, and bookmark management, with a tagging engine that supports search and retrieval across any channel or data type.

All communication happens over a 3DES channel established with a DH key exchange and augmented by a bi-directional SRP authentication:

http://srp.stanford.edu

It supports Xulrunner 1.9.2, for Windows 7 use a 32 bit JVM.

Build instructions:

Enter into the ss.build directory and type:

ant deploy.server -Drev=3000
ant deploy.client -Drev=3000

ant run.server.creator

When the dialog pops up, enter in your first and last name in the "Contact Name" field

If you have a password on mysql (only mysql supported at the moment), enter it in the bottom

That will create the stub database with one user

Then, run:

ant run.client

Followed by

ant run.server

It should fork the client process, but if it doesn't, open up another terminal and run the server and client in separate shells

When the application connects to the server, you can create another user by creating a "contact" in the drop down list in the main "SupraDevelopment" tab.
as long as you give the contact a username and a password, that user will be able to login. then, you should be able to run another "ant run.client" and connect in with the new user.

CTRL-T should open up a new tab. Any time you copy and paste a URL into a sphere's text input window, it will be converted to a bookmark and indexed. To search the index, do:

Command->SupraSearch

