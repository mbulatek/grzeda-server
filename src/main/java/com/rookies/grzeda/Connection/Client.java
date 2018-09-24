package com.rookies.grzeda.Connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import Messaging.Message;
import Messaging.ChatMessage;

interface DataReadyEventListener {
	public void event(Message message);
}

public class Client implements Runnable {
	public DataReadyEventListener eventListener;
	public Socket socket;
	
	ObjectInputStream inFromClient;
	ObjectOutputStream outToClient;
	
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	
	public void run() {
		
		LOGGER.log(Level.INFO, "run(): Entry");
		
		try {
			inFromClient = new ObjectInputStream(socket.getInputStream());
			outToClient = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOGGER.log(Level.INFO, "run(): Entering receiving loop");

		while (socket.isConnected()) {
            Message inMsg = null;
			try {
				inMsg = (Message)inFromClient.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			LOGGER.log(Level.INFO, "run(): Message read");
			
			if (inMsg != null) {
				if (eventListener != null) {
					eventListener.event(inMsg);
				}
			}
		}
		
		LOGGER.log(Level.INFO, "run(): Exit");
	}
	
	public void sendMessage(Message msg) throws IOException {
		LOGGER.log(Level.INFO, "sendMessage(): Entry");
		outToClient.writeObject(msg);
		LOGGER.log(Level.INFO, "sendMessage(): Exit");
	}
}
