package at.ac.tuwien.softwarearchitecture.swazam.peer.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class NetworkUtil {


	/**
	 * Used in determining if a port is used or not is open or not. Useful in checking if the superPeer is alive
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public boolean checkIfPortOpen(String ip, int port) {
		try (Socket ignored = new Socket(ip, port)) {
			return true;
		} catch (IOException ignored) {
			return false;
		}
	}
}
