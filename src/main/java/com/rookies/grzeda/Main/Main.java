package com.rookies.grzeda.Main;

import java.io.IOException;

import com.rookies.grzeda.Connection.Server;

public class Main {

	public static void main(String[] args) {
		Server server = new Server();
		
		try {
			server.listen();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
