package at.ac.tuwien.softwarearchitecture.swazam.peer.management;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;

public interface IPeerManagement {
	
	public void searchFingerprint(ClientInfo client, Fingerprint fingerprint);
	
	public void distributeFingerprintsToServer();

	public void forwardSearchRequest(ClientInfo clientInfo, Fingerprint fingerprint);
	
}
