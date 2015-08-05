package jp.co.mti.trn.ojt.http.server.tanaka_ky;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import jp.co.mti.trn.ojt.http.server.tanaka_ky.Server;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class ServerTest extends TestCase {
	int PORT = 8080;
	ServerSocket serverSocket;
	Socket socket;
	Server server;

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		server = new Server();
		serverSocket = new ServerSocket(PORT);
	}

	@Test
	public void testCheackRequest() throws IOException {

		socket = serverSocket.accept();
		assertEquals("/index.html", server.cheackRequest(socket));
		server.createResponse(socket, "/index.html");
		socket.close();
		serverSocket.close();

	}

}
