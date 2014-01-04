package at.ac.tuwien.softwarearchitecture.swazam.peer.management;

import java.util.Collection;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;

public interface IPeerManager {

	public void searchFingerprint(ClientInfo client, Fingerprint fingerprint);

	public void distributeFingerprints(Collection<Fingerprint> peerFingerprints);
	
	public void forwardSearchRequest(ClientInfo clientInfo, Fingerprint fingerprint);

}
