package project_16x16.multiplayer;

import java.util.Arrays;

import processing.data.JSONObject;
import processing.net.*;

import project_16x16.entities.Player;
import project_16x16.scene.GameplayScene;

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
	/**
	 * Representation of the other player
	 */
	private final Player p;
	
	public boolean isHost;

	/**
	 * Constructor for a connecting client
	 * @param host
	 * @param hostIP
	 * @param port
	 */
	public Multiplayer(GameplayScene host, String hostIP, int port) throws java.net.ConnectException {
		c = new Client(host.applet, hostIP, port);
		if (!c.active()) {
			throw new java.net.ConnectException();
		}
		p = new Player(host.applet, host);
	}

	/**
	 * Constructor for a host client
	 * @param host
	 * @param port
	 */
	public Multiplayer(GameplayScene host, int port) {
		s = new Server(host.applet, port);
		p = new Player(host.applet, host);
		isHost = true;
	}

	/**
	 * Called by clients
	 * @param x
	 * @param y
	 * @param name anim name
	 */
	public void writeDataClient(float x, float y, String name) {
		JSONObject data = new JSONObject();
		data.setFloat("x", x);
		data.setFloat("y", y);
		data.setString("anim", name);
		c.write(data.toString());
	}

	/**
	 * Called by clients -- consume server messages.
	 */
	public void readDataClient() {
		if (c.available() > 0) {
			JSONObject data = JSONObject.parse(c.readString());
			p.pos.x = data.getFloat("x");
			p.pos.y = data.getFloat("y");
			p.animation.name = data.getString("anim");
			p.display();
		}
	}

	/**
	 * Called by the host. Writes its data to server.
	 * @param x
	 * @param y
	 * @param name
	 */
	public void writeDataServer(float x, float y, String name) {
		JSONObject data = new JSONObject();
		data.setFloat("x", x);
		data.setFloat("y", y);
		data.setString("anim", name);
		s.write(data.toString());
	}

	/**
	 * Called by host. Reads data from (next available) client
	 */
	public void readDataServer() {
		c = s.available();
		if (c != null) {
			JSONObject data = JSONObject.parse(c.readString());
			p.pos.x = data.getFloat("x");
			p.pos.y = data.getFloat("y");
			p.animation.name = data.getString("anim");
			p.display();
		}
	}
	
	public void exit() {
		if (c!= null) {
			c.stop();
		}
		if (s!= null) {
			Arrays.asList(s.clients).forEach(client -> s.disconnect(client)); // TODO message clients.
			s.stop();
		}
	}
}