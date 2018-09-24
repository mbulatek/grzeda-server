package com.rookies.grzeda.Connection;

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
			System.out.println("Socket connected");

			
			ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());
			
			Message inMsg = null;
            inMsg = (Message)inFromClient.readObject();
            
            if (inMsg.type == Message.Type.initial)
            {
            	LOGGER.log(Level.INFO, "listen(): Initial message received");
            	
    			Client client = new Client();
    			client.socket = clientSocket;
    			
    			Table table = tables.get(inMsg.tableNo);
    			
    			if (table == null) {
    				table = new Table();
    				table.clients.add(client);
    				tables.put(inMsg.tableNo, table);
    			} else {
    				table.clients.add(client);
    			}

    			new Thread(client).start();
            }
		}
	}
}
