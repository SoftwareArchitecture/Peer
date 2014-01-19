package at.ac.tuwien.softwarearchitecture.swazam.peer.matching;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.MusicFileInfo;
import at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager.IFingerprintExtractorAndManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.peerManager.IPeerManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication.IServerCommunicationManager;

public class MatchingManager implements IMatchingManager {

	private IFingerprintExtractorAndManager fingerprintExtractorAndManager;
	private IPeerManager peerManager;
	private IServerCommunicationManager communicationManager;
	
	public MatchingManager(IPeerManager peerManager, IFingerprintExtractorAndManager fingerprintExtractorAndManager) {
		super();
		this.peerManager = peerManager;
		this.fingerprintExtractorAndManager = fingerprintExtractorAndManager;
	}

	/**
	 * The peer management leaves a reference at which will be called back when
	 * the file has been matched
	 * 
	 * @param peerManager
	 * @param fingerprint
	 */
	@Override
	public void matchFile(final ClientInfo clientInfo, final Fingerprint fingerprint) {

		// as this is asynchronous, start a new thread that handles it
		Thread searchThread = new Thread() {

			public void run() {
				
				MusicFileInfo fileInfo = fingerprintExtractorAndManager.evaluateFingerprint(fingerprint);

				// if not found, and if seed, broadcast request to other peers
				// in ring
				if (!fileInfo.isEmpty()) {
					Logger.getLogger(MatchingManager.class).log(Level.INFO, "Search request for Client " + clientInfo.getUsername() + " and sessionkey " + clientInfo.getSessionKey() + " successfull. Notifying Server");
					communicationManager.notifyAboutSearchResult(clientInfo, peerManager.getCurrentPeerInformation(), fileInfo);
				} else {
					Logger.getLogger(MatchingManager.class).log(Level.INFO, "Search request for Client " + clientInfo.getUsername() + " and sessionkey " + clientInfo.getSessionKey() + " will be forward to sub-peer circle (if any)");
					peerManager.forwardSearchRequest(clientInfo, fingerprint);
				}

			}
		};
		
		searchThread.setDaemon(true);
		searchThread.start();

	}

	public IServerCommunicationManager getCommunicationManager() {
		return communicationManager;
	}

	public void setCommunicationManager(IServerCommunicationManager communicationManager) {
		this.communicationManager = communicationManager;
	}

//	/**
//	 * Used in Asynchronous communication. If the communicationManager
//	 * broadcasts the search result to all other PeerManager, it will notify the
//	 * MatchingManager about the result
//	 * 
//	 * @param clientInfo
//	 * @param musicInfo
//	 */
//	@Override
//	public void notifySearchResult(ClientInfo clientInfo, MusicFileInfo musicInfo) {
//		communicationManager.notifyAboutSearchResult(clientInfo, musicInfo);
//	}

}
