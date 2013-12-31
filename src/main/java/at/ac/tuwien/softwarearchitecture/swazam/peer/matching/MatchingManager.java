package at.ac.tuwien.softwarearchitecture.swazam.peer.matching;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.peer.fingerprintExtractorAndManager.FingerprintExtractorAndManager;
import at.ac.tuwien.softwarearchitecture.swazam.peer.management.IPeerManagement;
import at.ac.tuwien.softwarearchitecture.swazam.peer.management.PeerManagement;

public class MatchingManager implements IMatchingManager{

	private FingerprintExtractorAndManager fingerprintExtractorAndManager;
	private IPeerManagement management;
	
	/**
	 * The peer management leaves a reference at which will be called back when the file has been matched
	 * @param peerManagement
	 * @param fingerprint
	 */
	@Override
	public void matchFile(Fingerprint fingerprint) {
		management.initiateFingerprintSearch(this, fingerprint);
	}

	@Override
	public void notifySearchResult() {
		// TODO Auto-generated method stub
		
	}
	
	

}
