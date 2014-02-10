-- Mail Server -- 

1. To run the application, download config and app directories

2. Please set the following properties in conf file
	* THREAD_COUNT - (As many threads will be created for SMTP Listener, POP Receiver and Client Listener services)
	* MAIL_SERVER_ROOT_FOLDER - Root folder to store the mails
	* CLIENT_LISTENER_PORT
	* LOCAL_DOMAIN_NAME - Name that will be provided in the email address

3. Proceed to the app directory and execute - RunMailServer.bat to start the Mail Server



-- Mail Client --

It is used for user account creation

1. Proceed to the app directory and execute - RunMailClient.bat to start the Mail Client



