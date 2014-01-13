package at.ac.tuwien.softwarearchitecture.swazam.peer.management;

import java.util.Collection;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerFingerprintInformation;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerRingInformation;


public interface IPeerManager {

	public void searchFingerprint(ClientInfo client, Fingerprint fingerprint);

	public void joinPeerNetwork();
	
	public void registerToServer();
	
	public void forwardSearchRequest(ClientInfo clientInfo, Fingerprint fingerprint);
	
	public void broadcastSuperPeerInfoHeartBeat();
	
	//used by SuperPeer to notify the peers that it is alive, and what is the current peer structure
	public void updateRingInformation(PeerRingInformation peerRingInformation);
	
	/**
	 * Used when a peer sends its fingerprints to the SuperPeer
	 * @param fingerprintInformation
	 */
	public void updatePeerInformation(PeerFingerprintInformation fingerprintInformation);
	
	public PeerInfo getCurrentPeerInformation();
	
	public void performLeaderElection();
	
	

}
