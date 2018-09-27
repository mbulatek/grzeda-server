package com.rookies.grzeda.Connection;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import Messaging.Message;
import Messaging.MessageChat;

interface ClientEventListener {
	class Event {
		Client source;
		Message message;
	}
	public void eventDataReady(Event event);
	public void eventTableAssigned(Event event);
	public void eventTableDisassigned(Event event);
	public void eventDisconnected(Event event);
}

public class Client implements Runnable {
	public ClientEventListener eventListener;
	public Socket socket;
	
	public ObjectInputStream inFromClient;
	public ObjectOutputStream outToClient;
	
	int senderId;
	int tableNo;
	
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	
	public Client(Socket socket) {
		this.socket = socket;
		try {
			inFromClient = new ObjectInputStream(socket.getInputStream());
			outToClient = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		LOGGER.log(Level.INFO, "run(): Entry");

		while (true) {
			Message inMsg = null;
			try {
				inMsg = (Message)inFromClient.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				if (e instanceof EOFException) {
					break;
				}
				e.printStackTrace();
			}
	
			LOGGER.log(Level.INFO, "run(): Message read");
			
			if (inMsg != null) {
				
				if (inMsg.type == Message.Type.initial) {
					if (eventListener != null) {
						ClientEventListener.Event event = new ClientEventListener.Event();
						event.source = this;
						event.message = inMsg;
						eventListener.eventTableAssigned(event);
					}
				}
				if (inMsg.type == Message.Type.chat) {
					MessageChat chatMsg = (MessageChat) inMsg;
					if (eventListener != null) {
						ClientEventListener.Event event = new ClientEventListener.Event();
						event.source = this;
						event.message = chatMsg;
						eventListener.eventDataReady(event);
					}
				}
			}
		}

		if (eventListener != null) {
			ClientEventListener.Event event = new ClientEventListener.Event();
			event.source = this;
			event.message = new Message();
				
			eventListener.eventTableDisassigned(new ClientEventListener.Event() {{
				source = this;
				message = new Message();
				//message.tableNo = 
			}});
		}
		
		if (eventListener != null) {
			ClientEventListener.Event event = new ClientEventListener.Event();
			event.source = this;
			eventListener.eventDisconnected(event);
		}
		
		LOGGER.log(Level.INFO, "run(): Exit");
	}
	
	public void sendMessage(Message msg) {
		LOGGER.log(Level.INFO, "sendMessage(): Entry");
		try {
			outToClient.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.log(Level.INFO, "sendMessage(): Exit");
	}
}
