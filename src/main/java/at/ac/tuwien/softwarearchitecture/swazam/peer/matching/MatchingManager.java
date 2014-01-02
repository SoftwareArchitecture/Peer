package at.ac.tuwien.softwarearchitecture.swazam.peer.matching;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.MusicFileInfo;
import at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager.FingerprintExtractorAndManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.management.IPeerManagement;
import at.ac.tuwien.softwarearchitecture.swazam.peer.management.PeerManagement;
import at.ac.tuwien.softwarearchitecture.swazam.peer.serverCommunication.IServerCommunicationManager;

public class MatchingManager implements IMatchingManager {

	private FingerprintExtractorAndManager fingerprintExtractorAndManager;
	private IPeerManagement management;
	private IServerCommunicationManager communicationManager;

	/**
	 * The peer management leaves a reference at which will be called back when
	 * the file has been matched
	 * 
	 * @param peerManagement
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
					communicationManager.notifyAboutSearchResult(clientInfo, fileInfo);
				} else {
					management.forwardSearchRequest(clientInfo, fingerprint);
				}

			}
		};
		
		searchThread.setDaemon(true);
		searchThread.start();

	}

	/**
	 * Used in Asynchronous communication. If the communicationManager
	 * broadcasts the search result to all other PeerManager, it will notify the
	 * MatchingManager about the result
	 * 
	 * @param clientInfo
	 * @param musicInfo
	 */
	@Override
	public void notifySearchResult(ClientInfo clientInfo, MusicFileInfo musicInfo) {
		communicationManager.notifyAboutSearchResult(clientInfo, musicInfo);
	}

}
