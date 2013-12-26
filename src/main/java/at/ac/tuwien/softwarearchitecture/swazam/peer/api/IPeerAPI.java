package at.ac.tuwien.softwarearchitecture.swazam.peer.api;

import java.util.UUID;

import at.ac.tuwien.softwarearchitecture.swazam.common.infos.PeerInfo;

public interface IPeerAPI {
	/**
	 * Updates the superPeerIf of a normal Peer
	 * @param peerID the peerID of a super peer candidate
	 */
	public void updateSuperPeer(UUID peerID);
	
	public void notifyServerIAmSuperPeer();
	
	public void joinRing(PeerInfo peerInfo);
	
}
