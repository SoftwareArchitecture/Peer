package at.ac.tuwien.softwarearchitecture.swazam.peer.management;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.peer.matching.IMatchingManager;

public interface IPeerManagement {
	
	public void initiateFingerprintSearch(IMatchingManager matchingManager, Fingerprint fingerprint);
	public void distributeFingerprintsToServer();
	
	
}
