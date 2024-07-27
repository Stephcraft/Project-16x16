package project_16x16.multiplayer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.ConnectException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import processing.data.JSONObject;
import processing.net.Client;
import processing.net.Server;
import project_16x16.SideScroller;

@ExtendWith(MockitoExtension.class)
class MultiplayerTest {

	@Mock
	private SideScroller player;

	@Test
	void callingConstructorAsServer_ok() {
		ConnectException ce = null;
		try {
			new Multiplayer(player, false);
		}
		catch (ConnectException e) {
			ce = e;
		}

		assertNull(ce);
	}

	@Test
	void callingConstructorAsClient_ok() {
		ConnectException ce = null;
		try {
			new Multiplayer(player, true);
		}
		catch (ConnectException e) {
			ce = e;
		}

		assertNull(ce);
	}

	@Test
	void callingConstructorAsServer_raisesException() {
		ConnectException ce = null;
		try (MockedConstruction<Server> mocked = mockConstruction(Server.class, (mock, context) -> {
			when(mock.active()).thenReturn(false);
		})) {
			try {
				new Multiplayer(player, true);
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNotNull(ce);
		}
	}

	@Test
	void callingConstructorAsClient_raisesException() {
		ConnectException ce = null;
		try (MockedConstruction<Client> mocked = mockConstruction(Client.class, (mock, context) -> {
			when(mock.active()).thenReturn(false);
		})) {
			try {
				new Multiplayer(player, false);
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNotNull(ce);
		}
	}

	@Test
	void callingReadDataAsServerWithNoClient_returnsNullData() {
		ConnectException ce = null;
		JSONObject data = null;
		try (MockedConstruction<Server> mocked = mockConstruction(Server.class, (mock, context) -> {
			when(mock.active()).thenReturn(true);
			when(mock.available()).thenReturn(null);
		})) {
			try {
				Multiplayer multiplayer = new Multiplayer(player, true);
				data = multiplayer.readData();
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNull(ce);
			assertNull(data);
		}
	}

	@Test
	void callingReadDataAsServerWithClient_returnsData() {
		ConnectException ce = null;
		JSONObject data = null;

		Client client = mock(Client.class);
		when(client.available()).thenReturn(1);
		when(client.readString()).thenReturn("{}");
		try (MockedConstruction<Server> mocked = mockConstruction(Server.class, (mock, context) -> {
			when(mock.active()).thenReturn(true);
			when(mock.available()).thenReturn(client);
		})) {
			try {
				Multiplayer multiplayer = new Multiplayer(player, true);
				data = multiplayer.readData();
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNull(ce);
			assertNotNull(data);
			assertTrue(data instanceof JSONObject);
		}
	}

	@Test
	void callingReadDataAsClientWithNoAvailableData_returnsNullData() {
		ConnectException ce = null;
		JSONObject data = null;
		try (MockedConstruction<Client> client = mockConstruction(Client.class, (mock, context) -> {
			when(mock.active()).thenReturn(true);
			when(mock.available()).thenReturn(0);
		})) {
			try {
				Multiplayer multiplayer = new Multiplayer(player, false);
				data = multiplayer.readData();
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNull(ce);
			assertNull(data);
		}
	}

	@Test
	void callingReadDataAsClient_returnsData() {
		ConnectException ce = null;
		JSONObject data = null;
		try (MockedConstruction<Client> client = mockConstruction(Client.class, (mock, context) -> {
			when(mock.active()).thenReturn(true);
			when(mock.available()).thenReturn(1);
			when(mock.readString()).thenReturn("{}");
		})) {
			try {
				Multiplayer multiplayer = new Multiplayer(player, false);
				data = multiplayer.readData();
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNull(ce);
			assertNotNull(data);
			assertTrue(data instanceof JSONObject);
		}
	}

	@Test
	void callingWriteDataAsServer() {
		ConnectException ce = null;
		try (MockedConstruction<Server> mocked = mockConstruction(Server.class, (mock, context) -> {
			when(mock.active()).thenReturn(true);
		})) {
			try {
				Multiplayer multiplayer = new Multiplayer(player, true);
				multiplayer.writeData("");

				assertFalse(mocked.constructed().isEmpty());
				verify(mocked.constructed().get(0), times(1)).write("");
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNull(ce);
		}
	}

	@Test
	void callingWriteDataAsClient() {
		ConnectException ce = null;
		try (MockedConstruction<Client> mocked = mockConstruction(Client.class, (mock, context) -> {
			when(mock.active()).thenReturn(true);
		})) {
			try {
				Multiplayer multiplayer = new Multiplayer(player, false);
				multiplayer.writeData("");

				assertFalse(mocked.constructed().isEmpty());
				verify(mocked.constructed().get(0), times(1)).write("");
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNull(ce);
		}
	}

	@Test
	void callingWriteDataAsClientNotActive_doNotWrites() {
		ConnectException ce = null;
		try (MockedConstruction<Client> mocked = mockConstruction(Client.class, (mock, context) -> {
			when(mock.active()).thenReturn(true);
		})) {
			try {
				Multiplayer multiplayer = new Multiplayer(player, false);

				assertFalse(mocked.constructed().isEmpty());
				when(mocked.constructed().get(0).active()).thenReturn(false);
				multiplayer.writeData("");
				verify(mocked.constructed().get(0), times(0)).write("");
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNull(ce);
		}
	}

	@Test
	void callingExitAsServer() {
		ConnectException ce = null;
		try (MockedConstruction<Server> mocked = mockConstruction(Server.class, (mock, context) -> {
			when(mock.active()).thenReturn(true);
		})) {
			try {
				Multiplayer multiplayer = new Multiplayer(player, true);
				multiplayer.exit();

				assertFalse(mocked.constructed().isEmpty());
				verify(mocked.constructed().get(0), times(1)).stop();
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNull(ce);
		}
	}

	@Test
	void callingExitAsClient() {
		ConnectException ce = null;
		try (MockedConstruction<Client> mocked = mockConstruction(Client.class, (mock, context) -> {
			when(mock.active()).thenReturn(true);
		})) {
			try {
				Multiplayer multiplayer = new Multiplayer(player, false);
				multiplayer.exit();

				assertFalse(mocked.constructed().isEmpty());
				verify(mocked.constructed().get(0), times(1)).stop();
			}
			catch (ConnectException e) {
				ce = e;
			}

			assertNull(ce);
		}
	}

}
