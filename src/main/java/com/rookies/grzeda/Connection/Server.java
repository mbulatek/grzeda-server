package com.rookies.grzeda.Connection;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import Messaging.Message;

public class Server {
	
	ServerSocket serverSocket;
	HashMap<Integer, Table> tables;
	
	private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
	
	public void listen() throws IOException, ClassNotFoundException {
		try {
			serverSocket = new ServerSocket(8008);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tables = new HashMap<Integer, Table>();
		
		while (true) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			LOGGER.log(Level.INFO, "listen(): Socket connected");

			Client client = new Client(clientSocket);
			client.eventListener = new ClientEventListener() {
				@Override
				public void eventDataReady(ClientEventListener.Event event) {
					LOGGER.log(Level.INFO, "listen(): DataReady handling");
					Table table = tables.get(event.message.tableNo);
					if (table != null) {
						if (event.message.type == Message.Type.chat) {
							for (Client client : table.clients) {
								client.sendMessage(event.message);
							}
						}
					}
				}
				
				@Override
				public void eventTableAssigned(ClientEventListener.Event event) {
					LOGGER.log(Level.INFO, "listen(): TableAssigned handling");
	    			Table table = tables.get(event.message.tableNo);
	    			
	    			if (table == null) {
	    				table = new Table();
	    				table.clients.add(event.source);
	    				tables.put(event.message.tableNo, table);
	    			} else {
	    				table.clients.add(event.source);
	    			}
				}

				@Override
				public void eventTableDisassigned(ClientEventListener.Event event) {
					LOGGER.log(Level.INFO, "listen(): TableAssigned handling");
	    			Table table = tables.get(event.message.tableNo);

	    			if (table != null) {
	    				table.clients.remove(event.source);
	    			}
				}

				@Override
				public void eventDisconnected(ClientEventListener.Event event) {
					LOGGER.log(Level.INFO, "listen(): Disconnected handling");
				}
			};
			
			new Thread(client).start();
			LOGGER.log(Level.INFO, "listen(): Client started");
		}
	}
}
