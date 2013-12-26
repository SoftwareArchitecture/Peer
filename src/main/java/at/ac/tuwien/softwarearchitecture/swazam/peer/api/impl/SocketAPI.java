package at.ac.tuwien.softwarearchitecture.swazam.peer.api.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.peerCommunication.AbstractPeerCommand;
import at.ac.tuwien.softwarearchitecture.swazam.common.peerCommunication.impl.JoinRingCommand;
import at.ac.tuwien.softwarearchitecture.swazam.common.peerCommunication.impl.SearchFingerprintCommand;
import at.ac.tuwien.softwarearchitecture.swazam.common.peerCommunication.impl.SetSuperPeerIDCommand;
import at.ac.tuwien.softwarearchitecture.swazam.common.peerCommunication.impl.UpdateSuperPeerCommand;
import at.ac.tuwien.softwarearchitecture.swazam.peer.Peer;
import at.ac.tuwien.softwarearchitecture.swazam.peer.api.IPeerAPI;

public class SocketAPI implements IPeerAPI {

	// peer on which this API is attached
	private Peer peer;
	private ServerSocket serverSocket;
	private Thread apiThread;

	public SocketAPI(Peer p, ServerSocket socket) {
		super();
		this.peer = p;
		this.serverSocket = socket;

		apiThread = new Thread() {
			public void run() {
				// Continuously wait for commands, and execute them
				while (!this.isInterrupted()) {
					Socket socket;
					try {
						// wait for something to be received by the socket
						socket = serverSocket.accept();

						// the socket should receive objects of type
						// AbstractPeerCommand
						ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
						AbstractPeerCommand command = (AbstractPeerCommand) inputStream.readObject();

						// process command based on its type
						switch (command.getType()) {

						case SEARCH_FINGERPRINT:
							SearchFingerprintCommand searchFingerprintCommand = (SearchFingerprintCommand) command;
							throw new UnsupportedOperationException("Not implemented");
							// break;

						case SET_SUPER_PEER:
							SetSuperPeerIDCommand setSuperPeerIDCommand = (SetSuperPeerIDCommand) command;
							
							//send to Super Peer command to join ring
							
							
							throw new UnsupportedOperationException("Not implemented");
							// break;

						case UPDATE_SUPER_PEER:
							UpdateSuperPeerCommand updateSuperPeerCommand = (UpdateSuperPeerCommand) command;
							updateSuperPeer(updateSuperPeerCommand.getSuperPeerID());
							
							break;

						case JOIN_RING:
							JoinRingCommand joinRingCommand = (JoinRingCommand) command;
							joinRing(joinRingCommand.getInfo());
							
							//send back on the same socket the Peers in the ring
							ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
							objectOutputStream.writeObject(peer.getPeerRing());
							objectOutputStream.close();
							break;
						}

						inputStream.close();
						socket.close();
						
					} catch (IOException e) {
						Logger.getLogger(SocketAPI.class).log(Level.ERROR, e);
					} catch (ClassNotFoundException e) {
						Logger.getLogger(SocketAPI.class).log(Level.ERROR, e);
					} 
				}
			}
		};

		// when shutting down the Peer, shut down the socket API
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					serverSocket.close();
				} catch (IOException e) {
					Logger.getLogger(SocketAPI.class).log(Level.ERROR, e);
				}
				apiThread.interrupt();
			}
		});

		apiThread.start();
	}

	@Override
	public synchronized void updateSuperPeer(UUID peerID) {
		if (peer.getSuperPeerID() == null) {
			peer.setSuperPeerID(peerID);
		} else if (peer.getSuperPeerID().compareTo(peerID) < 0) {
			peer.setSuperPeerID(peerID);
		}
		// check if current Peer is superPeer, and in this case notify server
		if (peer.getSuperPeerID().equals(peer.getPeerID())) {
			notifyServerIAmSuperPeer();
		}
	}

	@Override
	public synchronized void notifyServerIAmSuperPeer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void joinRing(PeerInfo peerInfo) {
		peer.addPeer(peerInfo);
	}

}
