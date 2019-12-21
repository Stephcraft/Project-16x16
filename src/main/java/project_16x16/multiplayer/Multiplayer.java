package project_16x16.multiplayer;

import java.net.ConnectException;
import java.util.Arrays;

import processing.data.JSONObject;
import processing.net.*;

import project_16x16.SideScroller;
import project_16x16.SideScroller.GameScenes;
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
	 * @param player
	 * @param hostIP
	 * @param port
	 */
	public Multiplayer(SideScroller player, String hostIP, int port, boolean isHost) throws java.net.ConnectException {
		this.isHost = isHost;
		
		if (isHost) {
			s = new Server(player, port);
			if (!s.active()) {
				throw new java.net.ConnectException();
			}
		}
		else {
			c = new Client(player, hostIP, port);
			if (!c.active()) {
				throw new java.net.ConnectException();
			}
		}

		p = new Player(player, (GameplayScene) GameScenes.GAME.getScene(), true);
	}
	
	/**
	 * Use default (LAN) port and host.
	 * @param player
	 * @param isHost
	 * @throws ConnectException 
	 */
	public Multiplayer(SideScroller player, boolean isHost) throws ConnectException {
		this(player, "127.0.0.1", 25565, isHost);
	}
	
	public void readData() {

		if (isHost) {
			c = s.available();
		}

		if (c != null && c.available() > 0) {
			String packet = c.readString();
			try {
				JSONObject data = JSONObject.parse(packet);
				p.pos.x = data.getFloat("x");
				p.pos.y = data.getFloat("y");
				p.setAnimation(data.getString("animSequence"));
				p.animation.setFrame(data.getFloat("animFrame"));
			} catch (java.lang.RuntimeException e) {
			} finally {
				p.display();
			}
		}
	}
	
	public void writeData(float x, float y, String name) {
        JSONObject data = new JSONObject();
        data.setFloat("x", x);
        data.setFloat("y", y);
        data.setString("animSequence", name);
        data.setFloat("animFrame", p.animation.getFrameID());
        
		if (isHost) {
			s.write(data.toString()); // write to client(s)
		}
		else {
	        c.write(data.toString()); // write to server
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