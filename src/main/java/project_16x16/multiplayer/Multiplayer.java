package project_16x16.multiplayer;

import processing.core.PApplet;
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
	private final Player p;
	private String dataReader;

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
		dataReader = "";
		p = new Player(host.applet, host);
	}

	/**
	 * Constructor for a host client
	 * @param host
	 * @param port
	 */
	public Multiplayer(GameplayScene host, int port) {
		s = new Server(host.applet, port);
		dataReader = "";
		p = new Player(host.applet, host);
	}

	public void writeDataClient(float x, float y, String name) {
		c.write(x + " " + y + ":" + name + "\n");
	}

	public void readDataClient() {
		if (c.available() > 0) {
			dataReader = c.readString();
			if (dataReader.indexOf("\n") != -1) {
				dataReader = dataReader.substring(0, dataReader.indexOf("\n"));
			}
			if (dataReader.indexOf(' ') != -1) {
				p.pos.x = PApplet.parseFloat(dataReader.substring(0, dataReader.indexOf(' ')));
			}
			if (dataReader.indexOf(':') != -1) {
				p.pos.y = PApplet
						.parseFloat(dataReader.substring(dataReader.indexOf(' ') + 1, dataReader.indexOf(':')));
			}
			if (dataReader.indexOf("\n") != -1) {
				p.animation.name = dataReader.substring(dataReader.indexOf(':') + 1, dataReader.indexOf('\n'));
			}
			p.display();
		}
	}

	public void writeDataServer(float x, float y, String name) {
		s.write(x + " " + y + ":" + name + "\n");
	}

	public void readDataServer() {
		c = s.available();
		if (c != null) {
			dataReader = c.readString();
			if (dataReader.indexOf("\n") != -1) {
				dataReader = dataReader.substring(0, dataReader.indexOf("\n"));
			}
			if (dataReader.indexOf(' ') != -1) {
				p.pos.x = PApplet.parseFloat(dataReader.substring(0, dataReader.indexOf(' ')));
			}
			if (dataReader.indexOf(':') != -1) {
				p.pos.y = PApplet
						.parseFloat(dataReader.substring(dataReader.indexOf(' ') + 1, dataReader.indexOf(':')));
			}
			if (dataReader.indexOf("\n") != -1) {
				p.animation.name = dataReader.substring(dataReader.indexOf(':') + 1, dataReader.indexOf('\n'));
			}
			p.display();
		}
	}
	
	public void exit() {
		if (c!= null) {
			c.stop();
		}
		if (s!= null) {
			s.stop();
		}
	}
}