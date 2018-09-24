package com.rookies.grzeda.Connection;

import java.io.IOException;
import java.util.LinkedList;

import Messaging.Message;

public class Table {
	LinkedList<Client> clients;
	
	public Table() {
		clients = new LinkedList<Client>();
	}
	
	void addClient(Client client) {
		client.eventListener = new DataReadyEventListener() {
			public void event(Message message) {
				for (Client client : clients) {
					try {
						client.sendMessage(message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		clients.add(client);
	}
}
