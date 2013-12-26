package at.ac.tuwien.softwarearchitecture.swazam.peer.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SocketUtil {

	/**
	 * Tests if a socket already exists at the specified IP and port, and if
	 * yes, increments the port until it find a free one, and then opens a
	 * ServerSocket
	 * 
	 * @param ip
	 *            used in testing if a socket on the specified IP and port
	 *            already exists
	 * @param port
	 *            at which to open the socket
	 * @return ServerSocket handling Peer API communication
	 */
	public ServerSocket openPeerAPISocket(String ip, int port) {

		// test if port is not already used, and if so, try another port
		while (true) {
			try {
				Socket ignored = new Socket(ip, port);
				ignored.close();
			} catch (IOException ignored) {
				break;
			}
			// if it reaches here, it means socket was open
			port++;
		}

		try {
			ServerSocket serverSocket = new ServerSocket(port);
			return serverSocket;
		} catch (IOException e) {
			Logger.getLogger(SocketUtil.class).log(Level.ERROR, e);
		}

		return null;

	}

	/**
	 * Used in determining if a socket is open or not. Useful in checking if the superPeer is alive
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public boolean checkIfSocketOpen(String ip, int port) {
		try (Socket ignored = new Socket(ip, port)) {
			return true;
		} catch (IOException ignored) {
			return false;
		}
	}
}
