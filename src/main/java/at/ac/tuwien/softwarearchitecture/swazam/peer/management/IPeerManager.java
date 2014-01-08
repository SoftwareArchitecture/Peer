package at.ac.tuwien.softwarearchitecture.swazam.peer.management;

import java.util.Collection;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;

import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerFingerprintInformation;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;


public interface IPeerManager {

	public void searchFingerprint(ClientInfo client, Fingerprint fingerprint);

	public void distributeFingerprints(Collection<Fingerprint> peerFingerprints);
	
	public void forwardSearchRequest(ClientInfo clientInfo, Fingerprint fingerprint);
	
	public void updateSuperPeerInfo(PeerInfo superPeerInfo);
	
	public void updateRingInformation(PeerFingerprintInformation fingerprintInformation);
	
	public void performLeaderElection();
	
	

}
