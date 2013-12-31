package at.ac.tuwien.softwarearchitecture.swazam.peer.matching;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.peer.management.IPeerManagement;

public interface IMatchingManager {

	/**
	 * Also used in asynchronous connector between the PeerManagement and
	 * Matching Manager for file search to initiate the search on Peer
	 * Management
	 */
	public void matchFile(Fingerprint fingerprint);

	// to be customized according to need
	/**
	 * To be used in asynchronous connector between the PeerManagement and
	 * Matching Manager for file search to notify the Matching Manager by the
	 * Peer Management
	 */
	public void notifySearchResult();

}
