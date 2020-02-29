package project_16x16.multiplayer;

import java.net.ConnectException;
import processing.data.JSONObject;
import processing.net.*;

import project_16x16.Main;

public class Multiplayer {

	/**
	 * A client connects to a server and sends data back and forth.
	 */
	private Client c;
	/**
	 * A server sends and receives data to and from its associated clients (other
	 * programs connected to it)
	 */
	private Server s;

	JSONObject data;
	public boolean isHost;

	/**
	 * Constructor for a connecting client
	 * 
	 * @param player
	 * @param hostIP
	 * @param port
	 */
	public Multiplayer(Main player, String hostIP, int port, boolean isHost) throws java.net.ConnectException {
		this.isHost = isHost;
		data = null;
		if (isHost) {
			s = new Server(player, port);
			if (!s.active()) {
				throw new java.net.ConnectException();
			}
		} else {
			c = new Client(player, hostIP, port);
			if (!c.active()) {
				throw new java.net.ConnectException();
			}
		}
	}

	/**
	 * Use default (LAN) port and host.
	 * 
	 * @param player
	 * @param isHost
	 * @throws ConnectException
	 */
	public Multiplayer(Main player, boolean isHost) throws ConnectException {
		this(player, "127.0.0.1", 25565, isHost);
	}

	public JSONObject readData() {

		if (isHost) {
			c = s.available();
		}

		if (c != null && c.available() > 0) {
			String packet = c.readString();
			try {
				data = JSONObject.parse(packet);
			} catch (java.lang.RuntimeException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public void writeData(String packet) {
		if (isHost) {
			s.write(packet); // write to client(s)
		} else {
			if (c.active()) {
				c.write(packet); // write to server
			}
		}
	}

	public void exit() {
		if (c != null) {
			c.stop();
		}
		if (s != null) { // TODO message clients.
			s.stop();
		}
	}
}