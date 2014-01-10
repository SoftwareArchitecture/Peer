package at.ac.tuwien.softwarearchitecture.swazam.peer.api;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.PropertyConfigurator;

import at.ac.tuwien.softwarearchitecture.swazam.peer.management.IPeerManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.management.PeerManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.matching.IMatchingManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.matching.MatchingManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication.IServerCommunicationManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication.ServerCommunicationManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.util.ConfigurationManagement;

/**
 * 
 * Class used to instantiate Peer components, and acts as a backbone for the
 * PeerAPI
 * 
 */
public class PeerControl {

	private ServerCommunicationManager communicationManager;
	private MatchingManager matchingManager;
	private PeerManager peerManager;

	private static PeerControl instance;

	static {

		// initiate Log4J
		// initiate logger
		{
			String date = new Date().toString();
			date = date.replace(" ", "_");
			date = date.replace(":", "_");
			System.getProperties().put("recording_date", date);

			try {
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				// ClassLoader classLoader =
				// Configuration.class.getClassLoader();

				InputStream log4jStream = ConfigurationManagement.getLog4JConfig();

				if (log4jStream != null) {
					PropertyConfigurator.configure(log4jStream);
					log4jStream.close();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		instance = new PeerControl();

	}

	public IServerCommunicationManager getCommunicationManager() {
		return communicationManager;
	}

	public IMatchingManager getMatchingManager() {
		return matchingManager;
	}

	public IPeerManager getPeerManager() {
		return peerManager;
	}

	private PeerControl() {

		// instantiate all objects and their dependencies
		// bad practice. instantiation sequence is crucial here
		peerManager = new PeerManager();
		communicationManager = new ServerCommunicationManager();
		peerManager.setServerCommunicationManager(communicationManager);

		// currently, to respect the sequence diagram
		// when the MatchingManager is instantiated, it instantiates a
		// FingerprintExtractorAndManager which uses the PeerManager to notify
		// the server using the ServerCommunicationManager
		// that a new peer has appeared
		matchingManager = new MatchingManager(peerManager);

		communicationManager.setMatchingManager(matchingManager);

		matchingManager.setCommunicationManager(communicationManager);

		peerManager.setMatchingManager(matchingManager);

	}

	public static PeerControl getInstance() {
		return instance;
	}

}
