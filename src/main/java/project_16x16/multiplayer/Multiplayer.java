package project_16x16.multiplayer;

import java.net.ConnectException;

import processing.data.JSONObject;
import processing.net.Client;
import processing.net.Server;
import project_16x16.SideScroller;

public class Multiplayer {

	/**
	 * A client connects to a server and sends data back and forth.
	 */
	private Client client;
	/**
	 * A server sends and receives data to and from its associated clients (other
	 * programs connected to it)
	 */
	private Server server;
	private JSONObject data;
	private boolean isHost;

	/**
	 * Constructor for a connecting client
	 *
	 * @param player
	 * @param hostIP
	 * @param port
	 */
	public Multiplayer(SideScroller player, String hostIP, int port, boolean isHost) throws java.net.ConnectException {
		this.isHost = isHost;
		data = null;
		if (isHost) {
			server = new Server(player, port);
			if (!server.active()) {
				throw new java.net.ConnectException();
			}
		} else {
			client = new Client(player, hostIP, port);
			if (!client.active()) {
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
	public Multiplayer(SideScroller player, boolean isHost) throws ConnectException {
		this(player, "127.0.0.1", 25565, isHost);
	}

	public JSONObject readData() {
		if (isHost) {
			client = server.available();
		}
		if (client != null && client.available() > 0) {
			String packet = client.readString();
			try {
				data = JSONObject.parse(packet);
			} catch (java.lang.RuntimeException e) {
			}
		}

		return data;
	}

	public void writeData(String packet) {
		if (isHost) {
			server.write(packet); // write to client(s)
		} else {
			if (client.active()) {
				client.write(packet); // write to server
			}
		}

	}

	public void exit() {
		if (client != null) {
			client.stop();
		}
		if (server != null) { // TODO message clients.
			server.stop();
		}

	}

}